package com.github.algorithmvisualizer

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * AlgorithmVisualizerPlugin 테스트
 */
class AlgorithmVisualizerPluginTest {

    @Test
    fun `plugin constants are defined`() {
        assertEquals("com.github.algorithmvisualizer.debug-visualizer", AlgorithmVisualizerPlugin.PLUGIN_ID)
        assertEquals("Algorithm Debug Visualizer", AlgorithmVisualizerPlugin.PLUGIN_NAME)
        assertNotNull(AlgorithmVisualizerPlugin.VERSION)
    }

    @Test
    fun `plugin can be instantiated`() {
        val plugin = AlgorithmVisualizerPlugin()
        assertNotNull(plugin)
    }
}
