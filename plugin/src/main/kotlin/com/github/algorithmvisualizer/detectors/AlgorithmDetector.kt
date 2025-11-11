package com.github.algorithmvisualizer.detectors

/**
 * 정렬 알고리즘 감지기
 *
 * 메서드명과 코드 패턴을 분석하여 정렬 알고리즘을 자동으로 식별합니다.
 *
 * @see docs/PHASE2_PLAN.md
 */
class AlgorithmDetector {

    /**
     * 메서드명으로부터 알고리즘 감지
     */
    fun detectFromMethodName(methodName: String): DetectionResult {
        val normalized = methodName.lowercase()

        return when {
            normalized.contains("bubble") -> DetectionResult(SortAlgorithm.BUBBLE_SORT, 0.9)
            normalized.contains("quick") -> DetectionResult(SortAlgorithm.QUICK_SORT, 0.9)
            normalized.contains("merge") -> DetectionResult(SortAlgorithm.MERGE_SORT, 0.9)
            normalized.contains("insertion") -> DetectionResult(SortAlgorithm.INSERTION_SORT, 0.9)
            normalized.contains("selection") -> DetectionResult(SortAlgorithm.SELECTION_SORT, 0.9)
            normalized.contains("heap") -> DetectionResult(SortAlgorithm.HEAP_SORT, 0.9)
            normalized == "sort" -> DetectionResult(SortAlgorithm.UNKNOWN, 0.3)
            else -> DetectionResult(SortAlgorithm.UNKNOWN, 0.1)
        }
    }

    /**
     * 코드 패턴으로부터 알고리즘 감지
     */
    fun detectFromCode(code: String): DetectionResult {
        val patterns = listOf(
            detectBubbleSort(code),
            detectQuickSort(code),
            detectMergeSort(code),
            detectInsertionSort(code),
            detectSelectionSort(code)
        )

        // 가장 높은 신뢰도를 가진 결과 찾기
        val bestMatch = patterns.maxByOrNull { it.confidence }

        // 신뢰도가 너무 낮으면 UNKNOWN 반환
        return if (bestMatch != null && bestMatch.confidence >= 0.3) {
            bestMatch
        } else {
            DetectionResult(SortAlgorithm.UNKNOWN, bestMatch?.confidence ?: 0.0)
        }
    }

    /**
     * 메서드명 + 코드 패턴 복합 감지
     */
    fun detect(methodName: String, code: String): DetectionResult {
        val nameResult = detectFromMethodName(methodName)
        val codeResult = detectFromCode(code)

        return when {
            // 둘 다 같은 알고리즘을 가리키면 신뢰도 증가
            nameResult.algorithm == codeResult.algorithm && nameResult.algorithm != SortAlgorithm.UNKNOWN -> {
                DetectionResult(
                    nameResult.algorithm,
                    maxOf(nameResult.confidence, codeResult.confidence) + 0.1
                )
            }
            // 메서드명이 더 확실하면 메서드명 우선
            nameResult.confidence > codeResult.confidence -> nameResult
            // 코드 패턴이 더 확실하면 코드 패턴 우선
            else -> codeResult
        }
    }

    // 개별 알고리즘 감지 함수들

    private fun detectBubbleSort(code: String): DetectionResult {
        // 중첩 루프 패턴
        val hasNestedLoop = code.contains(Regex("for\\s*\\(.*\\)\\s*\\{[^}]*for\\s*\\(.*\\)"))

        // arr[j] > arr[j + 1] 패턴 (버블 정렬의 핵심 패턴)
        val hasAdjacentCompare = code.contains(Regex("arr\\[\\w+\\]\\s*[><]\\s*arr\\[\\w+\\s*[+\\-]\\s*1\\]"))

        // 버블 정렬은 반드시 중첩 루프 + 인접 비교가 있어야 함
        if (!hasNestedLoop || !hasAdjacentCompare) {
            return DetectionResult(SortAlgorithm.BUBBLE_SORT, 0.0)
        }

        var confidence = 0.6  // 기본 신뢰도 (중첩 루프 + 인접 비교)

        // 스왑 패턴
        val hasSwap = code.contains("swap") || code.contains(Regex("temp\\s*=\\s*arr"))
        if (hasSwap) {
            confidence += 0.2
        }

        // n - i 패턴 (버블 정렬 최적화)
        if (code.contains(Regex("n\\s*-\\s*i"))) {
            confidence += 0.2
        }

        return DetectionResult(SortAlgorithm.BUBBLE_SORT, confidence)
    }

