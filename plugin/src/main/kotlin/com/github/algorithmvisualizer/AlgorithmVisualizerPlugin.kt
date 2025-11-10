package com.github.algorithmvisualizer

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

/**
 * Algorithm Debug Visualizer 플러그인의 메인 클래스
 *
 * 이 클래스는 플러그인이 로드될 때 초기화를 담당합니다.
 */
class AlgorithmVisualizerPlugin : StartupActivity {

    private val logger = Logger.getInstance(AlgorithmVisualizerPlugin::class.java)

    /**
     * 프로젝트가 열릴 때 호출됩니다.
     *
     * @param project 현재 프로젝트
     */
    override fun runActivity(project: Project) {
        logger.info("Algorithm Debug Visualizer plugin initialized for project: ${project.name}")

        // TODO: 향후 초기화 로직 추가
        // - 디버거 리스너 등록
        // - 도구 윈도우 초기화
        // - 설정 로드
    }

    companion object {
        const val PLUGIN_ID = "com.github.algorithmvisualizer.debug-visualizer"
        const val PLUGIN_NAME = "Algorithm Debug Visualizer"
        const val VERSION = "0.1.0-SNAPSHOT"
    }
}
