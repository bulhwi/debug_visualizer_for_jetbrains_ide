package com.github.algorithmvisualizer.debugger

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * DebuggerIntegration 테스트
 *
 * Note: 실제 디버거 통합 테스트는 통합 테스트 환경에서 수행됩니다.
 * 여기서는 기본적인 구조와 인터페이스만 확인합니다.
 */
class DebuggerIntegrationTest {

    @Test
    fun `DebuggerStateListener interface exists`() {
        // 인터페이스가 정의되어 있는지 확인
        val listener = object : DebuggerStateListener {}
        assertNotNull(listener)
    }

    @Test
    fun `DebuggerIntegration class structure`() {
        // 클래스 구조 확인
        val clazz = DebuggerIntegration::class.java
        assertTrue(clazz.declaredMethods.any { it.name == "getCurrentSession" })
        assertTrue(clazz.declaredMethods.any { it.name == "getCurrentStackFrame" })
        assertTrue(clazz.declaredMethods.any { it.name == "isDebugging" })
        assertTrue(clazz.declaredMethods.any { it.name == "isSuspended" })
        assertTrue(clazz.declaredMethods.any { it.name == "addListener" })
        assertTrue(clazz.declaredMethods.any { it.name == "removeListener" })
    }
}
