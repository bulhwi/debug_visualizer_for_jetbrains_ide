package com.github.algorithmvisualizer.debugger

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * ExpressionEvaluator 테스트
 *
 * Note: 실제 평가 기능은 통합 테스트에서 디버거와 함께 테스트됩니다.
 */
class ExpressionEvaluatorTest {

    @Test
    fun `ExpressionEvaluator can be instantiated`() {
        val evaluator = ExpressionEvaluator()
        assertNotNull(evaluator)
    }

    @Test
    fun `EvaluationResult data class structure`() {
        val result = ExpressionEvaluator.EvaluationResult(
            success = true,
            value = null,
            error = null
        )
        assertTrue(result.success)
    }
}
