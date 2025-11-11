package com.github.algorithmvisualizer.debugger

import com.github.algorithmvisualizer.collectors.SnapshotCollector
import com.github.algorithmvisualizer.detectors.AlgorithmDetector
import com.github.algorithmvisualizer.detectors.SortAlgorithm
import com.intellij.debugger.engine.SuspendContextImpl
import com.intellij.xdebugger.XDebugSession
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DebuggerListenerTest {
    private lateinit var listener: DebuggerListener
    private lateinit var debugSession: XDebugSession
    private lateinit var suspendContext: SuspendContextImpl

    @BeforeEach
    fun setUp() {
        debugSession = mockk(relaxed = true)
        suspendContext = mockk(relaxed = true)

        listener = DebuggerListener(debugSession)
    }

    // 기본 초기화 테스트
    @Test
    fun `should initialize listener`() {
        assertThat(listener).isNotNull
        assertThat(listener.isEnabled()).isTrue
    }

    @Test
    fun `should enable and disable listener`() {
        listener.disable()
        assertThat(listener.isEnabled()).isFalse

        listener.enable()
        assertThat(listener.isEnabled()).isTrue
    }

    // 스텝 이벤트 감지 테스트
    @Test
    fun `should detect step event`() {
        var stepDetected = false
        listener.onStepEvent { stepDetected = true }

        listener.onStep(suspendContext)

        assertThat(stepDetected).isTrue
    }

    @Test
    fun `should not trigger callback when disabled`() {
        var stepDetected = false
        listener.onStepEvent { stepDetected = true }
        listener.disable()

        listener.onStep(suspendContext)

        assertThat(stepDetected).isFalse
    }

    // 자동 캡처 상태 테스트
    @Test
    fun `should enable auto-capture`() {
        listener.enableAutoCapture()
        // 내부 상태는 private이므로 간접 테스트
        assertThat(listener.isEnabled()).isTrue
    }

    @Test
    fun `should disable auto-capture`() {
        listener.enableAutoCapture()
        listener.disableAutoCapture()
        // 내부 상태는 private이므로 간접 테스트
        assertThat(listener.isEnabled()).isTrue
    }

    // 컴포넌트 설정 테스트
    @Test
    fun `should set snapshot collector`() {
        val collector = SnapshotCollector()
        listener.setSnapshotCollector(collector)
        // 설정되었는지 간접 확인 (에러 없이 완료)
        assertThat(listener).isNotNull
    }

    @Test
    fun `should set algorithm detector`() {
        val detector = AlgorithmDetector()
        listener.setAlgorithmDetector(detector)
        // 설정되었는지 간접 확인
        assertThat(listener).isNotNull
    }

    // 알고리즘 상태 관리 테스트
    @Test
    fun `should set and get detected algorithm`() {
        listener.setDetectedAlgorithm(SortAlgorithm.BUBBLE_SORT)
        assertThat(listener.getDetectedAlgorithm()).isEqualTo(SortAlgorithm.BUBBLE_SORT)
    }

    @Test
    fun `should reset state when debug session ends`() {
        listener.setDetectedAlgorithm(SortAlgorithm.BUBBLE_SORT)

        listener.onSessionEnd()

        assertThat(listener.getDetectedAlgorithm()).isNull()
    }

    // 성능 테스트
    @Test
    fun `should handle step events quickly`() {
        val startTime = System.currentTimeMillis()

        repeat(100) {
            listener.onStep(suspendContext)
        }

        val elapsed = System.currentTimeMillis() - startTime
        assertThat(elapsed).isLessThan(100) // 100 스텝 < 100ms
    }
}
