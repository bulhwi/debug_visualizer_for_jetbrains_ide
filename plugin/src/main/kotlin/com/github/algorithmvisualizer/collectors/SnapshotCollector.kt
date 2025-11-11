package com.github.algorithmvisualizer.collectors

import com.google.gson.GsonBuilder

/**
 * 정렬 알고리즘의 각 단계를 스냅샷으로 캡처하는 컬렉터
 *
 * TDD로 작성된 컴포넌트로, 정렬 과정의 배열 상태를 JSON 형식으로 수집합니다.
 *
 * @see docs/sort-visualization-schema.md
 */
class SnapshotCollector {
    private val snapshots = mutableListOf<Snapshot>()
    private var algorithm: String? = null
    private var metadata: Metadata? = null

    /**
     * 배열 상태 스냅샷 캡처
     *
     * @param array 현재 배열 상태 (복사됨)
     * @param comparing 현재 비교 중인 인덱스
     * @param swapping 현재 스왑 중인 인덱스
     * @param sorted 정렬 완료된 인덱스
     * @param pivot 피벗 인덱스 (퀵소트)
     * @param action 현재 액션 타입
     * @param description 단계 설명 (옵션)
     */
    fun captureSnapshot(
        array: IntArray,
        comparing: IntArray? = null,
        swapping: IntArray? = null,
        sorted: IntArray? = null,
        pivot: Int? = null,
        action: String? = null,
        description: String? = null
    ) {
        // 배열 복사 (원본 보호)
        val arrayCopy = array.copyOf()

        val snapshot = Snapshot(
            array = arrayCopy.toList(),
            comparing = comparing?.toList(),
            swapping = swapping?.toList(),
            sorted = sorted?.toList(),
            pivot = pivot,
            action = action,
            description = description
        )

        snapshots.add(snapshot)
    }

    /**
     * 알고리즘 타입 설정
     */
    fun setAlgorithm(algorithm: String) {
        this.algorithm = algorithm
    }

    /**
     * 메타데이터 설정
     */
    fun setMetadata(language: String, expression: String, type: String) {
        this.metadata = Metadata(
            language = language,
            expression = expression,
            type = type
        )
    }

    /**
     * 스냅샷 개수 반환
     */
    fun getSnapshotCount(): Int = snapshots.size

    /**
     * 모든 스냅샷 초기화
     */
    fun clear() {
        snapshots.clear()
    }

    /**
     * JSON 문자열로 변환
     */
    fun toJson(): String {
        val data = VisualizationData(
            kind = "sort",
            timestamp = System.currentTimeMillis(),
            metadata = metadata ?: Metadata(
                language = "unknown",
                expression = "arr",
                type = "int[]"
            ),
            data = Data(
                snapshots = snapshots,
                algorithm = convertAlgorithmName(algorithm)
            )
        )

        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        return gson.toJson(data)
    }

    /**
     * 알고리즘 이름을 React 형식으로 변환
     * BUBBLE_SORT → bubble
     * QUICK_SORT → quick
     */
    private fun convertAlgorithmName(algorithm: String?): String? {
        return when (algorithm) {
            "BUBBLE_SORT" -> "bubble"
            "QUICK_SORT" -> "quick"
            "MERGE_SORT" -> "merge"
            "INSERTION_SORT" -> "insertion"
            "SELECTION_SORT" -> "selection"
            "HEAP_SORT" -> "heap"
            else -> algorithm?.lowercase()
        }
    }

    // 내부 데이터 클래스
    private data class VisualizationData(
        val kind: String,
        val timestamp: Long,
        val metadata: Metadata,
        val data: Data
    )

    private data class Metadata(
        val language: String,
        val expression: String,
        val type: String
    )

    private data class Data(
        val snapshots: List<Snapshot>,
        val algorithm: String?
    )

    private data class Snapshot(
        val array: List<Int>,
        val comparing: List<Int>? = null,
        val swapping: List<Int>? = null,
        val sorted: List<Int>? = null,
        val pivot: Int? = null,
        val action: String? = null,
        val description: String? = null
    )
}
