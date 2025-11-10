package com.github.algorithmvisualizer.toolwindow

import com.github.algorithmvisualizer.debugger.DebuggerIntegration
import com.github.algorithmvisualizer.debugger.DebuggerStateListener
import com.github.algorithmvisualizer.debugger.ExpressionEvaluator
import com.github.algorithmvisualizer.ui.JCEFVisualizationPanel
import com.intellij.openapi.project.Project
import com.intellij.ui.jcef.JBCefApp
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
    private val expressionEvaluator: ExpressionEvaluator = ExpressionEvaluator()
    private var jcefPanel: JCEFVisualizationPanel? = null
    private val useJCEF: Boolean = JBCefApp.isSupported()

    init {
        border = JBUI.Borders.empty(8)

        // 상단 패널: 표현식 입력 영역
        val topPanel = createTopPanel()
        add(topPanel, BorderLayout.NORTH)

        // 필드 초기화 (디버거 리스너 등록 전에 수행)
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

        // 중앙 패널: 시각화 영역
        visualizationArea = JPanel(BorderLayout())
        visualizationArea.border = JBUI.Borders.empty(0)

        // JCEF 지원 여부에 따라 다른 UI 표시
        if (useJCEF) {
            try {
                jcefPanel = JCEFVisualizationPanel()
                visualizationArea.add(jcefPanel!!, BorderLayout.CENTER)
                // JCEF는 자체 스크롤을 가지므로 스크롤 팬 없이 직접 추가
                add(visualizationArea, BorderLayout.CENTER)
            } catch (e: Exception) {
                showFallbackWelcome()
                val scrollPane = JBScrollPane(visualizationArea)
                add(scrollPane, BorderLayout.CENTER)
            }
        } else {
            showFallbackWelcome()
            val scrollPane = JBScrollPane(visualizationArea)
            add(scrollPane, BorderLayout.CENTER)
        }

        // 하단 패널: 상태 표시
        statusLabel = JBLabel("준비됨")
        statusLabel.border = JBUI.Borders.empty(4, 8)
        add(statusLabel, BorderLayout.SOUTH)

        // 모든 UI 컴포넌트가 초기화된 후 디버거 리스너 등록
        setupDebuggerListener()

        // JCEF 초기 상태 메시지
        if (useJCEF && jcefPanel != null) {
            updateStatus("JCEF 웹뷰 로드됨 - 디버깅 세션을 시작하세요")
        } else if (!useJCEF) {
            updateStatus("JCEF가 지원되지 않습니다", isError = true)
        }
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

        // 표현식 평가 실행
        val session = debuggerIntegration.getCurrentSession()!!
        expressionEvaluator.evaluateAndExtract(session, expression)
            .thenAccept { (value, type) ->
                SwingUtilities.invokeLater {
                    if (value != null) {
                        showEvaluationResult(expression, value, type)
                        updateStatus("평가 완료: $expression = $value")
                    } else {
                        updateStatus("평가 실패: 값을 가져올 수 없습니다", isError = true)
                    }
                }
            }
            .exceptionally { throwable ->
                SwingUtilities.invokeLater {
                    updateStatus("평가 중 오류 발생: ${throwable.message}", isError = true)
                }
                null
            }
    }

    /**
     * Fallback 환영 메시지 표시 (JCEF 미지원 시)
     */
    private fun showFallbackWelcome() {
        val welcomeLabel = JBLabel(
            "<html><center>" +
            "<h2>Algorithm Debug Visualizer</h2>" +
            "<p>디버깅 세션을 시작하고 표현식을 입력하세요.</p>" +
            "<br>" +
            "<p style='color: gray;'>예: myArray, myTree, graph</p>" +
            "<p style='color: orange;'>JCEF가 지원되지 않아 기본 UI를 사용합니다.</p>" +
            "</center></html>",
            SwingConstants.CENTER
        )
        visualizationArea.add(welcomeLabel, BorderLayout.CENTER)
    }

    /**
     * 평가 결과 표시
     */
    private fun showEvaluationResult(expression: String, value: String, type: String?) {
        if (useJCEF && jcefPanel != null) {
            // JCEF를 사용하여 표시
            val data = buildString {
                append("{")
                append("\"expression\": \"${escapeJson(expression)}\",")
                append("\"value\": \"${escapeJson(value)}\",")
                if (type != null) {
                    append("\"type\": \"${escapeJson(type)}\",")
                }
                append("\"timestamp\": ${System.currentTimeMillis()}")
                append("}")
            }
            jcefPanel?.showVisualization(data)
        } else {
            // Fallback: Swing 레이블 사용
            visualizationArea.removeAll()

            val resultText = buildString {
                append("<html><div style='padding: 16px;'>")
                append("<h3>평가 결과</h3>")
                append("<table style='border-collapse: collapse; width: 100%;'>")
                append("<tr><td style='padding: 8px; border: 1px solid gray;'><b>표현식:</b></td>")
                append("<td style='padding: 8px; border: 1px solid gray;'>$expression</td></tr>")
                if (type != null) {
                    append("<tr><td style='padding: 8px; border: 1px solid gray;'><b>타입:</b></td>")
                    append("<td style='padding: 8px; border: 1px solid gray;'>$type</td></tr>")
                }
                append("<tr><td style='padding: 8px; border: 1px solid gray;'><b>값:</b></td>")
                append("<td style='padding: 8px; border: 1px solid gray;'>$value</td></tr>")
                append("</table>")
                append("<br>")
                append("<p style='color: gray; font-size: 0.9em;'>")
                append("D3.js 시각화는 Phase 1-7에서 구현 예정")
                append("</p>")
                append("</div></html>")
            }

            val resultLabel = JBLabel(resultText, SwingConstants.LEFT)
            resultLabel.verticalAlignment = SwingConstants.TOP

            visualizationArea.add(resultLabel, BorderLayout.CENTER)
            visualizationArea.revalidate()
            visualizationArea.repaint()
        }
    }

    /**
     * JSON 문자열 이스케이프
     */
    private fun escapeJson(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
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
        if (useJCEF && jcefPanel != null) {
            jcefPanel?.clearVisualization()
        } else {
            visualizationArea.removeAll()
            visualizationArea.revalidate()
            visualizationArea.repaint()
        }
        updateStatus("시각화가 지워졌습니다")
    }

    /**
     * 리소스 정리
     */
    fun dispose() {
        jcefPanel?.dispose()
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
