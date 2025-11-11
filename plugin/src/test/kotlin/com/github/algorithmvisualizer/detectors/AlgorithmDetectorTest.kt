package com.github.algorithmvisualizer.detectors

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AlgorithmDetectorTest {
    private lateinit var detector: AlgorithmDetector

    @BeforeEach
    fun setUp() {
        detector = AlgorithmDetector()
    }

    // 변수명 기반 감지 테스트
    @Test
    fun `should detect bubble sort from method name`() {
        val methodName = "bubbleSort"

        val result = detector.detectFromMethodName(methodName)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.BUBBLE_SORT)
        assertThat(result.confidence).isGreaterThan(0.8)
    }

    @Test
    fun `should detect quick sort from method name`() {
        val methodName = "quickSort"

        val result = detector.detectFromMethodName(methodName)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.QUICK_SORT)
        assertThat(result.confidence).isGreaterThan(0.8)
    }

    @Test
    fun `should detect merge sort from method name`() {
        val methodName = "mergeSort"

        val result = detector.detectFromMethodName(methodName)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.MERGE_SORT)
        assertThat(result.confidence).isGreaterThan(0.8)
    }

    @Test
    fun `should detect insertion sort from method name`() {
        val methodName = "insertionSort"

        val result = detector.detectFromMethodName(methodName)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.INSERTION_SORT)
        assertThat(result.confidence).isGreaterThan(0.8)
    }

    @Test
    fun `should detect selection sort from method name`() {
        val methodName = "selectionSort"

        val result = detector.detectFromMethodName(methodName)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.SELECTION_SORT)
        assertThat(result.confidence).isGreaterThan(0.8)
    }

    // 코드 패턴 기반 감지 테스트
    @Test
    fun `should detect bubble sort from code pattern`() {
        val code = """
            void sort(int[] arr) {
                int n = arr.length;
                for (int i = 0; i < n - 1; i++) {
                    for (int j = 0; j < n - i - 1; j++) {
                        if (arr[j] > arr[j + 1]) {
                            int temp = arr[j];
                            arr[j] = arr[j + 1];
                            arr[j + 1] = temp;
                        }
                    }
                }
            }
        """.trimIndent()

        val result = detector.detectFromCode(code)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.BUBBLE_SORT)
        assertThat(result.confidence).isGreaterThan(0.7)
    }

    @Test
    fun `should detect quick sort from code pattern with pivot`() {
        val code = """
            void quickSort(int[] arr, int low, int high) {
                if (low < high) {
                    int pivot = arr[high];
                    int i = (low - 1);
                    for (int j = low; j < high; j++) {
                        if (arr[j] < pivot) {
                            i++;
                            swap(arr, i, j);
                        }
                    }
                    swap(arr, i + 1, high);
                }
            }
        """.trimIndent()

        val result = detector.detectFromCode(code)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.QUICK_SORT)
        assertThat(result.confidence).isGreaterThan(0.7)
    }

    @Test
    fun `should detect merge sort from code pattern with merge`() {
        val code = """
            void mergeSort(int[] arr, int l, int r) {
                if (l < r) {
                    int m = l + (r - l) / 2;
                    mergeSort(arr, l, m);
                    mergeSort(arr, m + 1, r);
                    merge(arr, l, m, r);
                }
            }
        """.trimIndent()

        val result = detector.detectFromCode(code)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.MERGE_SORT)
        assertThat(result.confidence).isGreaterThan(0.7)
    }

    // False positive 방지 테스트
    @Test
    fun `should not detect sort algorithm from unrelated code`() {
        val code = """
            void processData(int[] arr) {
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = arr[i] * 2;
                }
            }
        """.trimIndent()

        val result = detector.detectFromCode(code)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.UNKNOWN)
        assertThat(result.confidence).isLessThan(0.5)
    }

    @Test
    fun `should return unknown for generic method names`() {
        val methodName = "process"

        val result = detector.detectFromMethodName(methodName)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.UNKNOWN)
        assertThat(result.confidence).isLessThan(0.5)
    }

    // 복합 감지 테스트 (메서드명 + 코드 패턴)
    @Test
    fun `should have high confidence when both method name and code pattern match`() {
        val methodName = "bubbleSort"
        val code = """
            void bubbleSort(int[] arr) {
                for (int i = 0; i < n - 1; i++) {
                    for (int j = 0; j < n - i - 1; j++) {
                        if (arr[j] > arr[j + 1]) {
                            swap(arr, j, j + 1);
                        }
                    }
                }
            }
        """.trimIndent()

        val result = detector.detect(methodName, code)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.BUBBLE_SORT)
        assertThat(result.confidence).isGreaterThan(0.9)
    }

    @Test
    fun `should detect algorithm even when method name is generic but code pattern is clear`() {
        val methodName = "sort"
        val code = """
            void sort(int[] arr) {
                int pivot = arr[high];
                partition(arr, low, high);
            }
        """.trimIndent()

        val result = detector.detect(methodName, code)

        assertThat(result.algorithm).isEqualTo(SortAlgorithm.QUICK_SORT)
        assertThat(result.confidence).isGreaterThan(0.6)
    }

    // 신뢰도 점수 테스트
    @Test
    fun `should return higher confidence for specific method names`() {
        val specificResult = detector.detectFromMethodName("bubbleSort")
        val genericResult = detector.detectFromMethodName("sort")

        assertThat(specificResult.confidence).isGreaterThan(genericResult.confidence)
    }

    @Test
    fun `should adjust confidence based on code complexity`() {
        val simpleCode = """
            void sort(int[] arr) {
                for (int i = 0; i < n; i++) {
                    arr[i] = arr[i];
                }
            }
        """.trimIndent()

        val complexCode = """
            void sort(int[] arr) {
                for (int i = 0; i < n - 1; i++) {
                    for (int j = 0; j < n - i - 1; j++) {
                        if (arr[j] > arr[j + 1]) {
                            swap(arr, j, j + 1);
                        }
                    }
                }
            }
        """.trimIndent()

        val simpleResult = detector.detectFromCode(simpleCode)
        val complexResult = detector.detectFromCode(complexCode)

        assertThat(complexResult.confidence).isGreaterThan(simpleResult.confidence)
    }
}
