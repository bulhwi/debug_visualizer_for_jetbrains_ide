package com.github.algorithmvisualizer.debugger

import com.github.algorithmvisualizer.collectors.SnapshotCollector
import com.github.algorithmvisualizer.detectors.AlgorithmDetector
import com.github.algorithmvisualizer.detectors.SortAlgorithm
import com.intellij.debugger.engine.SuspendContextImpl
import com.intellij.openapi.diagnostic.Logger
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.frame.XStackFrame
import com.sun.jdi.*
import javax.swing.SwingUtilities

/**
 * 디버거 이벤트를 감지하고 자동으로 스냅샷을 수집하는 리스너
 *
 * @property debugSession 디버그 세션
 */
class DebuggerListener(private val debugSession: XDebugSession) {
    private val logger = Logger.getInstance(DebuggerListener::class.java)
    private var enabled = true
    private var autoCapture = false
    private var snapshotCollector: SnapshotCollector? = null
    private var algorithmDetector: AlgorithmDetector? = null
    private var detectedAlgorithm: SortAlgorithm? = null
    private var stepCallback: (() -> Unit)? = null

    init {
        // XDebugSession에 리스너 등록
        debugSession.addSessionListener(object : XDebugSessionListener {
            override fun sessionPaused() {
                // 디버거가 suspend(일시정지)될 때마다 호출됨
                if (enabled && autoCapture) {
                    onSuspend()
                }
            }
        })
    }

    /**
     * 리스너 활성화 상태 확인
     */
    fun isEnabled(): Boolean = enabled

    /**
     * 리스너 활성화
     */
    fun enable() {
        enabled = true
    }

    /**
     * 리스너 비활성화
     */
    fun disable() {
        enabled = false
    }

    /**
     * 자동 캡처 활성화
     */
    fun enableAutoCapture() {
        autoCapture = true
    }

    /**
     * 자동 캡처 비활성화
     */
    fun disableAutoCapture() {
        autoCapture = false
    }

    /**
     * 스냅샷 수집기 설정
     */
    fun setSnapshotCollector(collector: SnapshotCollector) {
        this.snapshotCollector = collector
    }

    /**
     * 알고리즘 감지기 설정
     */
    fun setAlgorithmDetector(detector: AlgorithmDetector) {
        this.algorithmDetector = detector
    }

    /**
     * 감지된 알고리즘 설정
     */
    fun setDetectedAlgorithm(algorithm: SortAlgorithm) {
        this.detectedAlgorithm = algorithm
    }

    /**
     * 감지된 알고리즘 반환
     */
    fun getDetectedAlgorithm(): SortAlgorithm? = detectedAlgorithm

    /**
     * 스텝 이벤트 콜백 등록
     */
    fun onStepEvent(callback: () -> Unit) {
        this.stepCallback = callback
    }

    /**
     * 스텝 이벤트 처리
     *
     * 디버거가 한 줄씩 실행될 때마다 호출됩니다.
     */
    fun onStep(suspendContext: SuspendContextImpl) {
        if (!enabled) return

        // 콜백 실행
        stepCallback?.invoke()

        // 자동 캡처가 활성화되어 있고, 스냅샷 수집기가 설정되어 있으면
        if (autoCapture && snapshotCollector != null) {
            captureSnapshot(suspendContext)
        }

        // 알고리즘 감지기가 설정되어 있으면 알고리즘 자동 감지
        if (algorithmDetector != null && detectedAlgorithm == null) {
            detectAlgorithm(suspendContext)
        }
    }

