# React UI 테스트 가이드

## 테스트 환경 구축

### 의존성 설치
```bash
cd visualizer-ui
npm install
```

## 테스트 실행

### 1. 단위 테스트 (Vitest)
```bash
# 전체 테스트 실행
npm test

# Watch 모드 (파일 변경 시 자동 실행)
npm test -- --watch

# UI 모드 (브라우저에서 테스트 결과 확인)
npm run test:ui

# 커버리지 리포트
npm test -- --coverage
```

**현재 테스트:**
- `src/App.test.tsx`: App 컴포넌트 테스트 (10개 테스트)
  - ✅ 제목 렌더링
  - ✅ 빈 상태 표시
  - ✅ visualizerAPI 등록
  - ✅ 데이터 표시
  - ✅ JSON 파싱
  - ✅ 에러 처리
  - ✅ 클리어 기능
  - ✅ 타임스탬프 표시
  - ✅ 타입 조건부 표시

### 2. 개발 서버 실행
```bash
# 개발 서버 시작 (http://localhost:3000)
npm run dev
```

브라우저에서 `http://localhost:3000` 열기

### 3. 테스트 페이지 사용
```bash
# 개발 서버 시작
npm run dev

# 브라우저에서 테스트 페이지 열기
# http://localhost:3000/test.html
```

**테스트 페이지 기능:**
- 🔘 **Test Array**: char 배열 테스트
- 🔘 **Test Primitive**: int 타입 테스트
- 🔘 **Test String**: String 타입 테스트
- 🔘 **Test Nested Array**: 2D 배열 테스트
- 🔘 **Clear**: 시각화 클리어
- 📝 **Custom**: 사용자 정의 데이터 전송

### 4. 프로덕션 빌드 테스트
```bash
# 빌드
npm run build

# 빌드된 파일 프리뷰
npm run preview
```

빌드 결과: `dist/` 디렉토리에 생성됨

## IntelliJ 플러그인과 통합 테스트

### 준비 사항
1. React UI 빌드
   ```bash
   cd visualizer-ui
   npm run build
   ```

2. 빌드된 파일이 `dist/` 디렉토리에 생성됨 확인

3. 플러그인에서 이 파일을 로드하도록 설정 (TODO: Phase 1-8)

### 플러그인에서 호출 예시
```kotlin
// Kotlin (플러그인)
val data = buildString {
    append("{")
    append("\"expression\": \"myArray\",")
    append("\"value\": \"[1, 2, 3]\",")
    append("\"type\": \"int[]\",")
    append("\"timestamp\": ${System.currentTimeMillis()}")
    append("}")
}

jcefPanel.executeJavaScript("window.visualizerAPI.showData('$data')")
```

## TDD 워크플로우

### 1. Red: 실패하는 테스트 작성
```typescript
// src/components/ArrayVisualizer.test.tsx
it('should render array elements as bars', () => {
  render(<ArrayVisualizer data={[1, 2, 3]} />)

  const bars = screen.getAllByRole('img', { name: /bar/ })
  expect(bars).toHaveLength(3)
})
```

### 2. Green: 테스트 통과시키기
```typescript
// src/components/ArrayVisualizer.tsx
export function ArrayVisualizer({ data }: { data: number[] }) {
  return (
    <div>
      {data.map((value, i) => (
        <div key={i} role="img" aria-label="bar">
          {value}
        </div>
      ))}
    </div>
  )
}
```

### 3. Refactor: 코드 개선
```typescript
// 성능 최적화, 스타일 개선 등
```

## 테스트 커버리지 목표

- **목표**: 80% 이상
- **현재**: App 컴포넌트 100%

```bash
# 커버리지 확인
npm test -- --coverage

# 커버리지 리포트 보기
open coverage/index.html
```

## 디버깅 팁

### Vitest 디버깅
```typescript
// 테스트에서 디버깅
it('should work', () => {
  console.log('Debug:', someValue)
  screen.debug() // DOM 출력
})
```

### 브라우저 콘솔
```javascript
// 개발 서버에서
console.log('visualizerAPI:', window.visualizerAPI)
```

### React DevTools
```bash
# Chrome Extension 설치
# https://chrome.google.com/webstore/detail/react-developer-tools/
```

## 문제 해결

### 테스트가 실패할 때
```bash
# 캐시 클리어
rm -rf node_modules/.vite
npm test
```

### 타입 에러
```bash
# 타입 체크
npm run type-check
```

### 의존성 문제
```bash
# 재설치
rm -rf node_modules package-lock.json
npm install
```

## 다음 단계

1. ✅ 기본 App 컴포넌트 테스트
2. 🔄 컴포넌트 단위 분리
3. ⏳ D3.js 시각화 컴포넌트 테스트
4. ⏳ 통합 테스트

---

**작성일**: 2025-11-10
**테스트 프레임워크**: Vitest + React Testing Library
