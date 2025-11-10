# Algorithm Visualizer UI

React + TypeScript + Vite 기반 시각화 UI

## 개발 시작

```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 빌드
npm run build

# 테스트
npm test
```

## 기술 스택

- **React** 18.2
- **TypeScript** 5.2
- **Vite** 5.0 (빠른 번들링)
- **D3.js** 7.8 (시각화)

## 프로젝트 구조

```
src/
├── components/       # React 컴포넌트
├── styles/          # CSS 스타일
├── utils/           # 유틸리티 함수
├── App.tsx          # 메인 앱 컴포넌트
└── main.tsx         # 진입점
```

## JCEF 통합

이 UI는 IntelliJ 플러그인의 JCEF 웹뷰 안에서 실행됩니다:

```kotlin
// Kotlin에서 호출
jcefPanel.executeJavaScript("""
  window.visualizerAPI.showData('{"expression": "arr", "value": "[1,2,3]"}')
""")
```

```typescript
// TypeScript에서 수신
window.visualizerAPI = {
  showData: (data: string) => {
    // 데이터 처리
  }
}
```

## 빌드 출력

빌드된 파일은 `dist/` 디렉토리에 생성됩니다:
- `dist/index.html` - 메인 HTML
- `dist/assets/` - JS/CSS 번들

이 파일들은 플러그인에 포함되어 JCEF로 로드됩니다.
