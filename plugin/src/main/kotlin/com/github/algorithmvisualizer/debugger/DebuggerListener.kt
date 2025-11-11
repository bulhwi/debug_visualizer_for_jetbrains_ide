package com.github.algorithmvisualizer.debugger

import com.github.algorithmvisualizer.collectors.SnapshotCollector
import com.github.algorithmvisualizer.detectors.AlgorithmDetector
import com.github.algorithmvisualizer.detectors.SortAlgorithm
import com.intellij.debugger.engine.SuspendContextImpl
import com.intellij.xdebugger.XDebugSession
import com.sun.jdi.*

/**
 * 디버거 이벤트를 감지하고 자동으로 스냅샷을 수집하는 리스너
 *
 * @property debugSession 디버그 세션
 */
class DebuggerListener(private val debugSession: XDebugSession) {
    private var enabled = true
    private var autoCapture = false
    private var snapshotCollector: SnapshotCollector? = null
    private var algorithmDetector: AlgorithmDetector? = null
    private var detectedAlgorithm: SortAlgorithm? = null
    private var stepCallback: (() -> Unit)? = null

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
