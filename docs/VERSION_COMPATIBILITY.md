# 버전 호환성 가이드 (Version Compatibility Guide)

## 개요

이 문서는 플러그인의 버전 호환성 전략과 대응 방안을 설명합니다.

## 현재 지원 범위

### IntelliJ Platform
- **최소 버전**: 2023.2 (build 232)
- **최대 버전**: 2024.1.* (build 241.*)
- **테스트 버전**: 2023.2.5

```kotlin
// build.gradle.kts
intellij {
    version.set("2023.2.5")
}

patchPluginXml {
    sinceBuild.set("232")
    untilBuild.set("241.*")
}
```

### JDK
- **요구 버전**: JDK 17
- **이유**: IntelliJ 2023.2부터 JDK 17 필수

### Kotlin
- **플러그인 버전**: 1.9.21
- **언어 타겟**: Java/Kotlin 모든 버전

### Java/Kotlin 프로젝트
- **지원**: Java 8+ / Kotlin 1.3+
- **이유**: JDI는 JVM 버전과 무관하게 동작

## 버전 업데이트 시 영향 분석

### 1. IDE 버전 업데이트

#### 문제점
IntelliJ Platform API는 버전마다 변경될 수 있습니다:
- Deprecated API 제거
- 새로운 API 추가
- 메서드 시그니처 변경

#### 대응 전략

**A. API 변경 최소화**
```kotlin
// ✅ 안정적인 API 사용
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebugSession

// ❌ 실험적 API 사용 지양
// import com.intellij.xdebugger.impl.InternalDebuggerAPI
```

**B. 호환성 레이어 구축**
```kotlin
// VersionCompatibility.kt
object ApiCompat {
    fun evaluateExpression(
        session: XDebugSession,
        expression: String
    ): CompletableFuture<Result> {
        // 버전별 분기 처리
        return when {
            isPlatform232OrLater() -> evaluateModern(session, expression)
            else -> evaluateLegacy(session, expression)
        }
    }

    private fun isPlatform232OrLater(): Boolean {
        val version = ApplicationInfo.getInstance().build.baselineVersion
        return version >= 232
    }
}
```

**C. 멀티 버전 테스트**
```yaml
# .github/workflows/compatibility-test.yml
strategy:
  matrix:
    idea-version: ['2023.2', '2023.3', '2024.1']

steps:
  - name: Test with IntelliJ ${{ matrix.idea-version }}
    run: |
      ./gradlew test -PideaVersion=${{ matrix.idea-version }}
```

#### 실제 영향 예시

**IntelliJ 2023.2 → 2024.1 변경사항:**
```kotlin
// 2023.2
XValuePresentation.renderValue(renderer: XValueTextRenderer)

// 2024.1 (가상 시나리오)
XValuePresentation.renderValue(
    renderer: XValueTextRenderer,
    context: XValueRenderContext  // 새 파라미터 추가
)
```

**대응 코드:**
```kotlin
fun renderValue(presentation: XValuePresentation) {
    if (supportsRenderContext()) {
        presentation.renderValue(renderer, createContext())
    } else {
        presentation.renderValue(renderer)
    }
}
```

### 2. JDI 버전 호환성

#### ✅ 좋은 소식: JDI는 매우 안정적

**JDI의 장점:**
- Java 1.3부터 존재 (20년 이상 안정)
- 하위 호환성 철저히 유지
- 표준 API로 거의 변경 없음

**실제 코드 (변경 가능성 낮음):**
```kotlin
// JDI API는 10년 이상 동일
val jdiValue = descriptor.value
when (jdiValue) {
    is IntegerValue -> jdiValue.value()  // Java 1.3부터 동일
    is ArrayReference -> jdiValue.getValue(i)  // 변경 없음
}
```

#### 새 Java 버전 대응

**Java 8 → Java 21 변화:**
- 새 기본 타입 없음 (int, long 등 동일)
- 새 언어 기능 (record, sealed class 등)은 **컴파일러 레벨**에서 처리됨
- JDI로 보면 여전히 `ObjectReference`

**예시: Java 17 Record**
```java
// Java 17
record Point(int x, int y) {}

// JDI로 보면
ObjectReference {
    type: "Point"
    fields: [
        { name: "x", value: IntegerValue(10) },
        { name: "y", value: IntegerValue(20) }
    ]
}
```

따라서 **Java 버전 업데이트는 플러그인에 거의 영향 없음** ✅

### 3. Kotlin 버전 호환성

#### Kotlin 1.9 → 2.0 변화

**Kotlin 플러그인 버전 (IDE 플러그인):**
```kotlin
// build.gradle.kts
intellij {
    plugins.set(listOf(
        "com.intellij.java",
        "org.jetbrains.kotlin"  // IDE의 Kotlin 플러그인 사용
    ))
}
```

**영향:**
- 사용자 프로젝트의 Kotlin 버전: **영향 없음** ✅
- JDI는 바이트코드 레벨에서 동작하므로 Kotlin 버전 무관

**예시:**
```kotlin
// Kotlin 2.0 data class
data class User(val name: String, val age: Int)

// JDI로 보면 (Kotlin 버전 무관)
ObjectReference {
    type: "User"
    fields: [
        { name: "name", value: StringReference("Alice") },
        { name: "age", value: IntegerValue(30) }
    ]
}
```

