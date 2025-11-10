package com.github.algorithmvisualizer.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Algorithm Visualizer 도구 윈도우 팩토리
 *
 * 이 클래스는 IDE가 도구 윈도우를 생성할 때 호출됩니다.
 */
class VisualizerToolWindowFactory : ToolWindowFactory, DumbAware {

    /**
     * 도구 윈도우 생성
     *
     * @param project 현재 프로젝트
     * @param toolWindow 도구 윈도우 인스턴스
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val visualizerPanel = VisualizerToolWindowPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(visualizerPanel, "", false)

        toolWindow.contentManager.addContent(content)
    }

    /**
     * 도구 윈도우가 프로젝트에 대해 사용 가능한지 여부
     *
     * @param project 현재 프로젝트
     * @return 항상 true (모든 프로젝트에서 사용 가능)
     */
    override fun shouldBeAvailable(project: Project): Boolean = true
}
