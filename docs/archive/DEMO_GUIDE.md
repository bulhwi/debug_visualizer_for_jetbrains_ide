# SortVisualizer 데모 가이드

**작성일**: 2025-11-11
**목적**: #12 정렬 알고리즘 시각화 컴포넌트 동작 확인

---

## 🎯 개요

SortVisualizer 컴포넌트를 브라우저에서 직접 테스트해볼 수 있는 가이드입니다.

---

## 🚀 빠른 시작

### 1. 개발 서버 실행

```bash
cd visualizer-ui
npm run dev
```

서버가 시작되면 브라우저에서 `http://localhost:5173` 열림

### 2. App.tsx에 데모 코드 추가

`visualizer-ui/src/App.tsx` 파일을 열고 다음 코드로 교체:

```tsx
import { SortVisualizer } from './components/SortVisualizer'
import type { SortVisualizationData } from './types/sort'
import './styles/App.css'

function App() {
  // 버블 정렬 데모 데이터
  const bubbleSortDemo: SortVisualizationData = {
    kind: 'sort',
    timestamp: Date.now(),
    metadata: {
      language: 'java',
      expression: 'arr',
      type: 'int[]'
    },
    data: {
      algorithm: 'bubble',
      snapshots: [
        {
          array: [5, 2, 8, 1, 9],
          action: 'compare',
          description: '초기 배열'
        },
        {
          array: [5, 2, 8, 1, 9],
          comparing: [0, 1],
          action: 'compare',
          description: 'arr[0]=5와 arr[1]=2 비교'
        },
        {
          array: [2, 5, 8, 1, 9],
          swapping: [0, 1],
          action: 'swap',
          description: 'arr[0]과 arr[1] 교환'
        },
        {
          array: [2, 5, 8, 1, 9],
          comparing: [1, 2],
          action: 'compare',
          description: 'arr[1]=5와 arr[2]=8 비교 (교환 안함)'
        },
        {
          array: [2, 5, 8, 1, 9],
          comparing: [2, 3],
          action: 'compare',
          description: 'arr[2]=8과 arr[3]=1 비교'
        },
        {
          array: [2, 5, 1, 8, 9],
          swapping: [2, 3],
          action: 'swap',
          description: 'arr[2]와 arr[3] 교환'
        },
        {
          array: [2, 5, 1, 8, 9],
          comparing: [3, 4],
          action: 'compare',
          description: 'arr[3]=8과 arr[4]=9 비교 (교환 안함)'
        },
        {
          array: [2, 5, 1, 8, 9],
          sorted: [4],
          description: 'arr[4]=9 정렬 완료'
        },
        {
          array: [2, 1, 5, 8, 9],
          swapping: [1, 2],
          action: 'swap',
          description: '2라운드: arr[1]과 arr[2] 교환'
        },
        {
          array: [2, 1, 5, 8, 9],
          sorted: [3, 4],
          description: 'arr[3], arr[4] 정렬 완료'
        },
        {
          array: [1, 2, 5, 8, 9],
          swapping: [0, 1],
          action: 'swap',
          description: '3라운드: arr[0]과 arr[1] 교환'
        },
        {
          array: [1, 2, 5, 8, 9],
          sorted: [0, 1, 2, 3, 4],
          action: 'sorted',
          description: '정렬 완료! 🎉'
        }
      ]
    }
  }

  // 퀵소트 데모 데이터
  const quickSortDemo: SortVisualizationData = {
    kind: 'sort',
    timestamp: Date.now(),
    metadata: {
      language: 'kotlin',
      expression: 'nums',
      type: 'IntArray'
    },
    data: {
      algorithm: 'quick',
      snapshots: [
        {
          array: [5, 2, 8, 1, 9, 3],
          action: 'compare',
          description: '초기 배열'
        },
        {
          array: [5, 2, 8, 1, 9, 3],
          pivot: 2,
          action: 'partition',
          description: '피벗 선택: arr[2]=8'
        },
        {
          array: [5, 2, 3, 1, 9, 8],
          pivot: 5,
          swapping: [2, 5],
          action: 'swap',
          description: '피벗을 올바른 위치로 이동'
        },
        {
          array: [1, 2, 3, 5, 9, 8],
          pivot: 3,
          sorted: [3],
          action: 'partition',
          description: '파티션 완료, 왼쪽/오른쪽 재귀'
        },
        {
          array: [1, 2, 3, 5, 8, 9],
          swapping: [4, 5],
          action: 'swap',
          description: '오른쪽 파티션 정렬'
        },
        {
          array: [1, 2, 3, 5, 8, 9],
          sorted: [0, 1, 2, 3, 4, 5],
          action: 'sorted',
          description: '정렬 완료! 🎉'
        }
      ]
    }
  }

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h1>🎨 정렬 알고리즘 시각화 데모</h1>

      <section style={{ marginBottom: '40px' }}>
        <h2>1. 버블 정렬 (Bubble Sort)</h2>
        <p>인접한 두 요소를 비교하여 정렬하는 알고리즘</p>
        <SortVisualizer data={bubbleSortDemo} autoPlay={false} />
      </section>

      <hr style={{ margin: '40px 0' }} />

      <section style={{ marginBottom: '40px' }}>
        <h2>2. 퀵 정렬 (Quick Sort)</h2>
        <p>피벗을 기준으로 파티셔닝하는 알고리즘</p>
        <SortVisualizer data={quickSortDemo} autoPlay={false} />
      </section>

      <hr style={{ margin: '40px 0' }} />

      <section>
        <h2>3. 자동 재생 데모</h2>
        <p>속도를 4배로 설정하고 자동 재생</p>
        <SortVisualizer data={bubbleSortDemo} autoPlay={true} defaultSpeed={4} />
      </section>
    </div>
  )
}

export default App
```

