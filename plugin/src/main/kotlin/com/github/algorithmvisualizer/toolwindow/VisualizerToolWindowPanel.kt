package com.github.algorithmvisualizer.toolwindow

import com.github.algorithmvisualizer.debugger.DebuggerIntegration
import com.github.algorithmvisualizer.debugger.DebuggerStateListener
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.intellij.xdebugger.XDebugSession
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*

/**
 * Algorithm Visualizer 도구 윈도우의 메인 패널
 *
 * 이 패널은 표현식 입력 필드, 평가 버튼, 시각화 영역을 포함합니다.
 */
class VisualizerToolWindowPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val expressionField: JBTextField
    private val evaluateButton: JButton
    private val visualizationArea: JPanel
    private val statusLabel: JBLabel
    private val debuggerIntegration: DebuggerIntegration = DebuggerIntegration.getInstance(project)

    init {
        border = JBUI.Borders.empty(8)

        // 디버거 상태 리스너 등록
        setupDebuggerListener()

        // 상단 패널: 표현식 입력 영역
        val topPanel = createTopPanel()
        add(topPanel, BorderLayout.NORTH)

        // 중앙 패널: 시각화 영역
        visualizationArea = JPanel(BorderLayout())
        visualizationArea.border = JBUI.Borders.empty(8)

        val welcomeLabel = JBLabel(
            "<html><center>" +
            "<h2>Algorithm Debug Visualizer</h2>" +
            "<p>디버깅 세션을 시작하고 표현식을 입력하세요.</p>" +
            "<br>" +
            "<p style='color: gray;'>예: myArray, myTree, graph</p>" +
            "</center></html>",
            SwingConstants.CENTER
        )
        visualizationArea.add(welcomeLabel, BorderLayout.CENTER)

        val scrollPane = JBScrollPane(visualizationArea)
        add(scrollPane, BorderLayout.CENTER)

        // 하단 패널: 상태 표시
        statusLabel = JBLabel("준비됨")
        statusLabel.border = JBUI.Borders.empty(4, 8)
        add(statusLabel, BorderLayout.SOUTH)

        // 필드 초기화
        expressionField = topPanel.components
            .filterIsInstance<JPanel>()
            .firstOrNull()
            ?.components
            ?.filterIsInstance<JBTextField>()
            ?.firstOrNull() ?: JBTextField()

        evaluateButton = topPanel.components
            .filterIsInstance<JPanel>()
            .firstOrNull()
            ?.components
            ?.filterIsInstance<JButton>()
            ?.firstOrNull() ?: JButton()
    }

    /**
     * 상단 입력 패널 생성
     */
    private fun createTopPanel(): JPanel {
        val panel = JPanel(BorderLayout(8, 8))
        panel.border = JBUI.Borders.empty(0, 0, 8, 0)

        // 레이블
        val label = JBLabel("표현식:")
        panel.add(label, BorderLayout.WEST)

        // 입력 필드와 버튼을 담을 패널
        val inputPanel = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0))

        // 표현식 입력 필드
        val textField = JBTextField(30)
        textField.toolTipText = "평가할 표현식을 입력하세요 (예: myArray, myTree.root)"
        textField.addActionListener {
            evaluateExpression()
        }
        inputPanel.add(textField)

        // 평가 버튼
        val button = JButton("Evaluate")
        button.toolTipText = "표현식을 평가하고 시각화합니다"
        button.addActionListener {
            evaluateExpression()
        }
        inputPanel.add(button)

        panel.add(inputPanel, BorderLayout.CENTER)

        return panel
    }

    /**
     * 디버거 상태 리스너 설정
     */
    private fun setupDebuggerListener() {
        debuggerIntegration.addListener(object : DebuggerStateListener {
            override fun onSessionStarted(session: XDebugSession) {
                updateStatus("디버깅 세션 시작됨: ${session.sessionName}")
                evaluateButton.isEnabled = true
            }

            override fun onSessionStopped(session: XDebugSession) {
                updateStatus("디버깅 세션 종료됨")
                evaluateButton.isEnabled = false
            }

            override fun onSessionChanged(previous: XDebugSession?, current: XDebugSession?) {
                if (current != null) {
                    updateStatus("디버깅 세션 활성화: ${current.sessionName}")
                    evaluateButton.isEnabled = true
                } else {
                    updateStatus("디버깅 세션 없음")
                    evaluateButton.isEnabled = false
                }
            }
        })

        // 초기 상태 확인
        if (debuggerIntegration.isDebugging()) {
            val session = debuggerIntegration.getCurrentSession()
            updateStatus("디버깅 중: ${session?.sessionName}")
            evaluateButton.isEnabled = true
        } else {
            updateStatus("디버깅 세션을 시작하세요")
            evaluateButton.isEnabled = false
        }
    }

    /**
     * 표현식 평가 실행
     */
    private fun evaluateExpression() {
        val expression = expressionField.text.trim()

        if (expression.isEmpty()) {
            updateStatus("표현식을 입력해주세요", isError = true)
            return
        }

        // 디버거 상태 확인
        if (!debuggerIntegration.isDebugging()) {
            updateStatus("디버깅 세션이 활성화되지 않았습니다", isError = true)
            return
        }

        if (!debuggerIntegration.isSuspended()) {
            updateStatus("브레이크포인트에서 실행이 멈춰있지 않습니다", isError = true)
            return
        }

        val stackFrame = debuggerIntegration.getCurrentStackFrame()
        if (stackFrame == null) {
            updateStatus("현재 스택 프레임을 가져올 수 없습니다", isError = true)
            return
        }

        updateStatus("평가 중: $expression")

        // TODO: 실제 표현식 평가 로직 구현
        // Phase 1-4에서 구현 예정

        // 임시 메시지
        SwingUtilities.invokeLater {
            showPlaceholderVisualization(expression)
            updateStatus("시각화 준비 완료 (Phase 1-4에서 실제 구현 예정)")
        }
    }

    /**
     * 플레이스홀더 시각화 표시 (개발 중)
     */
    private fun showPlaceholderVisualization(expression: String) {
        visualizationArea.removeAll()

        val placeholder = JBLabel(
            "<html><center>" +
            "<h3>시각화: $expression</h3>" +
            "<br>" +
            "<p>이 영역에 시각화가 표시됩니다.</p>" +
            "<p style='color: gray;'>Phase 1-5 (JCEF 웹뷰)와 Phase 1-7 (D3.js 렌더러)에서 구현 예정</p>" +
            "</center></html>",
            SwingConstants.CENTER
        )

        visualizationArea.add(placeholder, BorderLayout.CENTER)
        visualizationArea.revalidate()
        visualizationArea.repaint()
    }

    /**
     * 상태 메시지 업데이트
     */
    private fun updateStatus(message: String, isError: Boolean = false) {
        SwingUtilities.invokeLater {
            statusLabel.text = message
            statusLabel.foreground = if (isError) {
                java.awt.Color.RED
            } else {
                null // 기본 색상 사용
            }
        }
    }

    /**
     * 시각화 영역 클리어
     */
    fun clearVisualization() {
        visualizationArea.removeAll()
        visualizationArea.revalidate()
        visualizationArea.repaint()
        updateStatus("시각화가 지워졌습니다")
    }

    /**
     * 표현식 필드에 텍스트 설정
     */
    fun setExpression(text: String) {
        expressionField.text = text
    }

    /**
     * 현재 표현식 가져오기
     */
    fun getExpression(): String = expressionField.text.trim()
}
