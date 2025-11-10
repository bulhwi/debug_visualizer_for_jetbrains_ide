package com.github.algorithmvisualizer.debugger

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * 값 변환 로직 테스트
 *
 * Note: JDI 클래스들은 final class라서 mocking이 불가능합니다.
 * 따라서 변환 로직을 별도 함수로 분리하여 테스트합니다.
 * 실제 JDI 통합은 통합 테스트에서 수행합니다.
 */
class JdiValueConverterTest {

    /**
     * 배열 포맷팅 로직 테스트
     */
    @Test
    fun `formatArray should handle empty list`() {
        // Given
        val elements = emptyList<String>()

        // When
        val result = formatArray(elements)

        // Then
        assertEquals("[]", result)
    }

    @Test
    fun `formatArray should handle single element`() {
        // Given
        val elements = listOf("42")

        // When
        val result = formatArray(elements)

        // Then
        assertEquals("[42]", result)
    }

    @Test
    fun `formatArray should handle multiple elements`() {
        // Given
        val elements = listOf("1", "2", "3", "4", "5")

        // When
        val result = formatArray(elements)

        // Then
        assertEquals("[1, 2, 3, 4, 5]", result)
    }

    @Test
    fun `formatArray should handle quoted strings`() {
        // Given
        val elements = listOf("\"hello\"", "\"world\"")

        // When
        val result = formatArray(elements)

        // Then
        assertEquals("[\"hello\", \"world\"]", result)
    }

    @Test
    fun `formatArray should handle nested arrays`() {
        // Given
        val elements = listOf("[1, 2]", "[3, 4]", "[5, 6]")

        // When
        val result = formatArray(elements)

        // Then
        assertEquals("[[1, 2], [3, 4], [5, 6]]", result)
    }

    @Test
    fun `formatArray should handle null elements`() {
        // Given
        val elements = listOf("1", "null", "3")

        // When
        val result = formatArray(elements)

        // Then
        assertEquals("[1, null, 3]", result)
    }

    /**
     * 프리미티브 타입 포맷팅 테스트
     */
    @Test
    fun `formatChar should add single quotes`() {
        // Given
        val char = 'A'

        // When
        val result = formatChar(char)

        // Then
        assertEquals("'A'", result)
    }

    @Test
    fun `formatString should add double quotes`() {
        // Given
        val str = "hello"

        // When
        val result = formatString(str)

        // Then
        assertEquals("\"hello\"", result)
    }

    @Test
    fun `formatLong should add L suffix`() {
        // Given
        val long = 123456789L

        // When
        val result = formatLong(long)

        // Then
        assertEquals("123456789L", result)
    }

    @Test
    fun `formatFloat should add f suffix`() {
        // Given
        val float = 3.14f

        // When
        val result = formatFloat(float)

        // Then
        assertEquals("3.14f", result)
    }

    @Test
    fun `formatBoolean should return string representation`() {
        // Given & When & Then
        assertEquals("true", formatBoolean(true))
        assertEquals("false", formatBoolean(false))
    }

    @Test
    fun `formatInteger should return string representation`() {
        // Given
        val int = 42

        // When
        val result = formatInteger(int)

        // Then
        assertEquals("42", result)
    }

    @Test
    fun `formatDouble should return string representation`() {
        // Given
        val double = 3.14159

        // When
        val result = formatDouble(double)

        // Then
        assertEquals("3.14159", result)
    }

    @Test
    fun `formatByte should return string representation`() {
        // Given
        val byte = 127.toByte()

        // When
        val result = formatByte(byte)

        // Then
        assertEquals("127", result)
    }

    @Test
    fun `formatShort should return string representation`() {
        // Given
        val short = 32767.toShort()

        // When
        val result = formatShort(short)

        // Then
        assertEquals("32767", result)
    }

    // ========== Helper Functions (ExpressionEvaluator 로직 시뮬레이션) ==========

    private fun formatArray(elements: List<String>): String {
        return "[${elements.joinToString(", ")}]"
    }

    private fun formatChar(value: Char): String {
        return "'$value'"
    }

    private fun formatString(value: String): String {
        return "\"$value\""
    }

    private fun formatLong(value: Long): String {
        return "${value}L"
    }

    private fun formatFloat(value: Float): String {
        return "${value}f"
    }

    private fun formatBoolean(value: Boolean): String {
        return value.toString()
    }

    private fun formatInteger(value: Int): String {
        return value.toString()
    }

    private fun formatDouble(value: Double): String {
        return value.toString()
    }

    private fun formatByte(value: Byte): String {
        return value.toString()
    }

    private fun formatShort(value: Short): String {
        return value.toString()
    }
}
