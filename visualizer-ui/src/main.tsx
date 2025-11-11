import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'  // 원본 앱 (플러그인 통합용)
// import AppDemo from './AppDemo'  // 데모 앱 (정렬 시각화 테스트용)
import './styles/index.css'

// 데모 모드: AppDemo 사용 (정렬 알고리즘 시각화 테스트)
// 플러그인 모드: App 사용 (JCEF 통합)
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
