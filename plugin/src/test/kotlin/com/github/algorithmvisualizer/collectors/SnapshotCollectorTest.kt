package com.github.algorithmvisualizer.collectors

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SnapshotCollectorTest {
    private lateinit var collector: SnapshotCollector
    private val gson = Gson()

    @BeforeEach
    fun setUp() {
        collector = SnapshotCollector()
    }

    @Test
    fun `should create empty collector`() {
        assertThat(collector.getSnapshotCount()).isEqualTo(0)
    }

    @Test
    fun `should capture single snapshot`() {
        val array = intArrayOf(5, 2, 8, 1)

        collector.captureSnapshot(array, action = "compare")

        assertThat(collector.getSnapshotCount()).isEqualTo(1)
    }

    @Test
    fun `should capture multiple snapshots`() {
        val array1 = intArrayOf(5, 2, 8, 1)
        val array2 = intArrayOf(2, 5, 8, 1)
        val array3 = intArrayOf(2, 5, 1, 8)

        collector.captureSnapshot(array1, action = "compare")
        collector.captureSnapshot(array2, action = "swap")
        collector.captureSnapshot(array3, action = "compare")

        assertThat(collector.getSnapshotCount()).isEqualTo(3)
    }

    @Test
    fun `should capture comparing indices`() {
        val array = intArrayOf(5, 2, 8)
        val comparing = intArrayOf(0, 1)

        collector.captureSnapshot(array, comparing = comparing, action = "compare")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.has("comparing")).isTrue()
        assertThat(firstSnapshot.getAsJsonArray("comparing").size()).isEqualTo(2)
        assertThat(firstSnapshot.getAsJsonArray("comparing")[0].asInt).isEqualTo(0)
        assertThat(firstSnapshot.getAsJsonArray("comparing")[1].asInt).isEqualTo(1)
    }

    @Test
    fun `should capture swapping indices`() {
        val array = intArrayOf(2, 5, 8)
        val swapping = intArrayOf(0, 1)

        collector.captureSnapshot(array, swapping = swapping, action = "swap")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.has("swapping")).isTrue()
        assertThat(firstSnapshot.getAsJsonArray("swapping").size()).isEqualTo(2)
        assertThat(firstSnapshot.getAsJsonArray("swapping")[0].asInt).isEqualTo(0)
        assertThat(firstSnapshot.getAsJsonArray("swapping")[1].asInt).isEqualTo(1)
    }

    @Test
    fun `should capture sorted indices`() {
        val array = intArrayOf(1, 2, 5, 8)
        val sorted = intArrayOf(0, 1, 2, 3)

        collector.captureSnapshot(array, sorted = sorted, action = "sorted")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.has("sorted")).isTrue()
        assertThat(firstSnapshot.getAsJsonArray("sorted").size()).isEqualTo(4)
    }

    @Test
    fun `should capture pivot index`() {
        val array = intArrayOf(5, 2, 8, 1, 9, 3)
        val pivot = 2

        collector.captureSnapshot(array, pivot = pivot, action = "partition")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.has("pivot")).isTrue()
        assertThat(firstSnapshot.get("pivot").asInt).isEqualTo(2)
    }

    @Test
    fun `should capture action type`() {
        val array = intArrayOf(5, 2, 8)

        collector.captureSnapshot(array, action = "compare")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.has("action")).isTrue()
        assertThat(firstSnapshot.get("action").asString).isEqualTo("compare")
    }

    @Test
    fun `should capture description`() {
        val array = intArrayOf(5, 2, 8)
        val description = "Comparing arr[0]=5 and arr[1]=2"

        collector.captureSnapshot(array, action = "compare", description = description)

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.has("description")).isTrue()
        assertThat(firstSnapshot.get("description").asString).isEqualTo(description)
    }

    @Test
    fun `should export to JSON with correct structure`() {
        val array = intArrayOf(5, 2, 8, 1)
        collector.captureSnapshot(array, comparing = intArrayOf(0, 1), action = "compare")

        val json = collector.toJson()

        assertThat(json).contains("\"kind\": \"sort\"")
        assertThat(json).contains("\"snapshots\"")
        assertThat(json).contains("\"array\"")
    }

    @Test
    fun `should include algorithm in JSON`() {
        collector.setAlgorithm("bubble")

        val array = intArrayOf(5, 2, 8)
        collector.captureSnapshot(array, action = "compare")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val data = json.getAsJsonObject("data")

        assertThat(data.get("algorithm").asString).isEqualTo("bubble")
    }

    @Test
    fun `should handle empty array`() {
        val array = intArrayOf()

        collector.captureSnapshot(array, action = "sorted")

        assertThat(collector.getSnapshotCount()).isEqualTo(1)

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.getAsJsonArray("array").size()).isEqualTo(0)
    }

    @Test
    fun `should handle single element array`() {
        val array = intArrayOf(42)

        collector.captureSnapshot(array, sorted = intArrayOf(0), action = "sorted")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject

        assertThat(firstSnapshot.getAsJsonArray("array").size()).isEqualTo(1)
        assertThat(firstSnapshot.getAsJsonArray("array")[0].asInt).isEqualTo(42)
    }

    @Test
    fun `should clear snapshots`() {
        val array = intArrayOf(5, 2, 8)
        collector.captureSnapshot(array, action = "compare")
        collector.captureSnapshot(array, action = "swap")

        assertThat(collector.getSnapshotCount()).isEqualTo(2)

        collector.clear()

        assertThat(collector.getSnapshotCount()).isEqualTo(0)
    }

    @Test
    fun `should set metadata`() {
        collector.setMetadata(
            language = "java",
            expression = "arr",
            type = "int[]"
        )

        val array = intArrayOf(5, 2, 8)
        collector.captureSnapshot(array, action = "compare")

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val metadata = json.getAsJsonObject("metadata")

        assertThat(metadata.get("language").asString).isEqualTo("java")
        assertThat(metadata.get("expression").asString).isEqualTo("arr")
        assertThat(metadata.get("type").asString).isEqualTo("int[]")
    }

    @Test
    fun `should create array copy to prevent mutation`() {
        val array = intArrayOf(5, 2, 8)
        collector.captureSnapshot(array, action = "compare")

        // 원본 배열 변경
        array[0] = 100

        val json = gson.fromJson(collector.toJson(), JsonObject::class.java)
        val snapshots = json.getAsJsonObject("data").getAsJsonArray("snapshots")
        val firstSnapshot = snapshots[0].asJsonObject
        val capturedArray = firstSnapshot.getAsJsonArray("array")

        // 캡처된 배열은 변경되지 않아야 함
        assertThat(capturedArray[0].asInt).isEqualTo(5)
    }
}