### 4. Python/JavaScript 버전

#### 문제점
Python과 JavaScript는 **JDI를 사용할 수 없습니다**.

#### 대응 전략

**Python:**
```kotlin
// debugpy 프로토콜 사용 (PyCharm 플러그인)
class PythonDebuggerIntegration : DebuggerIntegration {
    override fun evaluateExpression(
        session: XDebugSession,
        expression: String
    ): CompletableFuture<Result> {
        // PyDebugValue 사용
        val pyValue = session.currentStackFrame?.evaluator?.evaluate(...)
        return extractPythonValue(pyValue)
    }
}
```

**JavaScript:**
```kotlin
// Chrome DevTools Protocol 사용
class JSDebuggerIntegration : DebuggerIntegration {
    override fun evaluateExpression(...) {
        // V8 Runtime API 사용
        val result = runtime.evaluate(expression)
        return extractJSValue(result)
    }
}
```

**버전 대응:**
- Python 3.8+ → debugpy 버전 의존
- Node.js 14+ → Chrome DevTools Protocol 안정적

## 호환성 테스트 매트릭스

### 필수 테스트 조합

| IDE 버전 | Java 버전 | Kotlin 버전 | 우선순위 |
|---------|----------|------------|---------|
| 2023.2  | 17       | 1.9        | ⭐⭐⭐ |
| 2023.3  | 17       | 1.9        | ⭐⭐⭐ |
| 2024.1  | 17       | 2.0        | ⭐⭐   |
| 2024.2  | 21       | 2.0        | ⭐     |

### CI/CD 테스트 설정

```yaml
# .github/workflows/compatibility.yml
name: Version Compatibility Tests

on: [push, pull_request]

jobs:
  test-multiple-versions:
    strategy:
      matrix:
        idea-version: ['2023.2', '2023.3', '2024.1']
        java-version: ['17', '21']

    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Setup JDK ${{ matrix.java-version }}
        uses: actions/setup-java@v3
        with:
          java-version: ${{ matrix.java-version }}

      - name: Test with IntelliJ ${{ matrix.idea-version }}
        run: |
          ./gradlew test -PideaVersion=${{ matrix.idea-version }}
```

## 버전 업데이트 체크리스트

### IDE 버전 업그레이드 시

- [ ] 새 IDE 버전으로 빌드 테스트
- [ ] Deprecated API 경고 확인
- [ ] 전체 테스트 실행 (단위 + E2E)
- [ ] `untilBuild` 값 업데이트
- [ ] 플러그인 설명에 지원 버전 명시

### Java/Kotlin 버전 업그레이드 시

- [ ] 새 언어 기능 테스트 (record, sealed class 등)
- [ ] JDI가 새 타입을 올바르게 처리하는지 확인
- [ ] 예외 케이스 문서화

### Python/JS 지원 추가 시

- [ ] 디버거 프로토콜 연구
- [ ] 별도 추출기 구현
- [ ] 버전별 호환성 테스트

## 마이그레이션 가이드

### IntelliJ 2024.2+ 지원 추가

```kotlin
// 1. build.gradle.kts 업데이트
patchPluginXml {
    sinceBuild.set("232")
    untilBuild.set("242.*")  // 2024.2까지 확장
}

// 2. API 변경 대응
// ApiCompatLayer.kt
fun createDebuggerListener(): XDebuggerManagerListener {
    return if (isPlatform242OrLater()) {
        // 새 API 사용
        object : XDebuggerManagerListener {
            override fun processStarted(session: XDebugSession, context: ExecutionContext) {
                // 새 파라미터 사용
            }
        }
    } else {
        // 기존 API 사용
        object : XDebuggerManagerListener {
            override fun processStarted(session: XDebugSession) {
                // 기존 동작
            }
        }
    }
}

// 3. 테스트
./gradlew test -PideaVersion=2024.2
```

## 장기 지원 전략

### 지원 정책

**Tier 1 (완전 지원):**
- 최신 버전
- 최신 - 1 버전

**Tier 2 (제한 지원):**
- 최신 - 2 버전 (중요 버그 수정만)

**Tier 3 (미지원):**
- 3년 이상 된 버전

**예시 (2025년 기준):**
```
Tier 1: 2024.3, 2024.2
Tier 2: 2024.1
Tier 3: 2023.3 이하
```

## 결론

### ✅ 안정적인 부분
1. **JDI API**: 20년 이상 안정적, 변경 가능성 극히 낮음
2. **Java/Kotlin 언어 버전**: JDI는 바이트코드 레벨이라 영향 없음
3. **핵심 디버거 API**: IntelliJ이 하위 호환성 유지

### ⚠️ 주의 필요한 부분
1. **IntelliJ Platform API**: 버전마다 Deprecated 발생 가능
2. **UI 컴포넌트**: JCEF, Swing 인터페이스 변경 가능
3. **Python/JS 디버거**: 별도 프로토콜이라 개별 대응 필요

### 📋 권장 사항
1. **최소 2개 버전 테스트**: 현재 + 최신
2. **API 추상화 레이어**: 버전별 분기 처리
3. **적극적 모니터링**: IntelliJ 릴리스 노트 확인
4. **CI/CD 통합**: 멀티 버전 자동 테스트

---

**작성일**: 2025-11-10
**버전**: 1.0.0
