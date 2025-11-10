package com.github.algorithmvisualizer.debugger

import com.intellij.debugger.engine.JavaValue
import com.intellij.openapi.diagnostic.Logger
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueCallback
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * 표현식 평가를 담당하는 클래스
 *
 * XDebuggerEvaluator를 사용하여 디버거 컨텍스트에서 표현식을 평가합니다.
 */
class ExpressionEvaluator {

    private val logger = Logger.getInstance(ExpressionEvaluator::class.java)

    /**
     * 표현식 평가 결과
     */
    data class EvaluationResult(
        val success: Boolean,
        val value: XValue? = null,
        val error: String? = null
    )

    /**
     * 주어진 표현식을 평가합니다.
     *
     * @param session 현재 디버그 세션
     * @param expression 평가할 표현식 (예: "myArray", "myTree.root")
     * @param timeoutSeconds 평가 타임아웃 (초 단위, 기본 5초)
     * @return 평가 결과를 담은 CompletableFuture
     */
    fun evaluateExpression(
        session: XDebugSession,
        expression: String,
        timeoutSeconds: Long = 5
    ): CompletableFuture<EvaluationResult> {
        val future = CompletableFuture<EvaluationResult>()

        try {
            val stackFrame = session.currentStackFrame
            if (stackFrame == null) {
                logger.warn("No current stack frame available")
                future.complete(EvaluationResult(false, error = "현재 스택 프레임을 가져올 수 없습니다"))
                return future
            }

            val evaluator = stackFrame.evaluator
            if (evaluator == null) {
                logger.warn("No evaluator available for current stack frame")
                future.complete(EvaluationResult(false, error = "평가기를 사용할 수 없습니다"))
                return future
            }

            logger.info("Evaluating expression: $expression")

            evaluator.evaluate(
                expression,
                object : XDebuggerEvaluator.XEvaluationCallback {
                    override fun evaluated(result: XValue) {
                        logger.info("Expression evaluated successfully: $expression")
                        future.complete(EvaluationResult(true, value = result))
                    }

                    override fun errorOccurred(errorMessage: String) {
                        logger.warn("Evaluation error for '$expression': $errorMessage")
                        future.complete(EvaluationResult(false, error = errorMessage))
                    }
                },
                null // XSourcePosition - null이면 현재 위치 사용
            )

            // 타임아웃 설정
            CompletableFuture.delayedExecutor(timeoutSeconds, TimeUnit.SECONDS).execute {
                if (!future.isDone) {
                    logger.warn("Evaluation timed out for expression: $expression")
                    future.complete(EvaluationResult(false, error = "평가 시간 초과 (${timeoutSeconds}초)"))
                }
            }

        } catch (e: Exception) {
            logger.error("Exception during expression evaluation: $expression", e)
            future.complete(EvaluationResult(false, error = "평가 중 예외 발생: ${e.message}"))
        }

        return future
    }

    /**
     * XValue에서 실제 값을 추출합니다.
     *
     * Java 디버거의 경우 JavaValue를 사용하여 descriptor에서 값을 가져옵니다.
     */
    private fun extractValueString(xValue: XValue): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        try {
            logger.info("Extracting value from XValue type: ${xValue.javaClass.name}")

            // Java 디버거의 경우 JavaValue에서 직접 값 추출 시도
            if (xValue is JavaValue) {
                try {
                    val descriptor = xValue.descriptor
                    val valueText = descriptor.calcValueName()
                    logger.info("JavaValue calcValueName: $valueText")
                    if (valueText != null && valueText != "null") {
                        future.complete(valueText)
                        return future
                    }
                } catch (e: Exception) {
                    logger.warn("Failed to extract from JavaValue", e)
                }
            }

            // 일반적인 방법: computePresentation 사용
            xValue.computePresentation(object : com.intellij.xdebugger.frame.XValueNode {
                override fun setPresentation(
                    icon: javax.swing.Icon?,
                    type: String?,
                    value: String,
                    hasChildren: Boolean
                ) {
                    // 간단한 형태의 setPresentation - 이게 호출되면 value가 실제 값
                    logger.info("setPresentation(simple) called: type=$type, value=$value, hasChildren=$hasChildren")
                    if (!future.isDone) {
                        future.complete(value)
                    }
                }

                override fun setPresentation(
                    icon: javax.swing.Icon?,
                    presentation: com.intellij.xdebugger.frame.presentation.XValuePresentation,
                    hasChildren: Boolean
                ) {
                    // XValuePresentation 형태 - 복잡한 객체인 경우
                    logger.info("setPresentation(XValuePresentation) called: type=${presentation.type}")
                    if (!future.isDone) {
                        val type = presentation.type ?: "unknown"
                        future.complete("$type (use Phase 1-9 for full extraction)")
                    }
                }

                override fun setFullValueEvaluator(fullValueEvaluator: com.intellij.xdebugger.frame.XFullValueEvaluator) {
                    logger.info("setFullValueEvaluator called - full value available")
                    // 전체 값 평가기는 무시 (Phase 1-9에서 구현)
                }
            }, com.intellij.xdebugger.frame.XValuePlace.TREE)

            // 타임아웃: 2초 후에도 완료되지 않으면 기본값 반환
            CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute {
                if (!future.isDone) {
                    logger.warn("Value extraction timed out")
                    future.complete("<timeout>")
                }
            }

        } catch (e: Exception) {
            logger.error("Failed to extract value string", e)
            future.complete("Error: ${e.message}")
        }

        return future
    }

    /**
     * 표현식 평가와 값 추출을 한 번에 수행합니다.
     *
     * @param session 현재 디버그 세션
     * @param expression 평가할 표현식
     * @return 값의 문자열 표현과 타입 정보를 담은 CompletableFuture
     */
    fun evaluateAndExtract(
        session: XDebugSession,
        expression: String
    ): CompletableFuture<Pair<String?, String?>> {
        return evaluateExpression(session, expression).thenCompose { result ->
            if (result.success && result.value != null) {
                // XValue에서 실제 값 추출
                extractValueString(result.value).thenApply { valueStr ->
                    Pair(valueStr, null)
                }
            } else {
                CompletableFuture.completedFuture(Pair(result.error, null))
            }
        }
    }
}