    private fun detectQuickSort(code: String): DetectionResult {
        var confidence = 0.0

        // pivot 키워드
        if (code.contains("pivot")) {
            confidence += 0.6
        }

        // partition 키워드
        if (code.contains("partition")) {
            confidence += 0.6
        }

        // 재귀 호출 패턴
        if (code.contains(Regex("\\w+Sort\\s*\\([^)]*,\\s*\\w+\\s*,\\s*\\w+\\)"))) {
            confidence += 0.3
        }

        // low, high 파라미터 패턴
        if (code.contains(Regex("\\(.*low.*high.*\\)")) || code.contains(Regex("\\(.*start.*end.*\\)"))) {
            confidence += 0.2
        }

        return DetectionResult(SortAlgorithm.QUICK_SORT, confidence)
    }

    private fun detectMergeSort(code: String): DetectionResult {
        var confidence = 0.0

        // merge 키워드
        if (code.contains("merge")) {
            confidence += 0.5
        }

        // 재귀 호출 2번 (좌우 분할)
        val recursiveCalls = Regex("\\w+Sort\\s*\\(").findAll(code).count()
        if (recursiveCalls >= 2) {
            confidence += 0.3
        }

        // 중간 인덱스 계산 패턴 (m, mid, middle 등)
        if (code.contains(Regex("(m|mid|middle)\\s*=.*\\+.*/"))) {
            confidence += 0.2
        }

        return DetectionResult(SortAlgorithm.MERGE_SORT, confidence)
    }

    private fun detectInsertionSort(code: String): DetectionResult {
        var confidence = 0.0

        // 단일 루프 + 내부 while 패턴
        if (code.contains(Regex("for\\s*\\(.*\\)\\s*\\{[^}]*while\\s*\\("))) {
            confidence += 0.4
        }

        // key 또는 temp 변수
        if (code.contains(Regex("(key|temp)\\s*=\\s*arr\\["))) {
            confidence += 0.3
        }

        // 왼쪽으로 이동하는 패턴
        if (code.contains(Regex("arr\\[\\w+\\s*\\+\\s*1\\]\\s*=\\s*arr\\[\\w+\\]"))) {
            confidence += 0.3
        }

        return DetectionResult(SortAlgorithm.INSERTION_SORT, confidence)
    }

    private fun detectSelectionSort(code: String): DetectionResult {
        var confidence = 0.0

        // 중첩 루프
        if (code.contains(Regex("for\\s*\\(.*\\)\\s*\\{[^}]*for\\s*\\(.*\\)"))) {
            confidence += 0.2
        }

        // min 또는 max 변수
        if (code.contains(Regex("(min|max)(Index|Idx)?\\s*="))) {
            confidence += 0.4
        }

        // 최소값 찾기 패턴
        if (code.contains(Regex("if\\s*\\(\\s*arr\\[\\w+\\]\\s*<\\s*arr\\[min"))) {
            confidence += 0.4
        }

        return DetectionResult(SortAlgorithm.SELECTION_SORT, confidence)
    }
}

/**
 * 정렬 알고리즘 종류
 */
enum class SortAlgorithm {
    BUBBLE_SORT,
    QUICK_SORT,
    MERGE_SORT,
    INSERTION_SORT,
    SELECTION_SORT,
    HEAP_SORT,
    UNKNOWN
}

/**
 * 감지 결과
 *
 * @property algorithm 감지된 알고리즘
 * @property confidence 신뢰도 (0.0 ~ 1.0)
 */
data class DetectionResult(
    val algorithm: SortAlgorithm,
    val confidence: Double
)