### 3. 브라우저에서 확인

저장하면 자동으로 리로드되고 3개의 시각화를 볼 수 있어:

1. **버블 정렬** - 수동 컨트롤로 단계별 확인
2. **퀵 정렬** - 피벗 기반 파티셔닝 확인
3. **자동 재생 데모** - 빠른 속도로 전체 과정 확인

---

## 🎮 UI 컨트롤 사용법

### 버튼
- **◀◀ Previous**: 이전 단계로 이동
- **▶ Play / ⏸ Pause**: 자동 재생 시작/정지
- **Next ▶▶**: 다음 단계로 이동

### 속도 조절
- **0.5x**: 느린 속도 (2초당 1단계)
- **1x**: 기본 속도 (1초당 1단계)
- **2x**: 2배 속도 (0.5초당 1단계)
- **4x**: 4배 속도 (0.25초당 1단계)

### 색상 의미
- **회청색 (#4ecdc4)**: 기본 상태 (미정렬)
- **주황색 (#ff922b)**: 현재 비교 중
- **빨간색 (#ff6b6b)**: 현재 교환 중
- **초록색 (#51cf66)**: 정렬 완료
- **보라색 (#9775fa)**: 피벗 (퀵소트)

---

## 📝 더 많은 예제

### 병합 정렬 예제 추가

```tsx
const mergeSortDemo: SortVisualizationData = {
  kind: 'sort',
  timestamp: Date.now(),
  metadata: {
    language: 'python',
    expression: 'arr',
    type: 'list'
  },
  data: {
    algorithm: 'merge',
    snapshots: [
      {
        array: [5, 2, 8, 1],
        action: 'partition',
        description: '초기 배열'
      },
      {
        array: [5, 2, 8, 1],
        partitions: [
          { start: 0, end: 1, label: 'left', color: '#ffd43b' },
          { start: 2, end: 3, label: 'right', color: '#74c0fc' }
        ],
        action: 'partition',
        description: '[5,2]와 [8,1]로 분할'
      },
      {
        array: [2, 5, 8, 1],
        action: 'merge',
        description: '[5,2] 병합 → [2,5]'
      },
      {
        array: [2, 5, 1, 8],
        action: 'merge',
        description: '[8,1] 병합 → [1,8]'
      },
      {
        array: [1, 2, 5, 8],
        action: 'merge',
        description: '[2,5]와 [1,8] 병합'
      },
      {
        array: [1, 2, 5, 8],
        sorted: [0, 1, 2, 3],
        action: 'sorted',
        description: '정렬 완료! 🎉'
      }
    ]
  }
}
```

### 큰 배열 테스트

```tsx
const largeSortDemo: SortVisualizationData = {
  kind: 'sort',
  timestamp: Date.now(),
  metadata: {
    language: 'java',
    expression: 'arr',
    type: 'int[]'
  },
  data: {
    algorithm: 'bubble',
    snapshots: [
      {
        array: [64, 34, 25, 12, 22, 11, 90, 88, 45, 50, 33, 17, 10, 5, 78, 65],
        description: '초기 배열 (16개 요소)'
      },
      // ... 중간 단계들 ...
      {
        array: [5, 10, 11, 12, 17, 22, 25, 33, 34, 45, 50, 64, 65, 78, 88, 90],
        sorted: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
        action: 'sorted',
        description: '정렬 완료!'
      }
    ]
  }
}
```

---

## 🧪 테스트 실행

### 단위 테스트 실행
```bash
cd visualizer-ui
npm test
```

**예상 결과**:
```
✓ src/components/SortVisualizer.test.tsx (20 tests) 471ms
  ✓ Rendering (4)
  ✓ Animation Controls (6)
  ✓ Snapshot Display (4)
  ✓ Speed Control (2)
  ✓ Edge Cases (3)
  ✓ Auto-play (1)

Test Files  1 passed (1)
     Tests  20 passed (20)
```

### 특정 테스트만 실행
```bash
# 렌더링 테스트만
npm test -- -t "Rendering"

# 애니메이션 컨트롤 테스트만
npm test -- -t "Animation Controls"
```

---

## 🔧 커스텀 데이터 만들기

### 1. 간단한 스냅샷 생성

```typescript
const myCustomSort: SortVisualizationData = {
  kind: 'sort',
  timestamp: Date.now(),
  metadata: {
    language: 'java',
    expression: 'myArray',
    type: 'int[]'
  },
  data: {
    algorithm: 'bubble',
    snapshots: [
      // 1단계
      {
        array: [3, 1, 2],
        action: 'compare'
      },
      // 2단계
      {
        array: [3, 1, 2],
        comparing: [0, 1],
        action: 'compare'
      },
      // 3단계
      {
        array: [1, 3, 2],
        swapping: [0, 1],
        action: 'swap'
      },
      // 완료
      {
        array: [1, 2, 3],
        sorted: [0, 1, 2],
        action: 'sorted'
      }
    ]
  }
}
```

### 2. Kotlin에서 JSON 생성

```kotlin
// plugin에서 실행 가능한 예제
fun main() {
    val collector = SnapshotCollector()
    collector.setAlgorithm("bubble")
    collector.setMetadata("java", "arr", "int[]")

    val array = intArrayOf(5, 2, 8, 1)

    // 초기 상태
    collector.captureSnapshot(array, action = "compare", description = "초기 배열")

    // 비교
    collector.captureSnapshot(array, comparing = intArrayOf(0, 1), action = "compare")

    // 스왑
    array[0] = 2
    array[1] = 5
    collector.captureSnapshot(array, swapping = intArrayOf(0, 1), action = "swap")

    // JSON 출력
    println(collector.toJson())
}
```

출력된 JSON을 복사해서 React 앱에서 사용 가능!

---

## 🐛 트러블슈팅

### 문제 1: 개발 서버가 시작되지 않음
```bash
cd visualizer-ui
npm install  # 의존성 재설치
npm run dev
```

### 문제 2: 타입 에러
```bash
cd visualizer-ui
npm run type-check  # TypeScript 타입 체크
```

### 문제 3: 시각화가 보이지 않음
- 브라우저 개발자 도구 (F12) 열기
- Console 탭에서 에러 확인
- `data` prop이 올바른 형식인지 확인

### 문제 4: 애니메이션이 작동하지 않음
- `autoPlay={true}` prop 확인
- `snapshots` 배열에 2개 이상의 요소가 있는지 확인
- 브라우저 콘솔에서 에러 확인

---

## 📚 추가 리소스

### 관련 문서
- [sort-visualization-schema.md](./sort-visualization-schema.md) - 데이터 스키마 상세 명세
- [TESTING.md](./TESTING.md) - 테스트 가이드
- [PRD.md](./PRD.md) - 제품 요구사항

### 코드 위치
- **React 컴포넌트**: `visualizer-ui/src/components/SortVisualizer.tsx`
- **테스트**: `visualizer-ui/src/components/SortVisualizer.test.tsx`
- **타입 정의**: `visualizer-ui/src/types/sort.ts`
- **Kotlin 컬렉터**: `plugin/src/main/kotlin/com/github/algorithmvisualizer/collectors/SnapshotCollector.kt`

---

## 💡 팁

### 1. 디버깅 모드
개발자 도구에서 현재 스냅샷 확인:
```javascript
// 브라우저 콘솔에서
console.log(document.querySelector('[data-testid="sort-visualizer"]'))
```

### 2. 스냅샷 개수 확인
```typescript
console.log(`총 ${data.data.snapshots.length}개의 스냅샷`)
```

### 3. 애니메이션 속도 동적 변경
컴포넌트에서 속도 조절 UI를 사용하거나:
```typescript
<SortVisualizer data={data} defaultSpeed={2} />
```

### 4. 커스텀 크기
```typescript
<SortVisualizer
  data={data}
  width={1000}
  height={500}
/>
```

---

**즐거운 테스트 되세요!** 🎉

문제가 있으면 이슈를 남겨주세요: https://github.com/bulhwi/debug_visualizer_for_jetbrains_ide/issues
