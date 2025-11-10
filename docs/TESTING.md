# 테스트 가이드 (Testing Guide)

## 개요

이 프로젝트는 TDD (Test-Driven Development) 방식을 채택하고 있으며, 빠른 피드백 루프를 위해 단위 테스트와 통합 테스트를 분리했습니다.

## 테스트 실행

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 테스트 클래스 실행
```bash
./gradlew test --tests "com.github.algorithmvisualizer.debugger.ExpressionEvaluatorTest"
```

### 특정 테스트 메서드 실행
```bash
./gradlew test --tests "com.github.algorithmvisualizer.debugger.ExpressionEvaluatorTest.EvaluationResult*"
```

### 연속 테스트 (TDD 모드)
```bash
./gradlew test --continuous
```
파일이 변경될 때마다 자동으로 테스트를 실행합니다.

### 빠른 테스트 (빌드 스킵)
```bash
./gradlew test -x buildSearchableOptions
```

## 테스트 구조

### 디렉토리 구조
```
plugin/src/test/kotlin/
├── com/github/algorithmvisualizer/
│   ├── debugger/
│   │   ├── DebuggerIntegrationTest.kt          # 디버거 통합 구조 테스트
│   │   ├── ExpressionEvaluatorTest.kt          # 표현식 평가기 단위 테스트
│   │   └── JdiValueConverterTest.kt            # JDI 값 변환 로직 테스트
│   ├── toolwindow/
│   │   └── VisualizerToolWindowPanelTest.kt    # UI 패널 유틸리티 테스트
│   └── AlgorithmVisualizerPluginTest.kt         # 플러그인 기본 구조 테스트
```

### 테스트 분류

#### 1. 단위 테스트 (Unit Tests)
- **목적**: 개별 함수/메서드의 로직 검증
- **특징**: 빠른 실행 (< 1초), 외부 의존성 없음
- **예시**:
  - `ExpressionEvaluatorTest`: EvaluationResult 데이터 클래스 테스트
  - `JdiValueConverterTest`: 값 포맷팅 로직 테스트
  - `VisualizerToolWindowPanelTest`: JSON 이스케이프 로직 테스트

#### 2. 통합 테스트 (Integration Tests)
- **목적**: IntelliJ Platform API와의 통합 검증
- **특징**: 느린 실행 (수 초), IDE 환경 필요
- **예시**:
  - `DebuggerIntegrationTest`: 디버거 API 통합 테스트
  - **TODO**: 실제 디버깅 세션 통합 테스트 (Phase 1-9)

## 테스트 작성 가이드

### Given-When-Then 패턴
모든 테스트는 AAA (Arrange-Act-Assert) 패턴을 따릅니다:

```kotlin
@Test
fun `formatArray should handle multiple elements`() {
    // Given (준비)
    val elements = listOf("1", "2", "3")

    // When (실행)
    val result = formatArray(elements)

    // Then (검증)
    assertEquals("[1, 2, 3]", result)
}
```

### 테스트 네이밍 규칙
- **백틱(backtick) 사용**: 읽기 쉬운 자연어 형식
- **패턴**: `[메서드명] should [기대동작] when [조건]`
- **예시**:
  ```kotlin
  @Test
  fun `escapeJson should escape double quotes`()

  @Test
  fun `EvaluationResult with success false and error message`()
  ```

### 테스트 가능한 코드 작성
1. **순수 함수 선호**: 부작용 없이 입력 → 출력
2. **의존성 주입**: 생성자나 파라미터로 의존성 전달
3. **작은 함수**: 한 가지 일만 수행
4. **헬퍼 메서드 분리**: 복잡한 로직은 별도 함수로 추출

**나쁜 예**:
```kotlin
fun processData() {
    val data = fetchFromDatabase()  // 외부 의존성
    val result = transform(data)
    saveToFile(result)              // 부작용
}
```

**좋은 예**:
```kotlin
fun transformData(data: Data): Result {
    // 순수 함수: 입력만 받고, 출력만 반환
    return Result(data.value * 2)
}

// 테스트 가능!
@Test
fun `transformData should double the value`() {
    val data = Data(value = 5)
    val result = transformData(data)
    assertEquals(10, result.value)
}
```

## JDI 테스트 제약사항

