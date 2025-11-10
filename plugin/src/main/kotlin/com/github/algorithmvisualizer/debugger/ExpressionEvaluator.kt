package com.github.algorithmvisualizer.debugger

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
        return evaluateExpression(session, expression).thenApply { result ->
            if (result.success && result.value != null) {
                // 간단한 문자열 표현 사용 (Phase 1-9에서 더 정교하게 구현 예정)
                Pair(result.value.toString(), null)
            } else {
                Pair(result.error, null)
            }
        }
    }
}
