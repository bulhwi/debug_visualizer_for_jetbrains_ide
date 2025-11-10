# 다음 작업 시작 가이드

**작성일**: 2025-11-11
**현재 상태**: Phase 1 완료 ✅
**다음 단계**: Phase 2 시작

---

## 🎯 현재까지 완료된 것

### Phase 1 (100% 완료)
- ✅ 플러그인 인프라 구축
- ✅ JDI 기반 값 추출 (모든 프리미티브 타입)
- ✅ JCEF 웹뷰 통합
- ✅ React UI + D3.js 막대 그래프
- ✅ 데이터 브리지 (Kotlin ↔ React)
- ✅ TDD 환경 (45개 테스트, 100% 통과)
- ✅ GitHub 저장소 생성 및 푸시
- ✅ Phase 1 이슈 정리 (7개 완료, 2개 남음)
- ✅ Phase 2 이슈 9개 등록 완료

### 테스트 현황
```
Kotlin: 35/35 통과 (< 1초)
React:  10/10 통과 (81ms)
총:     45/45 통과 ✅
```

### 저장소
https://github.com/bulhwi/debug_visualizer_for_jetbrains_ide

---

## 🚀 다음 작업 시작하기

### Phase 2 작업 순서 (권장)

#### 1단계: 정렬 알고리즘 시각화 (1주)
```bash
# 이슈 #12번부터 시작
gh issue view 12 --repo bulhwi/debug_visualizer_for_jetbrains_ide
```

**작업 순서**:
1. **#12: 정렬 알고리즘 시각화** (가장 우선)
   - TDD로 SortVisualizer 컴포넌트 작성
   - 스냅샷 데이터 구조 설계
   - D3.js 애니메이션 구현

2. **#13: 정렬 알고리즘 감지기**
   - TDD로 AlgorithmDetector 작성
   - PSI 활용한 코드 패턴 분석

3. **#14: 스냅샷 수집기**
   - TDD로 SnapshotCollector 작성
   - 디버거 스텝마다 배열 상태 캡처

#### 2단계: 트리 구조 시각화 (1주)
4. **#16: 트리 노드 파서** (먼저)
5. **#15: 트리 구조 시각화**
6. **#17: 트리 연산 애니메이션**

#### 3단계: DP 테이블 시각화 (3-4일)
7. **#18: DP 테이블 시각화**
8. **#19: DP 경로 추적**

#### 4단계: 보너스 (시간 있으면)
9. **#20: 그래프 시각화**

---

## 📋 작업 시작 체크리스트

### 작업 전 확인사항
- [ ] 최신 코드 pull 받기: `git pull origin main`
- [ ] 빌드 정상 동작 확인: `./gradlew build`
- [ ] 모든 테스트 통과 확인: `./gradlew test`
- [ ] React UI 테스트 확인: `cd visualizer-ui && npm test`

### 새 작업 시작할 때
1. **GitHub 이슈 확인**
   ```bash
   gh issue view [이슈번호] --repo bulhwi/debug_visualizer_for_jetbrains_ide
   ```

2. **브랜치 생성**
   ```bash
   git checkout -b phase2/issue-[번호]-[간단한설명]
   # 예: git checkout -b phase2/issue-12-sort-visualizer
   ```

3. **TDD 시작!**
   - ✅ **테스트 먼저 작성** (Red)
   - ✅ 최소한의 구현 (Green)
   - ✅ 리팩토링 (Refactor)

4. **작업 완료 후**
   ```bash
   # 테스트 확인
   ./gradlew test
   cd visualizer-ui && npm test

   # 커밋
   git add .
   git commit -m "[#12] 정렬 알고리즘 시각화 구현

   - SortVisualizer 컴포넌트 (TDD)
   - 12개 테스트 추가
   - 버블/퀵/병합 정렬 지원

   🤖 Generated with Claude Code

   Co-Authored-By: Claude <noreply@anthropic.com>"

   # 푸시 및 PR 생성
   git push -u origin phase2/issue-12-sort-visualizer
   gh pr create --title "[Phase 2] 정렬 알고리즘 시각화" --body "Closes #12"
   ```

5. **이슈 업데이트**
   - PR 생성 후 이슈에 링크
   - 완료 시 이슈 close

---

## 🧪 TDD 원칙 (필수!)

