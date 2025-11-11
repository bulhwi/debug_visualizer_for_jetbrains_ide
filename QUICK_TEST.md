# 🚀 SortVisualizer 빠른 테스트 가이드

**5분 안에 정렬 시각화 확인하기!**

---

## 📋 준비사항

- Node.js 설치되어 있어야 함
- 터미널 열기

---

## 🎬 3단계로 시작하기

### 1️⃣ 개발 서버 실행

```bash
cd visualizer-ui
npm run dev
```

실행되면 이런 메시지가 나옴:
```
  ➜  Local:   http://localhost:5173/
```

### 2️⃣ 브라우저 열기

브라우저에서 자동으로 열리거나, 수동으로 열기:
```
http://localhost:5173
```

### 3️⃣ 완료! 🎉

이제 3가지 정렬 알고리즘을 볼 수 있어:

1. **버블 정렬** - 단계별 수동 컨트롤
2. **퀵 정렬** - 피벗 기반 파티셔닝
3. **병합 정렬** - 분할 정복
4. **자동 재생 데모** - 4배속 자동 재생

---

## 🎮 테스트해보기

### 버튼 클릭해보기
- **Next ▶▶**: 다음 단계로 이동 → 막대 색상이 바뀜
- **▶ Play**: 자동 재생 시작
- **⏸ Pause**: 일시정지
- **◀◀ Previous**: 이전 단계로 되돌리기

### 속도 조절
- 드롭다운에서 `0.5x`, `1x`, `2x`, `4x` 선택

### 색상 확인
- **회청색**: 기본 상태
- **주황색**: 비교 중 (comparing)
- **빨간색**: 교환 중 (swapping)
- **초록색**: 정렬 완료 (sorted)
- **보라색**: 피벗 (pivot, 퀵소트만)

---

## 🔥 자동 재생 데모

페이지를 스크롤해서 **"⚡ 자동 재생 데모"** 섹션으로 이동하면:
- 페이지 로드 시 자동으로 재생 시작
- 4배속으로 빠르게 정렬 과정을 볼 수 있음
- **Pause** 버튼으로 원하는 순간에 멈출 수 있음

---

## 🧪 테스트 실행

다른 터미널에서:

```bash
cd visualizer-ui
npm test
```

**예상 결과**:
```
✓ src/components/SortVisualizer.test.tsx (20 tests)
  ✓ Rendering (4)
  ✓ Animation Controls (6)
  ✓ Snapshot Display (4)
  ✓ Speed Control (2)
  ✓ Edge Cases (3)
  ✓ Auto-play (1)

Test Files  1 passed (1)
     Tests  20 passed (20)
```

---

## 🔧 원래 앱으로 돌아가기

데모가 끝나면 플러그인 통합용 앱으로 돌아가려면:

### `visualizer-ui/src/main.tsx` 수정:

```tsx
import App from './App'  // 주석 해제
// import AppDemo from './AppDemo'  // 주석 처리

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />  // AppDemo → App으로 변경
  </React.StrictMode>,
)
```

---

## 💡 팁

### 브라우저 개발자 도구로 디버깅
1. **F12** 눌러서 개발자 도구 열기
2. **Console** 탭에서 에러 확인
3. **Elements** 탭에서 SVG 구조 확인

### 커스텀 데이터 테스트
`visualizer-ui/src/AppDemo.tsx` 파일 열어서:
- `bubbleSortDemo` 객체 수정
- `array` 값 변경해서 다른 배열 테스트
- 브라우저가 자동으로 리로드됨

---

## 📸 스크린샷 찍기

테스트하면서 마음에 드는 순간:
1. 원하는 단계에서 **Pause** 클릭
2. 브라우저 스크린샷 (Cmd+Shift+4, Mac)
3. 이슈나 PR에 첨부 가능!

---

## 🐛 문제 해결

### 포트가 이미 사용 중
```bash
# 다른 포트로 실행
npm run dev -- --port 5174
```

### 변경사항이 반영 안됨
```bash
# 브라우저 하드 리프레시
# Mac: Cmd + Shift + R
# Windows: Ctrl + Shift + R
```

### 타입 에러
```bash
cd visualizer-ui
npm install  # 의존성 재설치
```

---

## 📚 더 자세한 가이드

- **전체 데모 가이드**: `docs/DEMO_GUIDE.md`
- **스키마 문서**: `docs/sort-visualization-schema.md`
- **테스트 가이드**: `docs/TESTING.md`

---

**즐거운 테스트 되세요!** 🎉

문제가 있으면 터미널 에러 메시지 복사해서 이슈로 남겨주세요.
