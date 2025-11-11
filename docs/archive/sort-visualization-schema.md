# 정렬 알고리즘 시각화 스키마 (Phase 2)

**작성일**: 2025-11-11
**이슈**: #12

---

## 개요

정렬 알고리즘의 각 단계를 캡처한 **스냅샷 기반 시각화**를 위한 데이터 스키마입니다.

## 스냅샷 기반 스키마

### TypeScript 타입 정의

```typescript
interface SortVisualizationData extends VisualizationData {
  kind: 'sort';
  data: {
    snapshots: SortSnapshot[];
    algorithm?: 'bubble' | 'quick' | 'merge' | 'insertion' | 'selection' | 'heap';
  };
}

interface SortSnapshot {
  array: number[];
  comparing?: number[];     // 현재 비교 중인 인덱스
  swapping?: number[];      // 현재 스왑 중인 인덱스
  sorted?: number[];        // 정렬 완료된 인덱스
  pivot?: number;           // 퀵소트 피벗
  partitions?: Partition[]; // 병합/퀵소트 파티션
  action?: 'compare' | 'swap' | 'merge' | 'partition' | 'sorted';
  description?: string;     // 단계 설명 (옵션)
}

interface Partition {
  start: number;
  end: number;
  label?: string;
  color?: string;
}
```

---

## 알고리즘별 예제

### 1. 버블 정렬 (Bubble Sort)

**특징**:
- 인접한 두 요소를 비교하고 교환
- `comparing`과 `swapping` 필드 사용

```json
{
  "kind": "sort",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "java",
    "expression": "arr",
    "type": "int[]"
  },
  "data": {
    "algorithm": "bubble",
    "snapshots": [
      {
        "array": [5, 2, 8, 1, 9],
        "comparing": [0, 1],
        "action": "compare",
        "description": "Comparing arr[0]=5 and arr[1]=2"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "swapping": [0, 1],
        "action": "swap",
        "description": "Swapping arr[0] and arr[1]"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "comparing": [1, 2],
        "action": "compare",
        "description": "Comparing arr[1]=5 and arr[2]=8"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "comparing": [2, 3],
        "action": "compare",
        "description": "Comparing arr[2]=8 and arr[3]=1"
      },
      {
        "array": [2, 5, 1, 8, 9],
        "swapping": [2, 3],
        "action": "swap",
        "description": "Swapping arr[2] and arr[3]"
      },
      {
        "array": [2, 5, 1, 8, 9],
        "sorted": [4],
        "description": "arr[4]=9 is in correct position"
      },
      {
        "array": [1, 2, 5, 8, 9],
        "sorted": [0, 1, 2, 3, 4],
        "action": "sorted",
        "description": "Sorting complete!"
      }
    ]
  },
  "config": {
    "animation": true,
    "speed": 1.0
  }
}
```

---

### 2. 퀵소트 (Quick Sort)

**특징**:
- 피벗을 선택하고 파티셔닝
- `pivot`과 `partitions` 필드 사용

```json
{
  "kind": "sort",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "kotlin",
    "expression": "nums",
    "type": "IntArray"
  },
  "data": {
    "algorithm": "quick",
    "snapshots": [
      {
        "array": [5, 2, 8, 1, 9, 3],
        "pivot": 2,
        "partitions": [
          {"start": 0, "end": 5, "label": "initial", "color": "#e9ecef"}
        ],
        "action": "partition",
        "description": "Pivot: arr[2]=8"
      },
      {
        "array": [5, 2, 3, 1, 9, 8],
        "pivot": 2,
        "comparing": [0, 5],
        "action": "compare",
        "description": "Comparing arr[0]=5 with pivot=8"
      },
      {
        "array": [5, 2, 3, 1, 8, 9],
        "pivot": 4,
        "swapping": [2, 4],
        "action": "swap",
        "description": "Placing pivot in correct position"
      },
      {
        "array": [1, 2, 3, 5, 8, 9],
        "pivot": 2,
        "partitions": [
          {"start": 0, "end": 2, "label": "left", "color": "#ffd43b"},
          {"start": 3, "end": 5, "label": "right", "color": "#74c0fc"}
        ],
        "action": "partition",
        "description": "Partition complete, recursing on subarrays"
      },
      {
        "array": [1, 2, 3, 5, 8, 9],
        "sorted": [0, 1, 2, 3, 4, 5],
        "action": "sorted",
        "description": "Sorting complete!"
      }
    ]
  }
}
```

---

### 3. 병합 정렬 (Merge Sort)

**특징**:
- 배열을 분할하고 병합
- `partitions` 필드로 서브배열 표시

