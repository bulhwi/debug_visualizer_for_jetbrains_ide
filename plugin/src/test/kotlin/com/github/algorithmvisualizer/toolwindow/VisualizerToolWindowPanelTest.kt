package com.github.algorithmvisualizer.toolwindow

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * VisualizerToolWindowPanel 테스트
 *
 * Note: 실제 프로젝트가 필요한 테스트는 통합 테스트에서 수행합니다.
 * 여기서는 기본적인 구조와 유틸리티 메서드를 테스트합니다.
 */
class VisualizerToolWindowPanelTest {

    @Test
    fun `escapeJson should escape backslashes`() {
        // Given
        val input = "path\\to\\file"

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("path\\\\to\\\\file", result)
    }

    @Test
    fun `escapeJson should escape double quotes`() {
        // Given
        val input = "He said \"hello\""

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("He said \\\"hello\\\"", result)
    }

    @Test
    fun `escapeJson should escape newlines`() {
        // Given
        val input = "line1\nline2"

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("line1\\nline2", result)
    }

    @Test
    fun `escapeJson should escape carriage returns`() {
        // Given
        val input = "line1\rline2"

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("line1\\rline2", result)
    }

    @Test
    fun `escapeJson should escape tabs`() {
        // Given
        val input = "col1\tcol2"

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("col1\\tcol2", result)
    }

    @Test
    fun `escapeJson should handle multiple escape sequences`() {
        // Given
        val input = "test\n\"value\"\tdata\\path"

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("test\\n\\\"value\\\"\\tdata\\\\path", result)
    }

    @Test
    fun `escapeJson should handle empty string`() {
        // Given
        val input = ""

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `escapeJson should handle string without special characters`() {
        // Given
        val input = "simple text"

        // When
        val result = escapeJsonString(input)

        // Then
        assertEquals("simple text", result)
    }

    /**
     * Helper function to test JSON escaping logic
     * VisualizerToolWindowPanel의 escapeJson과 동일한 로직
     */
    private fun escapeJsonString(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
