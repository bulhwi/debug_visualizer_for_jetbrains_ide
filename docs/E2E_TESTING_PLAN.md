# E2E 테스트 계획 (End-to-End Testing Plan)

## 개요

E2E 테스트는 실제 IDE 환경에서 플러그인의 전체 워크플로우를 검증합니다.

## 테스트 전략

### 1. IntelliJ Platform Test Framework 사용

IntelliJ은 공식적으로 플러그인 테스트 프레임워크를 제공합니다:

```kotlin
// plugin/src/test/kotlin/integration/
class DebuggerVisualizerE2ETest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testArrayVisualization() {
        // 1. 테스트 Java 파일 로드
        myFixture.configureByFile("ArraySum.java")

        // 2. 브레이크포인트 설정
        val document = myFixture.editor.document
        val lineNumber = 10 // sum 계산 후
        myFixture.editor.gutterComponentEx.setLineNumberEnabled(true)

        // 3. 디버거 시작 (시뮬레이션)
        val debugProcess = startDebugSession()

        // 4. 표현식 평가
        val result = evaluateExpression(debugProcess, "chars")

        // 5. 검증
        assertEquals("['1', '2', '3', '4', '5']", result)
    }
}
```

### 2. 테스트 데이터 구조

```
plugin/src/test/
├── kotlin/
│   ├── unit/                    # 단위 테스트 (현재)
│   └── integration/             # E2E 테스트 (신규)
│       ├── ArrayVisualizationE2ETest.kt
│       ├── TreeVisualizationE2ETest.kt
│       └── GraphVisualizationE2ETest.kt
└── testData/                    # 테스트 데이터
    ├── java/
    │   ├── ArraySum.java
    │   ├── BinaryTree.java
    │   └── Graph.java
    ├── kotlin/
    │   ├── ArraySum.kt
    │   └── BinaryTree.kt
    └── expected/                # 예상 결과
        ├── array_output.json
        └── tree_output.json
```

### 3. 테스트 시나리오

#### Scenario 1: 배열 시각화
```gherkin
Given 사용자가 ArraySum.java를 연다
And 10번 라인에 브레이크포인트를 설정한다
When 디버거를 시작한다
And "chars" 표현식을 평가한다
Then 결과는 "['1', '2', '3', '4', '5']"이어야 한다
```

#### Scenario 2: 중첩 배열
```gherkin
Given 사용자가 Matrix.java를 연다
When "matrix" 표현식을 평가한다
Then 결과는 "[[1, 2], [3, 4]]"이어야 한다
```

#### Scenario 3: 에러 처리
```gherkin
Given 디버거가 실행 중이지 않을 때
When 표현식을 평가하려고 시도하면
Then "디버깅 세션이 활성화되지 않았습니다" 에러가 표시되어야 한다
```

### 4. 테스트 실행 방식

#### 방법 A: Headless UI 테스트
```kotlin
@RunWith(JUnit4::class)
class HeadlessE2ETest {
    @Rule
    @JvmField
    val projectRule = ProjectRule()

    @Test
    fun testArrayEvaluation() {
        runInEdtAndWait {
            // UI 작업을 EDT에서 실행
            val project = projectRule.project
            // ...
        }
    }
}
```

#### 방법 B: UI 테스트 (느림, 하지만 완전)
```bash
# IntelliJ UI Test Framework 사용
./gradlew runIdeForUiTests &
./gradlew test --tests "*E2E*"
```

### 5. Mock 디버거 세션

실제 디버거를 시작하는 것은 느리므로, Mock 사용 고려:

```kotlin
class MockDebugSession(project: Project) : XDebugSession(...) {
    private val mockValues = mutableMapOf<String, XValue>()

    fun addMockValue(expression: String, value: XValue) {
        mockValues[expression] = value
    }

    override fun evaluate(
        expression: String,
        callback: XDebuggerEvaluator.XEvaluationCallback
    ) {
        val value = mockValues[expression]
        if (value != null) {
            callback.evaluated(value)
        } else {
            callback.errorOccurred("Unknown expression")
        }
    }
}
```

### 6. CI/CD 통합

```yaml
# .github/workflows/e2e-tests.yml
name: E2E Tests

on: [pull_request]

jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Setup JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'

      - name: Run E2E Tests
        run: |
          cd plugin
          ./gradlew test --tests "*E2E*"

      - name: Upload Test Reports
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: test-reports
          path: plugin/build/reports/tests/
```

## 테스트 범위

### Phase 1-9 (현재 단계)
- ✅ 단위 테스트: 값 변환 로직
- 🔄 통합 테스트: 디버거 API 통합
- ⏳ E2E 테스트: 전체 워크플로우

### 우선순위

**High (Phase 1-9에서 구현):**
1. 배열 평가 E2E 테스트
2. 프리미티브 타입 E2E 테스트
3. 에러 시나리오 테스트

**Medium (Phase 2):**
4. 복잡한 객체 (트리, 그래프)
5. JCEF 통합 테스트
6. 다국어 지원 (Python, JS)

**Low (Phase 3+):**
7. 성능 테스트 (대용량 데이터)
8. UI 인터랙션 테스트
9. 멀티 플랫폼 테스트

## 예상 테스트 실행 시간

| 테스트 유형 | 테스트 수 | 실행 시간 | 환경 |
|------------|----------|-----------|------|
| 단위 테스트 | 35개 | < 1초 | 로컬/CI |
| 통합 테스트 | ~10개 | ~30초 | 로컬/CI |
| E2E 테스트 | ~20개 | ~2분 | 로컬/CI |
| **전체** | **~65개** | **~2.5분** | **CI** |

## 구현 계획

### Step 1: 테스트 인프라 구축
```kotlin
// BasePlatformTestCase 상속
abstract class VisualizerE2ETestBase : BasePlatformTestCase() {
    protected lateinit var debuggerHelper: DebuggerTestHelper

    override fun setUp() {
        super.setUp()
        debuggerHelper = DebuggerTestHelper(project)
    }
}
```

### Step 2: 테스트 헬퍼 작성
```kotlin
class DebuggerTestHelper(private val project: Project) {
    fun createMockDebugSession(): XDebugSession { ... }
    fun evaluateAndWait(expression: String): String { ... }
    fun setBreakpoint(file: VirtualFile, line: Int) { ... }
}
```

### Step 3: 첫 번째 E2E 테스트 작성
```kotlin
class ArrayVisualizationE2ETest : VisualizerE2ETestBase() {
    fun testSimpleIntArray() {
        val result = debuggerHelper.evaluateAndWait("new int[]{1,2,3}")
        assertEquals("[1, 2, 3]", result)
    }
}
```

### Step 4: 점진적 확장
- 더 복잡한 데이터 구조
- 에러 케이스
- 성능 시나리오

## 참고 자료

- [IntelliJ Platform Testing](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)
- [IntelliJ Platform Test Framework](https://github.com/JetBrains/intellij-community/tree/master/platform/testFramework)
- [Debugging Tests](https://plugins.jetbrains.com/docs/intellij/testing-faq.html)

---

**작성일**: 2025-11-10
**버전**: 1.0.0
