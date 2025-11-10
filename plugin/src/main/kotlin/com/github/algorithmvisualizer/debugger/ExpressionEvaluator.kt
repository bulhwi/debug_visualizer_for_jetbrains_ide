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
 * JDI (Java Debug Interface)를 직접 사용하여 배열 및 컬렉션의 값을 추출합니다.
 */
class ExpressionEvaluator {

    private val logger = Logger.getInstance(ExpressionEvaluator::class.java)

    companion object {
        // 타임아웃 설정 (초)
        private const val DEFAULT_EVALUATION_TIMEOUT_SECONDS = 5L
        private const val VALUE_EXTRACTION_TIMEOUT_SECONDS = 3L

        // 에러 메시지
        private const val ERROR_NO_STACK_FRAME = "현재 스택 프레임을 가져올 수 없습니다"
        private const val ERROR_NO_EVALUATOR = "평가기를 사용할 수 없습니다"
        private const val ERROR_NO_DEBUG_PROCESS = "디버그 프로세스를 사용할 수 없습니다"
        private const val ERROR_TIMEOUT = "타임아웃"
        private const val ERROR_UNSUPPORTED_TYPE = "지원하지 않는 타입"
    }

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
                future.complete(EvaluationResult(false, error = ERROR_NO_STACK_FRAME))
                return future
            }

            val evaluator = stackFrame.evaluator
            if (evaluator == null) {
                logger.warn("No evaluator available for current stack frame")
                future.complete(EvaluationResult(false, error = ERROR_NO_EVALUATOR))
                return future
            }

            logger.warn("=== EVALUATING EXPRESSION: $expression ===")

            evaluator.evaluate(
                expression,
                object : XDebuggerEvaluator.XEvaluationCallback {
                    override fun evaluated(result: XValue) {
                        logger.warn("=== EVALUATED SUCCESS: $expression, XValue class: ${result.javaClass.name} ===")
                        future.complete(EvaluationResult(true, value = result))
                    }

                    override fun errorOccurred(errorMessage: String) {
                        logger.warn("=== EVALUATION ERROR for '$expression': $errorMessage ===")
                        future.complete(EvaluationResult(false, error = errorMessage))
                    }
                },
                null // XSourcePosition - null이면 현재 위치 사용
            )

            // 타임아웃 설정
            setTimeoutForFuture(
                future,
                timeoutSeconds,
                EvaluationResult(false, error = "평가 시간 초과 (${timeoutSeconds}초)")
            )

        } catch (e: Exception) {
            logger.error("Exception during expression evaluation: $expression", e)
            future.complete(EvaluationResult(false, error = "평가 중 예외 발생: ${e.message}"))
        }

        return future
    }

    /**
     * XValue에서 실제 값을 추출합니다.
     *
     * Java 디버거의 경우 JavaValue를 사용하여 자식 값들을 순회합니다.
     */
    private fun extractValueString(xValue: XValue): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        try {
            logger.warn("=== EXTRACTING value from XValue type: ${xValue.javaClass.name} ===")

            // JavaValue인 경우 항상 JDI를 직접 사용
            if (xValue is JavaValue) {
                return extractValueUsingJDI(xValue)
            }

            // JavaValue가 아닌 경우 computePresentation 사용
            xValue.computePresentation(object : com.intellij.xdebugger.frame.XValueNode {
                override fun setPresentation(
                    icon: javax.swing.Icon?,
                    type: String?,
                    value: String,
                    hasChildren: Boolean
                ) {
                    // 간단한 값 (프리미티브, 문자열 등)
                    logger.warn("=== setPresentation(simple) called: type=$type, value=$value, hasChildren=$hasChildren ===")
                    if (!future.isDone) {
                        future.complete(value)
                    }
                }

                override fun setPresentation(
                    icon: javax.swing.Icon?,
                    presentation: com.intellij.xdebugger.frame.presentation.XValuePresentation,
                    hasChildren: Boolean
                ) {
                    // 복잡한 객체 (배열, 컬렉션 등)
                    logger.warn("=== setPresentation(XValuePresentation) called: type=${presentation.type}, hasChildren=$hasChildren ===")

                    if (!future.isDone) {
                        future.complete(presentation.type ?: "unknown")
                    }
                }

                override fun setFullValueEvaluator(fullValueEvaluator: com.intellij.xdebugger.frame.XFullValueEvaluator) {
                    logger.warn("=== setFullValueEvaluator called ===")
                }
            }, com.intellij.xdebugger.frame.XValuePlace.TREE)

            // 타임아웃 설정
            setTimeoutForFuture(future, VALUE_EXTRACTION_TIMEOUT_SECONDS, "<timeout>")

        } catch (e: Exception) {
            logger.error("Failed to extract value string", e)
            future.complete("Error: ${e.message}")
        }

        return future
    }

    /**
     * JDI를 사용하여 JavaValue에서 값을 직접 추출합니다.
     *
     * 프리미티브, 문자열, 배열, 컬렉션 등 모든 타입을 처리합니다.
     */
    private fun extractValueUsingJDI(xValue: JavaValue): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        try {
            logger.warn("=== Extracting JavaValue using JDI ===")

            // DebugProcess를 통해 JDI Value에 접근
            val debugProcess = xValue.evaluationContext.debugProcess
            if (debugProcess == null) {
                logger.warn("=== No debug process available ===")
                future.complete("<$ERROR_NO_DEBUG_PROCESS>")
                return future
            }

            debugProcess.managerThread.invoke(object : com.intellij.debugger.engine.events.DebuggerCommandImpl() {
                override fun action() {
                    try {
                        val descriptor = xValue.descriptor
                        val jdiValue = descriptor.value

                        logger.warn("=== JDI Value type: ${jdiValue?.javaClass?.name} ===")

                        val result = convertJdiValueToString(jdiValue)
                        logger.warn("=== Extracted value: $result ===")
                        future.complete(result)
                    } catch (e: Exception) {
                        logger.error("JDI extraction failed", e)
                        future.complete("<error: ${e.message}>")
                    }
                }
            })

            // 타임아웃 설정
            setTimeoutForFuture(future, VALUE_EXTRACTION_TIMEOUT_SECONDS, "<timeout>")

        } catch (e: Exception) {
            logger.error("Failed to extract value using JDI", e)
            future.complete("<error: ${e.message}>")
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
        logger.warn("=== START evaluateAndExtract for: $expression ===")
        return evaluateExpression(session, expression).thenCompose { result ->
            logger.warn("=== evaluateExpression result: success=${result.success}, hasValue=${result.value != null} ===")
            if (result.success && result.value != null) {
                // XValue에서 실제 값 추출
                extractValueString(result.value).thenApply { valueStr ->
                    logger.warn("=== FINAL extracted value: $valueStr ===")
                    Pair(valueStr, null)
                }
            } else {
                logger.warn("=== FINAL error: ${result.error} ===")
                CompletableFuture.completedFuture(Pair(result.error, null))
            }
        }
    }

    // ========== Helper Methods ==========

    /**
     * JDI Value를 문자열로 변환합니다.
     *
     * @param value JDI Value 객체
     * @return 문자열 표현
     */
    private fun convertJdiValueToString(value: com.sun.jdi.Value?): String {
        return when (value) {
            null -> "null"
            is com.sun.jdi.CharValue -> "'${value.value()}'"
            is com.sun.jdi.StringReference -> "\"${value.value()}\""
            is com.sun.jdi.BooleanValue -> value.value().toString()
            is com.sun.jdi.ByteValue -> value.value().toString()
            is com.sun.jdi.ShortValue -> value.value().toString()
            is com.sun.jdi.IntegerValue -> value.value().toString()
            is com.sun.jdi.LongValue -> "${value.value()}L"
            is com.sun.jdi.FloatValue -> "${value.value()}f"
            is com.sun.jdi.DoubleValue -> value.value().toString()
            is com.sun.jdi.ArrayReference -> {
                // 배열인 경우 각 요소 추출
                val length = value.length()
                val elements = mutableListOf<String>()

                logger.warn("=== Array length: $length ===")

                for (i in 0 until length) {
                    val element = value.getValue(i)
                    val elementStr = convertJdiValueToString(element)
                    elements.add(elementStr)
                    logger.warn("=== Array[$i]: $elementStr ===")
                }

                val result = "[${elements.joinToString(", ")}]"
                logger.warn("=== Final array values: $result ===")
                result
            }
            is com.sun.jdi.ObjectReference -> value.toString()
            else -> value.toString()
        }
    }

    /**
     * CompletableFuture에 타임아웃을 설정합니다.
     *
     * @param future 타임아웃을 설정할 Future
     * @param timeoutSeconds 타임아웃 시간 (초)
     * @param defaultValue 타임아웃 시 반환할 기본값
     */
    private fun <T> setTimeoutForFuture(
        future: CompletableFuture<T>,
        timeoutSeconds: Long,
        defaultValue: T
    ) {
        CompletableFuture.delayedExecutor(timeoutSeconds, TimeUnit.SECONDS).execute {
            if (!future.isDone) {
                logger.warn("Operation timed out after ${timeoutSeconds}s")
                future.complete(defaultValue)
            }
        }
    }
}
