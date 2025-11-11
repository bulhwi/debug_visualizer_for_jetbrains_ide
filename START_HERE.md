# 🚀 정렬 시각화 테스트 시작 가이드

**2가지 테스트 방법을 선택하세요!**

---

## 방법 1️⃣: 브라우저에서 빠르게 테스트 (3분)

**가장 빠르고 쉬운 방법! 브라우저만 있으면 OK**

```bash
cd visualizer-ui

# main.tsx 수정 (AppDemo 사용)
# 3번 줄: import App from './App' → 주석 처리
# 4번 줄: // import AppDemo from './AppDemo' → 주석 해제
# 11번 줄: <App /> → <AppDemo />로 변경

npm run dev
```

브라우저에서 **http://localhost:3000** 열기

**볼 수 있는 것:**
- 🔵 버블 정렬 시각화
- 🔵 퀵 정렬 시각화
- 🔵 병합 정렬 시각화
- ⚡ 자동 재생 데모

**상세 가이드**: `QUICK_TEST.md`

---

## 방법 2️⃣: 실제 IntelliJ 플러그인으로 테스트 (10분)

**실제 디버깅 환경에서 테스트 (현재는 수동 주입 방식)**

### 1. 플러그인 실행

```bash
cd plugin
./gradlew runIde
```

새 IntelliJ IDEA 창이 열립니다.

### 2. 테스트 Java 프로젝트 생성

새 창에서:
1. **File** → **New** → **Project** → **Java**
2. `src/Main.java` 생성 (버블 정렬 코드)
3. 브레이크포인트 설정
4. **Debug** 실행

### 3. 시각화 확인

1. **View** → **Tool Windows** → **Algorithm Visualizer**
2. 웹뷰에서 **오른쪽 클릭** → **Inspect** (개발자 도구)
3. **Console**에서 다음 코드 실행:

```javascript
window.visualizerAPI.showData(JSON.stringify({
  "kind": "sort",
  "timestamp": Date.now(),
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
        "action": "compare",
        "description": "초기 배열"
      },
      {
        "array": [5, 2, 8, 1, 9],
        "comparing": [0, 1],
        "action": "compare",
        "description": "arr[0]=5와 arr[1]=2 비교"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "swapping": [0, 1],
        "action": "swap",
        "description": "교환"
      },
      {
        "array": [1, 2, 5, 8, 9],
        "sorted": [0, 1, 2, 3, 4],
        "action": "sorted",
        "description": "정렬 완료!"
      }
    ]
  }
}))
```

**상세 가이드**: `PLUGIN_TEST_GUIDE.md`

---

## ⚠️ 현재 상태

### ✅ 완료된 것 (Phase 2 #12)
- React SortVisualizer 컴포넌트 (20개 테스트 통과)
- Kotlin SnapshotCollector (16개 테스트 통과)
- D3.js 기반 시각화
- 애니메이션 컨트롤
- 색상 코딩

### 🔲 아직 안된 것 (다음 작업)
- **자동 스냅샷 수집** (#14) - 아직 수동 주입 필요
- **알고리즘 자동 감지** (#13) - 코드 패턴 분석 미구현

### 🎯 완성되면
브레이크포인트에서 멈출 때마다 자동으로 정렬 과정 시각화!

---

## 🎮 UI 컨트롤

- **Next ▶▶**: 다음 단계
- **▶ Play**: 자동 재생
- **⏸ Pause**: 일시정지
- **◀◀ Previous**: 이전 단계
- **Speed**: 0.5x ~ 4x 속도 조절

## 🎨 색상 의미

- 🟦 회청색: 기본 상태
- 🟧 주황색: 비교 중
- 🟥 빨간색: 교환 중
- 🟩 초록색: 정렬 완료
- 🟪 보라색: 피벗 (퀵소트)

---

## 📚 모든 가이드

| 파일 | 내용 |
|------|------|
| **QUICK_TEST.md** | 브라우저 빠른 테스트 (3분) |
| **PLUGIN_TEST_GUIDE.md** | 플러그인 실제 테스트 (10분) |
| **docs/DEMO_GUIDE.md** | 상세 데모 가이드 |
| **docs/sort-visualization-schema.md** | 데이터 스키마 문서 |
| **docs/TESTING.md** | 테스트 가이드 |

---

## 🐛 문제 해결

### 브라우저 데모가 안보여요
```bash
cd visualizer-ui
npm install
npm run dev
```

### 플러그인이 빌드 안돼요
```bash
cd plugin
./gradlew clean build
```

### React UI가 플러그인에 안보여요
```bash
cd visualizer-ui
npm run build
cd ../plugin
./gradlew build
./gradlew runIde
```

---

## 🎉 추천 테스트 순서

1. **브라우저 데모** 먼저 확인 (방법 1️⃣)
   - 가장 빠르게 시각화 확인 가능
   - UI 컨트롤 테스트

2. **플러그인 실행** (방법 2️⃣)
   - 실제 IDE 환경 확인
   - 수동 JSON 주입으로 테스트

3. **GitHub PR 확인**
   - https://github.com/bulhwi/debug_visualizer_for_jetbrains_ide/pull/21
   - 구현 내용 및 테스트 결과 확인

---

**즐거운 테스트 되세요!** 🚀

문제가 있으면 이슈로 남겨주세요!
