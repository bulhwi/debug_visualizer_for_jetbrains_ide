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
        // React UI 리소스 로드 시도
        val indexHtmlPath = "/web/index.html"
        val resourceUrl = javaClass.getResource(indexHtmlPath)

        logger.warn("=== JCEF LOADING START ===")
        logger.warn("Looking for React UI at: $indexHtmlPath")
        logger.warn("Resource URL: $resourceUrl")

        if (resourceUrl != null) {
            // React UI가 빌드되어 리소스에 포함된 경우
            logger.warn("✅ React UI found! Loading from resources...")

            try {
                // HTML 내용 읽기
                val htmlContent = javaClass.getResourceAsStream(indexHtmlPath)?.bufferedReader()?.use { it.readText() }
                logger.warn("HTML content length: ${htmlContent?.length ?: 0}")

                if (htmlContent != null) {
                    // CSS와 JS 파일을 인라인으로 삽입
                    val cssContent = javaClass.getResourceAsStream("/web/assets/index-BIyWmH3D.css")?.bufferedReader()?.use { it.readText() }
                    val jsContent = javaClass.getResourceAsStream("/web/assets/index-BQJihk9k.js")?.bufferedReader()?.use { it.readText() }

                    logger.warn("CSS content length: ${cssContent?.length ?: 0}")
                    logger.warn("JS content length: ${jsContent?.length ?: 0}")

                    if (cssContent != null && jsContent != null) {
                        // 인라인 HTML 생성
                        val inlineHtml = """
                            <!doctype html>
                            <html lang="en">
                              <head>
                                <meta charset="UTF-8" />
                                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                                <title>Algorithm Visualizer</title>
                                <style>
                                $cssContent
                                </style>
                              </head>
                              <body>
                                <div id="root"></div>
                                <script type="module">
                                $jsContent
                                </script>
                              </body>
                            </html>
                        """.trimIndent()

                        browser.loadHTML(inlineHtml)
                        logger.warn("✅ React UI loaded successfully with inline assets")
                    } else {
                        logger.warn("❌ Failed to load CSS or JS assets, using fallback")
                        loadFallbackHTML()
                    }
                } else {
                    logger.warn("❌ Failed to read React UI HTML content, loading fallback")
                    loadFallbackHTML()
                }
            } catch (e: Exception) {
                logger.warn("❌ Error loading React UI: ${e.message}", e)
                loadFallbackHTML()
            }
        } else {
            // React UI가 없는 경우 fallback HTML 사용
            logger.warn("❌ React UI not found in resources, loading fallback HTML")
            loadFallbackHTML()
        }
    }

    /**
     * Fallback HTML 로드 (React UI가 없을 때)
     */
    private fun loadFallbackHTML() {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Algorithm Visualizer</title>
                <style>
                    * {
                        box-sizing: border-box;
                    }
                    body {
                        margin: 0;
                        padding: 0;
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: #333333;
                        min-height: 100%;
                        overflow-x: hidden;
                    }
                    .container {
                        background-color: white;
                        height: 100%;
                        display: flex;
                        flex-direction: column;
                    }
                    .welcome {
                        text-align: center;
                        padding: 40px 30px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        flex-shrink: 0;
                    }
                    .welcome h1 {
                        margin: 0 0 12px 0;
                        font-size: 2em;
                        font-weight: 700;
                    }
                    .welcome p {
                        margin: 6px 0;
                        font-size: 14px;
                        line-height: 1.5;
                        opacity: 0.95;
                    }
                    .emoji {
                        font-size: 2.5em;
                        margin-bottom: 12px;
                    }
                    #visualization {
                        flex: 1;
                        padding: 20px;
                        overflow-y: auto;
                    }
                    .status {
                        padding: 16px 20px;
                        margin: 10px 0;
                        border-radius: 8px;
                        background-color: #f5f5f5;
                        border-left: 4px solid #2196F3;
                    }
                    .status.success {
                        background-color: #e8f5e9;
                        border-left-color: #4caf50;
                        color: #2e7d32;
                    }
                    .status.error {
                        background-color: #ffebee;
                        border-left-color: #f44336;
                        color: #c62828;
                    }
                    .data-display {
                        font-family: 'Monaco', 'Menlo', 'Courier New', monospace;
                        background-color: #f8f9fa;
                        padding: 20px;
                        border-radius: 8px;
                        margin-top: 10px;
                        white-space: pre-wrap;
                        word-break: break-all;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="welcome">
                        <div class="emoji">🎨</div>
                        <h1>Algorithm Debug Visualizer</h1>
                        <p>Fallback UI (React UI not built)</p>
                        <p>디버거를 시작하고 표현식을 평가해보세요.</p>
                    </div>
                    <div id="visualization">
                        <div style="text-align: center; color: #999; padding: 40px;">
                            <p>표현식 평가 결과가 여기에 표시됩니다.</p>
                        </div>
                    </div>
                </div>

                <script>
                    console.log('🎨 Algorithm Visualizer - Initializing (Fallback)...');

                    // Java에서 호출할 수 있는 함수들
                    window.visualizerAPI = {
                        showData: function(data) {
                            console.log('📊 Received data:', data);
                            const viz = document.getElementById('visualization');

                            let dataObj;
                            try {
                                dataObj = typeof data === 'string' ? JSON.parse(data) : data;
                            } catch (e) {
                                dataObj = { raw: data };
                            }

                            viz.innerHTML =
                                '<div class="status success">' +
                                '<strong>✓ 평가 완료</strong>' +
                                '</div>' +
                                '<div class="data-display">' +
                                JSON.stringify(dataObj, null, 2) +
                                '</div>';
                        },

                        showError: function(message) {
                            console.error('❌ Error:', message);
                            const viz = document.getElementById('visualization');
                            viz.innerHTML =
                                '<div class="status error">' +
                                '<strong>❌ 오류 발생</strong><br>' +
                                message +
                                '</div>';
                        },

                        clear: function() {
                            console.log('🗑️ Clearing visualization');
                            const viz = document.getElementById('visualization');
                            viz.innerHTML =
                                '<div style="text-align: center; color: #999; padding: 40px;">' +
                                '<p>표현식 평가 결과가 여기에 표시됩니다.</p>' +
                                '</div>';
                        }
                    };

                    // Java로 메시지 전송 함수 (브리지 주입 후 사용 가능)
                    window.sendToJava = function(message) {
                        console.log('📤 Sending to Java:', message);
                    };

                    console.log('✅ Visualizer API initialized successfully (Fallback)');
                </script>
            </body>
            </html>
        """.trimIndent()

        logger.info("Loading fallback HTML into JCEF browser")
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