### 문제
JDI (Java Debug Interface) 클래스들은 `final class`이며 mocking이 불가능합니다:
- `IntegerValue`, `StringReference`, `ArrayReference` 등
- MockK, Mockito 모두 실패

### 해결 방법
1. **로직 분리**: JDI 타입 변환 로직을 순수 함수로 분리
   ```kotlin
   // 테스트 가능한 순수 함수
   private fun formatArray(elements: List<String>): String {
       return "[${elements.joinToString(", ")}]"
   }
   ```

2. **통합 테스트로 연기**: 실제 JDI 객체는 통합 테스트에서 검증
   - Phase 1-9에서 실제 디버거 세션과 함께 테스트 예정

## 테스트 커버리지

### 현재 상태 (2025-11-10)
```
테스트 실행: 35개
성공: 35개
실패: 0개
건너뜀: 0개
소요 시간: ~800ms
```

### 커버리지 목표
- **단위 테스트**: 핵심 로직 80% 이상
- **통합 테스트**: 주요 시나리오 100%
- **UI 테스트**: 크리티컬 패스만 (통합 테스트에서)

### 커버리지 확인
```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

**Note**: JaCoCo 플러그인은 향후 추가 예정

## TDD 워크플로우

### 1. Red (실패하는 테스트 작성)
```kotlin
@Test
fun `새로운 기능 테스트`() {
    val result = newFunction()
    assertEquals(expected, result)  // 아직 구현 안 됨 → 실패
}
```

### 2. Green (최소한의 구현)
```kotlin
fun newFunction(): Result {
    return Result()  // 테스트 통과만 시키기
}
```

### 3. Refactor (리팩토링)
```kotlin
fun newFunction(): Result {
    // 코드 개선, 중복 제거, 성능 최적화
    // 테스트는 계속 통과해야 함
}
```

### 4. Repeat
다음 기능으로 이동

## 자동화 도구

### IDE 설정 (IntelliJ IDEA)
1. **자동 테스트 실행**:
   - Settings → Build, Execution, Deployment → Build Tools → Gradle
   - "Run tests using" → Gradle (권장) 또는 IntelliJ IDEA

2. **테스트 커버리지 단축키**:
   - `Ctrl+Shift+F10` (Mac: `Cmd+Shift+R`): 현재 테스트 실행
   - `Ctrl+Shift+F10` with Coverage: 커버리지와 함께 실행

### CI/CD (향후 추가)
```yaml
# .github/workflows/test.yml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run tests
        run: ./gradlew test
```

## 문제 해결

### 테스트가 느려요
1. **`-x buildSearchableOptions`** 사용
2. **특정 테스트만 실행**: `--tests` 옵션
3. **병렬 실행** 확인: `maxParallelForks` 설정됨

### 테스트가 실패해요
1. **로그 확인**:
   ```bash
   ./gradlew test --info
   ```
2. **리포트 확인**:
   ```bash
   open build/reports/tests/test/index.html
   ```
3. **캐시 삭제**:
   ```bash
   ./gradlew clean test
   ```

### IntelliJ에서 테스트가 안 보여요
1. `build.gradle.kts` 파일 우클릭 → "Refresh Gradle Project"
2. File → Invalidate Caches / Restart

## 베스트 프랙티스

### ✅ DO
- 테스트 먼저 작성 (TDD)
- 하나의 테스트는 하나의 동작만 검증
- 읽기 쉬운 테스트 이름 사용
- Given-When-Then 패턴 사용
- 빠른 단위 테스트 우선, 느린 통합 테스트 최소화

### ❌ DON'T
- 테스트에서 외부 시스템 직접 호출 (DB, API 등)
- 랜덤 값 사용 (테스트 결과가 불안정)
- 순서에 의존하는 테스트 (각 테스트는 독립적)
- 너무 많은 것을 한 테스트에서 검증
- 테스트를 스킵하거나 주석 처리

## 참고 자료

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Kotlin Test Documentation](https://kotlinlang.org/api/latest/kotlin.test/)
- [IntelliJ Platform Testing](https://plugins.jetbrains.org/docs/intellij/testing-plugins.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

**작성일**: 2025-11-10
**업데이트**: TDD 환경 구축 완료