### Kotlin 테스트
```kotlin
// 위치: plugin/src/test/kotlin/
// 프레임워크: JUnit 5 + MockK + AssertJ

@Test
fun `should detect bubble sort algorithm`() {
    // Given
    val code = """
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n-1; j++) {
                if (arr[j] > arr[j+1]) {
                    swap(arr, j, j+1);
                }
            }
        }
    """.trimIndent()

    // When
    val result = algorithmDetector.detect(code)

    // Then
    assertThat(result.algorithm).isEqualTo(Algorithm.BUBBLE_SORT)
    assertThat(result.confidence).isGreaterThan(0.9)
}
```

### React 테스트
```typescript
// 위치: visualizer-ui/src/test/
// 프레임워크: Vitest + React Testing Library

it('should render sort visualizer with snapshots', () => {
  // Given
  const snapshots = [
    { array: [5, 2, 8, 1], comparing: [0, 1] },
    { array: [2, 5, 8, 1], comparing: [2, 3] },
  ];

  // When
  render(<SortVisualizer snapshots={snapshots} />);

  // Then
  expect(screen.getByText('Step 1 / 2')).toBeInTheDocument();
  expect(screen.getAllByRole('graphics-symbol')).toHaveLength(4);
});
```

---

## 📊 완료 기준

각 이슈 완료 시 확인사항:
- [ ] 모든 테스트 통과 (기존 + 신규)
- [ ] 코드 커버리지 > 80% (신규 코드)
- [ ] 빌드 성공
- [ ] 코드 리뷰 (셀프 리뷰 포함)
- [ ] 문서 업데이트 (필요 시)
- [ ] GitHub 이슈 close

---

## 🔧 유용한 명령어

### 빌드 & 테스트
```bash
# 전체 빌드
./gradlew build

# Kotlin 테스트만
./gradlew test

# React UI 테스트만
cd visualizer-ui && npm test

# 플러그인 실행 (IDE 띄우기)
./gradlew runIde

# 플러그인 JAR 생성
./gradlew buildPlugin
```

### Git & GitHub
```bash
# 이슈 목록
gh issue list --repo bulhwi/debug_visualizer_for_jetbrains_ide

# Phase 2 이슈만
gh issue list --repo bulhwi/debug_visualizer_for_jetbrains_ide --label phase-2

# 특정 이슈 보기
gh issue view 12 --repo bulhwi/debug_visualizer_for_jetbrains_ide

# PR 생성
gh pr create --title "제목" --body "내용"

# 이슈 닫기
gh issue close 12 --repo bulhwi/debug_visualizer_for_jetbrains_ide
```

---

## 📚 참고 문서

### 프로젝트 문서
- [PRD.md](./docs/PRD.md) - 제품 요구사항
- [PHASE1_COMPLETE.md](./docs/PHASE1_COMPLETE.md) - Phase 1 완료 보고서
- [PHASE2_PLAN.md](./docs/PHASE2_PLAN.md) - Phase 2 상세 계획
- [TESTING.md](./docs/TESTING.md) - 테스트 가이드
- [LESSONS_LEARNED.md](./docs/LESSONS_LEARNED.md) - 교훈 정리

### 기술 문서
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [D3.js Documentation](https://d3js.org/)
- [Vitest](https://vitest.dev/)
- [React Testing Library](https://testing-library.com/react)

---

## 💡 Phase 2 시작 시 첫 명령어

```bash
# 1. 최신 코드 확인
git pull origin main

# 2. Phase 2 첫 번째 이슈 확인
gh issue view 12 --repo bulhwi/debug_visualizer_for_jetbrains_ide

# 3. 브랜치 생성
git checkout -b phase2/issue-12-sort-visualizer

# 4. 테스트 파일 생성 (TDD 시작!)
# Kotlin
mkdir -p plugin/src/test/kotlin/com/github/algorithmvisualizer/visualizers
touch plugin/src/test/kotlin/com/github/algorithmvisualizer/visualizers/SortVisualizerTest.kt

# React
touch visualizer-ui/src/components/SortVisualizer.test.tsx
```

---

## 🎯 Phase 2 목표 재확인

- **기간**: 2-3주
- **핵심 기능**: 정렬, 트리, DP 시각화
- **방법론**: TDD (테스트 먼저!)
- **품질 기준**:
  - 모든 테스트 통과
  - 커버리지 > 80%
  - 성능 목표 달성

---

**모든 작업은 TDD로 진행합니다!** 🧪

**Phase 1 완료를 축하합니다!** 🎉
