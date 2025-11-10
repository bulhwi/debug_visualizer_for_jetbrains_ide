package com.github.algorithmvisualizer.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefClient
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * JCEF를 사용한 웹 기반 시각화 패널
 *
 * 이 패널은 JBCefBrowser를 사용하여 HTML/JavaScript 기반 시각화를 렌더링합니다.
 */
class JCEFVisualizationPanel : JPanel(BorderLayout()) {

    private val logger = Logger.getInstance(JCEFVisualizationPanel::class.java)
    private val browser: JBCefBrowser
    private var isInitialized = false
    private val jsToJavaQuery: JBCefJSQuery

    init {
        // JCEF 브라우저 생성
        browser = JBCefBrowser()

        // JavaScript에서 Java로 메시지 전달을 위한 쿼리 설정
        jsToJavaQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)
        jsToJavaQuery.addHandler { message ->
            handleJavaScriptMessage(message)
            null
        }

        // 로드 핸들러 설정
        browser.jbCefClient.addLoadHandler(object : CefLoadHandlerAdapter() {
            override fun onLoadEnd(cefBrowser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                if (frame?.isMain == true) {
                    logger.info("JCEF browser loaded successfully")
                    isInitialized = true
                    injectJavaScriptBridge()
                }
            }

            override fun onLoadError(
                browser: CefBrowser?,
                frame: CefFrame?,
                errorCode: org.cef.handler.CefLoadHandler.ErrorCode?,
                errorText: String?,
                failedUrl: String?
            ) {
                logger.error("JCEF load error: $errorText (code: $errorCode, url: $failedUrl)")
            }
        }, browser.cefBrowser)

        // 초기 HTML 로드
        loadInitialHTML()

        // 브라우저 컴포넌트 추가
        add(browser.component, BorderLayout.CENTER)
    }

    /**
     * 초기 HTML 페이지 로드
     */
    private fun loadInitialHTML() {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        margin: 0;
                        padding: 20px;
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        background-color: #ffffff;
                        color: #333333;
                    }
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                    }
                    .welcome {
                        text-align: center;
                        padding: 60px 20px;
                    }
                    .welcome h1 {
                        color: #2196F3;
                        margin-bottom: 16px;
                    }
                    .welcome p {
                        color: #666;
                        font-size: 16px;
                        line-height: 1.6;
                    }
                    #visualization {
                        min-height: 400px;
                        border: 1px solid #e0e0e0;
                        border-radius: 8px;
                        padding: 20px;
                        margin-top: 20px;
                    }
                    .status {
                        padding: 12px;
                        margin: 10px 0;
                        border-radius: 4px;
                        background-color: #f5f5f5;
                    }
                    .status.success {
                        background-color: #e8f5e9;
                        color: #2e7d32;
                    }
                    .status.error {
                        background-color: #ffebee;
                        color: #c62828;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="welcome">
                        <h1>🎨 Algorithm Visualizer</h1>
                        <p>JCEF 웹뷰가 성공적으로 로드되었습니다.</p>
                        <p>디버거에서 표현식을 평가하면 이 영역에 시각화가 표시됩니다.</p>
                    </div>
                    <div id="visualization"></div>
                </div>

                <script>
                    // Java에서 호출할 수 있는 함수들
                    window.visualizerAPI = {
                        showData: function(data) {
                            console.log('Received data:', data);
                            const viz = document.getElementById('visualization');
                            viz.innerHTML = '<div class="status success">데이터 수신: ' + JSON.stringify(data) + '</div>';
                        },

                        showError: function(message) {
                            const viz = document.getElementById('visualization');
                            viz.innerHTML = '<div class="status error">오류: ' + message + '</div>';
                        },

                        clear: function() {
                            document.getElementById('visualization').innerHTML = '';
                        }
                    };

                    // Java로 메시지 전송 함수 (주입될 예정)
                    window.sendToJava = function(message) {
                        console.log('Sending to Java:', message);
                    };

                    console.log('Visualizer API initialized');
                </script>
            </body>
            </html>
        """.trimIndent()

        browser.loadHTML(html)
    }

    /**
     * JavaScript와 Java 간의 브리지 주입
     */
    private fun injectJavaScriptBridge() {
        val jsCode = """
            window.sendToJava = function(message) {
                ${jsToJavaQuery.inject("message")}
            };
            console.log('Java-JavaScript bridge injected');
        """.trimIndent()

        browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
    }

    /**
     * JavaScript에서 전달된 메시지 처리
     */
    private fun handleJavaScriptMessage(message: String) {
        logger.info("Received message from JavaScript: $message")
        // TODO: 메시지 처리 로직 구현
    }

    /**
     * 시각화 데이터를 JavaScript로 전송
     *
     * @param data JSON 형식의 시각화 데이터
     */
    fun showVisualization(data: String) {
        if (!isInitialized) {
            logger.warn("Browser not initialized yet")
            return
        }

        val jsCode = "window.visualizerAPI.showData($data);"
        browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
    }

    /**
     * 에러 메시지 표시
     */
    fun showError(message: String) {
        if (!isInitialized) {
            logger.warn("Browser not initialized yet")
            return
        }

        val escapedMessage = message.replace("'", "\\'")
        val jsCode = "window.visualizerAPI.showError('$escapedMessage');"
        browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
    }

    /**
     * 시각화 영역 클리어
     */
    fun clearVisualization() {
        if (!isInitialized) {
            return
        }

        val jsCode = "window.visualizerAPI.clear();"
        browser.cefBrowser.executeJavaScript(jsCode, browser.cefBrowser.url, 0)
    }

    /**
     * JavaScript 코드 실행
     */
    fun executeJavaScript(code: String) {
        if (!isInitialized) {
            logger.warn("Browser not initialized yet")
            return
        }

        browser.cefBrowser.executeJavaScript(code, browser.cefBrowser.url, 0)
    }

    /**
     * 브라우저 리소스 정리
     */
    fun dispose() {
        jsToJavaQuery.dispose()
        browser.dispose()
    }
}
