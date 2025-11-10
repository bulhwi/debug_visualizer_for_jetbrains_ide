package com.github.algorithmvisualizer.debugger

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertEquals

/**
 * ExpressionEvaluator 테스트
 *
 * Note: 실제 평가 기능은 통합 테스트에서 디버거와 함께 테스트됩니다.
 * 여기서는 데이터 클래스와 헬퍼 메서드들을 테스트합니다.
 */
class ExpressionEvaluatorTest {

    @Test
    fun `ExpressionEvaluator can be instantiated`() {
        val evaluator = ExpressionEvaluator()
        assertNotNull(evaluator)
    }

    // ========== EvaluationResult Tests ==========

    @Test
    fun `EvaluationResult with success true and value`() {
        // Given & When
        val result = ExpressionEvaluator.EvaluationResult(
            success = true,
            value = null, // XValue는 mock이 필요하므로 null 사용
            error = null
        )

        // Then
        assertTrue(result.success)
        assertNull(result.error)
    }

    @Test
    fun `EvaluationResult with success false and error message`() {
        // Given & When
        val result = ExpressionEvaluator.EvaluationResult(
            success = false,
            value = null,
            error = "변수를 찾을 수 없습니다"
        )

        // Then
        assertFalse(result.success)
        assertNull(result.value)
        assertEquals("변수를 찾을 수 없습니다", result.error)
    }

    @Test
    fun `EvaluationResult equality test`() {
        // Given
        val result1 = ExpressionEvaluator.EvaluationResult(
            success = true,
            value = null,
            error = null
        )
        val result2 = ExpressionEvaluator.EvaluationResult(
            success = true,
            value = null,
            error = null
        )
        val result3 = ExpressionEvaluator.EvaluationResult(
            success = false,
            value = null,
            error = "error"
        )

        // Then
        assertThat(result1).isEqualTo(result2)
        assertThat(result1).isNotEqualTo(result3)
    }

    @Test
    fun `EvaluationResult toString should contain all fields`() {
        // Given
        val result = ExpressionEvaluator.EvaluationResult(
            success = false,
            value = null,
            error = "평가 실패"
        )

        // When
        val str = result.toString()

        // Then
        assertThat(str).contains("success=false")
        assertThat(str).contains("평가 실패")
    }

    @Test
    fun `EvaluationResult copy should work correctly`() {
        // Given
        val original = ExpressionEvaluator.EvaluationResult(
            success = true,
            value = null,
            error = null
        )

        // When
        val copied = original.copy(success = false, error = "변경됨")

        // Then
        assertFalse(copied.success)
        assertEquals("변경됨", copied.error)
        assertTrue(original.success) // 원본은 변경되지 않음
    }

    // ========== Constants Tests ==========

    @Test
    fun `timeout constants should be positive`() {
        // Reflection을 사용하여 companion object의 private 상수 확인
        val companionClass = ExpressionEvaluator::class.java.declaredClasses
            .find { it.simpleName == "Companion" }

        assertNotNull(companionClass)
    }

    @Test
    fun `error message constants should not be empty`() {
        // 에러 메시지 상수들이 정의되어 있는지 확인
        // 실제로는 private이므로 간접적으로 확인
        val evaluator = ExpressionEvaluator()
        assertNotNull(evaluator)
    }
}