```json
{
  "kind": "sort",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "python",
    "expression": "arr",
    "type": "list"
  },
  "data": {
    "algorithm": "merge",
    "snapshots": [
      {
        "array": [5, 2, 8, 1],
        "partitions": [
          {"start": 0, "end": 3, "label": "initial", "color": "#e9ecef"}
        ],
        "action": "partition",
        "description": "Initial array"
      },
      {
        "array": [5, 2, 8, 1],
        "partitions": [
          {"start": 0, "end": 1, "label": "left", "color": "#ffd43b"},
          {"start": 2, "end": 3, "label": "right", "color": "#74c0fc"}
        ],
        "action": "partition",
        "description": "Dividing into [5,2] and [8,1]"
      },
      {
        "array": [2, 5, 8, 1],
        "partitions": [
          {"start": 0, "end": 1, "label": "merged", "color": "#51cf66"}
        ],
        "action": "merge",
        "description": "Merging [5,2] -> [2,5]"
      },
      {
        "array": [2, 5, 1, 8],
        "partitions": [
          {"start": 2, "end": 3, "label": "merged", "color": "#51cf66"}
        ],
        "action": "merge",
        "description": "Merging [8,1] -> [1,8]"
      },
      {
        "array": [1, 2, 5, 8],
        "action": "merge",
        "description": "Merging [2,5] and [1,8]"
      },
      {
        "array": [1, 2, 5, 8],
        "sorted": [0, 1, 2, 3],
        "action": "sorted",
        "description": "Sorting complete!"
      }
    ]
  }
}
```

---

### 4. 삽입 정렬 (Insertion Sort)

**특징**:
- 정렬된 부분과 미정렬 부분
- `sorted` 필드로 정렬된 영역 표시

```json
{
  "kind": "sort",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "java",
    "expression": "arr",
    "type": "int[]"
  },
  "data": {
    "algorithm": "insertion",
    "snapshots": [
      {
        "array": [5, 2, 8, 1, 9],
        "sorted": [0],
        "description": "First element is trivially sorted"
      },
      {
        "array": [5, 2, 8, 1, 9],
        "comparing": [0, 1],
        "sorted": [0],
        "action": "compare",
        "description": "Inserting arr[1]=2 into sorted portion"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "sorted": [0, 1],
        "action": "swap",
        "description": "arr[1] inserted at position 0"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "comparing": [1, 2],
        "sorted": [0, 1],
        "action": "compare",
        "description": "arr[2]=8 is already in correct position"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "sorted": [0, 1, 2],
        "description": "arr[2] stays in place"
      },
      {
        "array": [1, 2, 5, 8, 9],
        "sorted": [0, 1, 2, 3, 4],
        "action": "sorted",
        "description": "Sorting complete!"
      }
    ]
  }
}
```

---

## 색상 코딩

### 상태별 색상

```typescript
enum SortVisualizationColor {
  DEFAULT = '#4ecdc4',        // 기본 상태 (미정렬)
  COMPARING = '#ff922b',      // 비교 중
  SWAPPING = '#ff6b6b',       // 스왑 중
  SORTED = '#51cf66',         // 정렬 완료
  PIVOT = '#9775fa',          // 피벗 (퀵소트)
  PARTITION_LEFT = '#ffd43b', // 왼쪽 파티션
  PARTITION_RIGHT = '#74c0fc',// 오른쪽 파티션
  MERGING = '#ff8787'         // 병합 중
}
```

### 시각화 예시

```
[5][2][8][1][9]  <- DEFAULT (회청색)
[5][2][8][1][9]  <- comparing=[0,1] → arr[0],arr[1] 주황색
[2][5][8][1][9]  <- swapping=[0,1] → arr[0],arr[1] 빨간색
[1][2][5][8][9]  <- sorted=[0,1,2,3,4] → 모두 초록색
```

---

## UI 컨트롤 요구사항

### 재생 컨트롤

```typescript
interface AnimationControl {
  currentStep: number;      // 현재 스냅샷 인덱스
  totalSteps: number;       // 총 스냅샷 수
  isPlaying: boolean;       // 재생 중 여부
  speed: number;            // 재생 속도 (0.5x ~ 4x)
}

interface Actions {
  play(): void;
  pause(): void;
  next(): void;
  prev(): void;
  reset(): void;
  setSpeed(speed: number): void;
  goToStep(step: number): void;
}
```

### UI 레이아웃

