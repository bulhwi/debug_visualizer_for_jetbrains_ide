import { useState, useEffect } from 'react'
import './styles/App.css'
import { ArrayVisualizer } from './components/ArrayVisualizer'

interface VisualizationData {
  expression: string
  value: string
  type?: string
  timestamp: number
}

/**
 * 배열 데이터인지 확인
 */
function isArrayData(data: VisualizationData): boolean {
  const value = data.value
  // "[]" 형식이거나 배열 타입인 경우
  return (
    (typeof value === 'string' && value.startsWith('[') && value.endsWith(']')) ||
    (data.type !== undefined && data.type.includes('[]'))
  )
}

function App() {
  const [data, setData] = useState<VisualizationData | null>(null)

  useEffect(() => {
    // Java에서 호출할 수 있는 전역 함수 등록
    (window as any).visualizerAPI = {
      showData: (dataStr: string) => {
        console.log('📊 Received data from Java:', dataStr)
        try {
          const parsed = typeof dataStr === 'string' ? JSON.parse(dataStr) : dataStr
          setData(parsed)
        } catch (e) {
          console.error('Failed to parse data:', e)
          setData({ expression: 'error', value: dataStr, timestamp: Date.now() })
        }
      },
      clear: () => {
        console.log('🗑️ Clearing visualization')
        setData(null)
      },
    }

    console.log('✅ Visualizer API initialized')
  }, [])

  return (
    <div className="app">
      <header className="header">
        <h1>🎨 Algorithm Debug Visualizer</h1>
        <p>React + Vite + TypeScript</p>
      </header>

      <main className="main">
        {data ? (
          <div className="visualization">
            {/* D3.js 시각화 영역 */}
            {isArrayData(data) && (
              <div className="visualization-container">
                <ArrayVisualizer data={data.value} expression={data.expression} />
              </div>
            )}

            {/* 데이터 정보 카드 */}
            <div className="info-cards">
              <div className="data-card">
                <h2>표현식</h2>
                <code>{data.expression}</code>
              </div>

              {data.type && (
                <div className="data-card">
                  <h2>타입</h2>
                  <code>{data.type}</code>
                </div>
              )}

              <div className="data-card">
                <h2>값</h2>
                <pre className="value">{data.value}</pre>
              </div>

              <div className="timestamp">
                평가 시각: {new Date(data.timestamp).toLocaleTimeString()}
              </div>
            </div>
          </div>
        ) : (
          <div className="empty-state">
            <div className="emoji">📊</div>
            <h2>데이터 준비 중</h2>
            <p>디버거에서 표현식을 평가하면 여기에 표시됩니다.</p>
          </div>
        )}
      </main>
    </div>
  )
}

export default App
