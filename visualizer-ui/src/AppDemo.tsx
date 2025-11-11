import { SortVisualizer } from './components/SortVisualizer'
import type { SortVisualizationData } from './types/sort'
import './styles/App.css'

/**
 * SortVisualizer 데모 앱
 *
 * 사용법:
 * 1. visualizer-ui/src/main.tsx에서 App 대신 AppDemo를 import
 * 2. npm run dev 실행
 * 3. http://localhost:5173 열기
 */
function AppDemo() {
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
          description: 'arr[0]과 arr[1] 교환 (5 ↔ 2)'
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
          description: 'arr[2]와 arr[3] 교환 (8 ↔ 1)'
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
          description: 'arr[4]=9가 올바른 위치에 도달 (1라운드 완료)'
        },
        {
          array: [2, 1, 5, 8, 9],
          swapping: [1, 2],
          action: 'swap',
          description: '2라운드: arr[1]과 arr[2] 교환 (5 ↔ 1)'
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
          description: '3라운드: arr[0]과 arr[1] 교환 (2 ↔ 1)'
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
          array: [5, 2, 8, 1, 9, 3],
          pivot: 2,
          comparing: [0, 2],
          action: 'compare',
          description: 'arr[0]=5를 피벗 8과 비교 (작음, 왼쪽)'
        },
        {
          array: [5, 2, 8, 1, 9, 3],
          pivot: 2,
          comparing: [5, 2],
          action: 'compare',
          description: 'arr[5]=3을 피벗 8과 비교 (작음, 왼쪽)'
        },
        {
          array: [5, 2, 3, 1, 9, 8],
          pivot: 5,
          swapping: [2, 5],
          action: 'swap',
          description: '피벗을 올바른 위치로 이동 (8 ↔ 3)'
        },
        {
          array: [1, 2, 3, 5, 9, 8],
          pivot: 3,
          sorted: [5],
          action: 'partition',
          description: '파티션 완료. arr[5]=8은 최종 위치'
        },
        {
          array: [1, 2, 3, 5, 8, 9],
          swapping: [4, 5],
          action: 'swap',
          description: '오른쪽 파티션 정렬 (9 ↔ 8)'
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

  // 병합 정렬 데모
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
          description: '분할: [5,2]와 [8,1]'
        },
        {
          array: [2, 5, 8, 1],
          partitions: [
            { start: 0, end: 1, label: 'merged', color: '#51cf66' }
          ],
          action: 'merge',
          description: '왼쪽 병합: [5,2] → [2,5]'
        },
        {
          array: [2, 5, 1, 8],
          partitions: [
            { start: 2, end: 3, label: 'merged', color: '#51cf66' }
          ],
          action: 'merge',
          description: '오른쪽 병합: [8,1] → [1,8]'
        },
        {
          array: [1, 2, 5, 8],
          action: 'merge',
          description: '최종 병합: [2,5]와 [1,8] → [1,2,5,8]'
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

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto', fontFamily: 'sans-serif' }}>
      <header style={{ textAlign: 'center', marginBottom: '40px' }}>
        <h1>🎨 정렬 알고리즘 시각화 데모</h1>
        <p style={{ color: '#666' }}>
          Phase 2 - Issue #12: 정렬 알고리즘 시각화 컴포넌트
        </p>
        <div style={{ fontSize: '14px', color: '#999', marginTop: '10px' }}>
          <span style={{ marginRight: '20px' }}>✅ React: 20/20 테스트 통과</span>
          <span>✅ Kotlin: 16/16 테스트 통과</span>
        </div>
      </header>

      <section style={{ marginBottom: '60px', background: '#f8f9fa', padding: '30px', borderRadius: '12px' }}>
        <h2 style={{ marginTop: 0 }}>1️⃣ 버블 정렬 (Bubble Sort)</h2>
        <p style={{ color: '#666', fontSize: '14px' }}>
          인접한 두 요소를 비교하여 큰 값을 오른쪽으로 이동시키는 알고리즘입니다.
          시간 복잡도: O(n²)
        </p>
        <div style={{ background: 'white', padding: '20px', borderRadius: '8px', marginTop: '20px' }}>
          <SortVisualizer data={bubbleSortDemo} autoPlay={false} />
        </div>
      </section>

      <section style={{ marginBottom: '60px', background: '#f8f9fa', padding: '30px', borderRadius: '12px' }}>
        <h2 style={{ marginTop: 0 }}>2️⃣ 퀵 정렬 (Quick Sort)</h2>
        <p style={{ color: '#666', fontSize: '14px' }}>
          피벗을 기준으로 배열을 분할하고 정복하는 알고리즘입니다.
          시간 복잡도: 평균 O(n log n), 최악 O(n²)
        </p>
        <div style={{ background: 'white', padding: '20px', borderRadius: '8px', marginTop: '20px' }}>
          <SortVisualizer data={quickSortDemo} autoPlay={false} />
        </div>
      </section>

      <section style={{ marginBottom: '60px', background: '#f8f9fa', padding: '30px', borderRadius: '12px' }}>
        <h2 style={{ marginTop: 0 }}>3️⃣ 병합 정렬 (Merge Sort)</h2>
        <p style={{ color: '#666', fontSize: '14px' }}>
          배열을 절반씩 분할한 후 정렬하면서 병합하는 알고리즘입니다.
          시간 복잡도: O(n log n) (안정적)
        </p>
        <div style={{ background: 'white', padding: '20px', borderRadius: '8px', marginTop: '20px' }}>
          <SortVisualizer data={mergeSortDemo} autoPlay={false} />
        </div>
      </section>

      <section style={{ marginBottom: '60px', background: '#e7f5ff', padding: '30px', borderRadius: '12px', border: '2px solid #339af0' }}>
        <h2 style={{ marginTop: 0 }}>⚡ 자동 재생 데모 (4배속)</h2>
        <p style={{ color: '#666', fontSize: '14px' }}>
          버블 정렬 과정을 빠른 속도로 자동 재생합니다.
        </p>
        <div style={{ background: 'white', padding: '20px', borderRadius: '8px', marginTop: '20px' }}>
          <SortVisualizer data={bubbleSortDemo} autoPlay={true} defaultSpeed={4} />
        </div>
      </section>

      <footer style={{ textAlign: 'center', padding: '40px 0', borderTop: '1px solid #e9ecef', marginTop: '60px' }}>
        <h3>🎮 UI 컨트롤 가이드</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginTop: '20px' }}>
          <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px' }}>
            <h4 style={{ marginTop: 0 }}>🔘 버튼</h4>
            <ul style={{ textAlign: 'left', fontSize: '14px', lineHeight: '1.8' }}>
              <li><strong>◀◀ Previous</strong>: 이전 단계</li>
              <li><strong>▶ Play / ⏸ Pause</strong>: 재생/정지</li>
              <li><strong>Next ▶▶</strong>: 다음 단계</li>
            </ul>
          </div>

          <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px' }}>
            <h4 style={{ marginTop: 0 }}>⚡ 속도</h4>
            <ul style={{ textAlign: 'left', fontSize: '14px', lineHeight: '1.8' }}>
              <li><strong>0.5x</strong>: 느린 속도</li>
              <li><strong>1x</strong>: 기본 속도</li>
              <li><strong>2x / 4x</strong>: 빠른 속도</li>
            </ul>
          </div>

          <div style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px' }}>
            <h4 style={{ marginTop: 0 }}>🎨 색상</h4>
            <ul style={{ textAlign: 'left', fontSize: '14px', lineHeight: '1.8' }}>
              <li><span style={{ color: '#4ecdc4' }}>⬛</span> 기본 (미정렬)</li>
              <li><span style={{ color: '#ff922b' }}>⬛</span> 비교 중</li>
              <li><span style={{ color: '#ff6b6b' }}>⬛</span> 교환 중</li>
              <li><span style={{ color: '#51cf66' }}>⬛</span> 정렬 완료</li>
              <li><span style={{ color: '#9775fa' }}>⬛</span> 피벗</li>
            </ul>
          </div>
        </div>

        <div style={{ marginTop: '40px', color: '#999', fontSize: '14px' }}>
          <p>
            📚 자세한 가이드: <code>docs/DEMO_GUIDE.md</code>
          </p>
          <p>
            🐛 이슈 리포트: <a href="https://github.com/bulhwi/debug_visualizer_for_jetbrains_ide/issues" target="_blank">GitHub Issues</a>
          </p>
        </div>
      </footer>
    </div>
  )
}

export default AppDemo