```
┌────────────────────────────────────────────┐
│ Sort Visualizer - Bubble Sort              │
├────────────────────────────────────────────┤
│                                            │
│   [막대 그래프 영역]                         │
│   ┃  ┃     ┃ ┃        ┃                   │
│   ┃  ┃     ┃ ┃        ┃                   │
│   ┃  ┃  ┃  ┃ ┃  ┃     ┃                   │
│   ┃  ┃  ┃  ┃ ┃  ┃  ┃  ┃                   │
│   5  2  8  1 9  3  7  6                   │
│                                            │
│   Description: Comparing arr[0]=5 and...   │
├────────────────────────────────────────────┤
│ [◀◀][▶/⏸][▶▶]  Step: 3/24  Speed: [1x▼]  │
└────────────────────────────────────────────┘
```

---

## Kotlin 데이터 수집

### SnapshotCollector 인터페이스

```kotlin
interface SnapshotCollector {
    /**
     * 현재 배열 상태를 스냅샷으로 캡처
     */
    fun captureSnapshot(
        array: IntArray,
        comparing: IntArray? = null,
        swapping: IntArray? = null,
        sorted: IntArray? = null,
        pivot: Int? = null,
        action: String? = null,
        description: String? = null
    )

    /**
     * 모든 스냅샷을 JSON으로 변환
     */
    fun toJson(): String
}
```

### 사용 예시 (Java 코드에서)

```java
// 버블 정렬 예시
void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            // 스냅샷 캡처: 비교
            captureSnapshot(arr, new int[]{j, j+1}, null, null, null, "compare");

            if (arr[j] > arr[j + 1]) {
                // 스왑
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;

                // 스냅샷 캡처: 스왑
                captureSnapshot(arr, null, new int[]{j, j+1}, null, null, "swap");
            }
        }
    }
    // 스냅샷 캡처: 정렬 완료
    captureSnapshot(arr, null, null, IntStream.range(0, n).toArray(), null, "sorted");
}
```

---

## React 컴포넌트 API

### SortVisualizer Props

```typescript
interface SortVisualizerProps {
  data: SortVisualizationData;
  width?: number;
  height?: number;
  autoPlay?: boolean;
  defaultSpeed?: number;
  onStepChange?: (step: number) => void;
}

export function SortVisualizer({
  data,
  width = 800,
  height = 400,
  autoPlay = false,
  defaultSpeed = 1.0,
  onStepChange
}: SortVisualizerProps) {
  // 구현...
}
```

---

## 성능 고려사항

### 대용량 배열 처리

- **최대 요소 수**: 100개 (시각적으로 구분 가능한 한계)
- **스냅샷 최대 수**: 1000개 (메모리 제한)
- **애니메이션 FPS**: 60fps 목표
- **디바운싱**: 빠른 스테핑 시 렌더링 스킵

### 최적화 전략

```typescript
// Virtual scrolling for large arrays
if (array.length > 100) {
  renderVisibleElements(startIdx, endIdx);
}

// Throttle animation updates
const throttledUpdate = throttle(updateVisualization, 16); // 60fps
```

---

## 테스트 전략

### 단위 테스트

```typescript
describe('SortSnapshot', () => {
  it('should parse valid snapshot data', () => {
    const snapshot: SortSnapshot = {
      array: [3, 1, 2],
      comparing: [0, 1],
      action: 'compare'
    };
    expect(isValidSnapshot(snapshot)).toBe(true);
  });

  it('should reject invalid snapshot data', () => {
    const invalid = { array: [] }; // 빈 배열
    expect(isValidSnapshot(invalid)).toBe(false);
  });
});
```

### 통합 테스트

```typescript
describe('SortVisualizer', () => {
  it('should render all snapshots', () => {
    const data: SortVisualizationData = {
      kind: 'sort',
      data: {
        snapshots: [
          { array: [3, 1, 2], comparing: [0, 1] },
          { array: [1, 3, 2], swapping: [0, 1] }
        ]
      }
    };

    render(<SortVisualizer data={data} />);
    expect(screen.getByText('Step 1 / 2')).toBeInTheDocument();
  });
});
```

---

## 확장 가능성

### 추가 알고리즘 지원

```typescript
type SortAlgorithm =
  | 'bubble'      // ✅ Phase 2
  | 'quick'       // ✅ Phase 2
  | 'merge'       // ✅ Phase 2
  | 'insertion'   // 🔲 Phase 3
  | 'selection'   // 🔲 Phase 3
  | 'heap'        // 🔲 Phase 3
  | 'radix'       // 🔲 Phase 4
  | 'counting';   // 🔲 Phase 4
```

### 커스텀 메타데이터

```typescript
interface ExtendedSortSnapshot extends SortSnapshot {
  metadata?: {
    comparisons: number;    // 누적 비교 횟수
    swaps: number;          // 누적 스왑 횟수
    timeComplexity: string; // "O(n^2)"
    spaceComplexity: string;// "O(1)"
  };
}
```

---

**마지막 업데이트**: 2025-11-11
**다음 단계**: React SortVisualizer 컴포넌트 TDD 작성