    /**
     * 현재 메서드명 반환
     */
    fun getCurrentMethodName(suspendContext: SuspendContextImpl): String? {
        return try {
            val frameProxy = suspendContext.frameProxy ?: return null
            val location = frameProxy.location()
            location.method().name()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 스코프 내 배열 변수 찾기
     */
    fun findArrayVariables(suspendContext: SuspendContextImpl): List<String> {
        return try {
            val frameProxy = suspendContext.frameProxy ?: return emptyList()
            val location = frameProxy.location()
            val method = location.method()

            method.variables()
                .filter { it.type() is ArrayType }
                .map { it.name() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 세션 종료 시 상태 초기화
     */
    fun onSessionEnd() {
        detectedAlgorithm = null
        autoCapture = false
        snapshotCollector = null
    }

    /**
     * 디버거 suspend 이벤트 처리 (실제 스텝 이벤트)
     */
    private fun onSuspend() {
        logger.warn("🔔 [DebuggerListener] onSuspend() called - enabled=$enabled, autoCapture=$autoCapture")

        SwingUtilities.invokeLater {
            try {
                val stackFrame = debugSession.currentStackFrame
                if (stackFrame == null) {
                    logger.warn("❌ [DebuggerListener] No current stack frame")
                    return@invokeLater
                }

                logger.warn("✅ [DebuggerListener] Stack frame available")

                // 알고리즘 감지 (아직 감지되지 않았으면)
                if (algorithmDetector != null && detectedAlgorithm == null) {
                    logger.warn("🔍 [DebuggerListener] Detecting algorithm...")
                    detectAlgorithmFromStackFrame(stackFrame)
                    logger.warn("🎯 [DebuggerListener] Algorithm detected: $detectedAlgorithm")
                }

                // 스냅샷 수집 (수집 완료 후 자동으로 callback 호출됨)
                if (snapshotCollector != null && autoCapture) {
                    logger.warn("📸 [DebuggerListener] Starting snapshot collection...")
                    captureSnapshotFromStackFrame(stackFrame)
                } else {
                    logger.warn("⚠️ [DebuggerListener] Snapshot collection skipped (collector=$snapshotCollector, autoCapture=$autoCapture)")
                    // 자동 캡처가 비활성화된 경우에만 즉시 콜백 호출
                    stepCallback?.invoke()
                }
            } catch (e: Exception) {
                logger.warn("❌ [DebuggerListener] Error in onSuspend: ${e.message}", e)
            }
        }
    }

    /**
     * 스택 프레임에서 알고리즘 감지
     *
     * 실제 소스 코드를 읽어서 패턴 분석으로 알고리즘 감지
     */
    private fun detectAlgorithmFromStackFrame(stackFrame: XStackFrame) {
        try {
            val sourcePosition = stackFrame.sourcePosition ?: return
            val file = sourcePosition.file

            // 소스 파일 전체 읽기
            val sourceCode = try {
                val inputStream = file.inputStream
                inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                return
            }

            // 코드 패턴으로 알고리즘 감지
            val result = algorithmDetector?.detectFromCode(sourceCode) ?: return

            // 신뢰도가 0.5 이상이면 알고리즘 확정
            if (result.confidence >= 0.5) {
                detectedAlgorithm = result.algorithm
                // SnapshotCollector에도 알고리즘 설정
                snapshotCollector?.setAlgorithm(result.algorithm.name)
            }
        } catch (e: Exception) {
            // 에러 무시
        }
    }

    /**
     * 스택 프레임에서 스냅샷 수집
     *
     * ExpressionEvaluator를 사용하여 배열 변수를 자동으로 평가하고 스냅샷 수집
     */
    private fun captureSnapshotFromStackFrame(stackFrame: XStackFrame) {
        try {
            val evaluator = ExpressionEvaluator()

            // 일반적인 배열 변수명 목록
            val commonArrayNames = listOf("arr", "array", "nums", "data", "values")

            // 각 변수명을 순차적으로 시도
            tryEvaluateArrayVariable(evaluator, commonArrayNames, 0)
        } catch (e: Exception) {
            // ExpressionEvaluator 사용 실패 시 무시
        }
    }

    /**
     * 배열 변수를 순차적으로 평가 (재귀적으로 시도)
     */
    private fun tryEvaluateArrayVariable(evaluator: ExpressionEvaluator, arrayNames: List<String>, index: Int) {
        if (index >= arrayNames.size) {
            logger.warn("⚠️ [DebuggerListener] All array names tried, none found")
            return // 모든 변수명 시도 완료
        }

        val arrayName = arrayNames[index]
        logger.warn("🔎 [DebuggerListener] Trying to evaluate variable: '$arrayName' (${index + 1}/${arrayNames.size})")

        evaluator.evaluateAndExtract(debugSession, arrayName)
            .thenAccept { (value, type) ->
                logger.warn("📦 [DebuggerListener] Evaluation result - value=$value, type=$type")

                if (value != null && type?.contains("[]") == true) {
                    // 배열 값 파싱 ("[1, 2, 3]" 또는 "{1, 2, 3}" 형식)
                    try {
                        val cleanedValue = value
                            .replace("[", "")
                            .replace("]", "")
                            .replace("{", "")
                            .replace("}", "")

                        val arrayValues = cleanedValue
                            .split(",")
                            .map { it.trim().toIntOrNull() }
                            .filterNotNull()
                            .toIntArray()

                        logger.warn("🎯 [DebuggerListener] Parsed array: ${arrayValues.contentToString()}")

                        if (arrayValues.isNotEmpty()) {
                            snapshotCollector?.captureSnapshot(arrayValues)
                            val count = snapshotCollector?.getSnapshotCount() ?: 0
                            logger.warn("✅ [DebuggerListener] Snapshot collected! Total snapshots: $count")

                            // 스냅샷이 수집되었으므로 콜백 호출 (시각화 트리거)
                            SwingUtilities.invokeLater {
                                logger.warn("🔔 [DebuggerListener] Invoking stepCallback after snapshot")
                                stepCallback?.invoke()
                            }

                            // 성공했으므로 더 이상 시도하지 않음
                            return@thenAccept
                        }
                    } catch (e: Exception) {
                        logger.warn("❌ [DebuggerListener] Array parsing failed: ${e.message}")
                        // 파싱 실패는 무시하고 다음 변수명 시도
                    }
                }

                // 평가 실패 또는 배열이 아닌 경우 다음 변수명 시도
                logger.warn("➡️ [DebuggerListener] Trying next variable name...")
                tryEvaluateArrayVariable(evaluator, arrayNames, index + 1)
            }
            .exceptionally {
                logger.warn("❌ [DebuggerListener] Evaluation failed for '$arrayName': ${it.message}")
                // 평가 실패 시 다음 변수명 시도
                tryEvaluateArrayVariable(evaluator, arrayNames, index + 1)
                null
            }
    }

    /**
     * 스냅샷 캡처 (내부 메서드)
     */
    private fun captureSnapshot(suspendContext: SuspendContextImpl) {
        try {
            val frameProxy = suspendContext.frameProxy ?: return
            val threadProxy = frameProxy.threadProxy()
            val stackFrame = frameProxy.stackFrame

            // 로컬 변수 중 배열 타입 찾기
            val visibleVariables = stackFrame.visibleVariables().filter { it.type() is ArrayType }
            if (visibleVariables.isEmpty()) return

            // 첫 번째 배열 변수의 값 읽기
            val arrayVar = visibleVariables.first()
            val arrayRef = stackFrame.getValue(arrayVar) as? ArrayReference ?: return

            // 배열 데이터 추출
            val length = arrayRef.length()
            val values = arrayRef.getValues(0, length)
            val intArray = values.mapNotNull { (it as? IntegerValue)?.value() }.toIntArray()

            // 스냅샷 수집
            snapshotCollector?.captureSnapshot(intArray)
        } catch (e: Exception) {
            // 에러 무시 (디버깅 중 일시적 오류 가능)
        }
    }

    /**
     * 알고리즘 감지 (내부 메서드)
     */
    private fun detectAlgorithm(suspendContext: SuspendContextImpl) {
        try {
            val frameProxy = suspendContext.frameProxy ?: return
            val location = frameProxy.location()
            val method = location.method()
            val methodName = method.name()

            // 메서드명으로 알고리즘 감지
            val result = algorithmDetector?.detectFromMethodName(methodName) ?: return

            if (result.confidence > 0.8) {
                detectedAlgorithm = result.algorithm
            }
        } catch (e: Exception) {
            // 에러 무시
        }
    }
}
