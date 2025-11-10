# 교훈 및 이슈 정리 (Lessons Learned)

## 날짜: 2025-11-10

## 🎯 핵심 교훈

### 1. IntelliJ XDebugger API의 제한사항

#### 문제
`XValue.computePresentation()` API를 사용하여 배열 요소의 값을 추출하려 했으나:
- `setPresentation(icon, type, value, hasChildren)` 오버로드가 호출되지 않음
- `setPresentation(icon, XValuePresentation, hasChildren)` 오버로드만 호출됨
- `XValuePresentation`의 `type` 속성이 `null`로 반환됨
- `XValuePresentation.renderValue()` API 시그니처가 복잡하고 여러 버전이 존재

#### 시도한 해결 방법들
1. ❌ `descriptor.valueText` - 빈 문자열 반환
2. ❌ `descriptor.calcValueName()` - 변수명만 반환
3. ❌ `descriptor.calcValue()` - 파라미터 필요, 컴파일 에러
4. ❌ `XValuePresentation.renderValue()` - API 시그니처 불일치
5. ❌ `SimpleColoredText` 사용 - 타입 미스매치
6. ❌ `computeChildren()` + `computePresentation()` - 값이 준비되지 않은 상태에서 접근 시도
   - 에러: "Value is not yet calculated for class ArrayElementDescriptorImpl"

#### ✅ 최종 해결책: JDI 직접 사용

```kotlin
// DebuggerCommandImpl을 통해 디버거 스레드에서 실행
debugProcess.managerThread.invoke(object : DebuggerCommandImpl() {
    override fun action() {
        val jdiValue = descriptor.value
        if (jdiValue is ArrayReference) {
            val length = jdiValue.length()
            for (i in 0 until length) {
                val element = jdiValue.getValue(i)
                val elementStr = when (element) {
                    is CharValue -> "'${element.value()}'"
                    is PrimitiveValue -> element.toString()
                    is StringReference -> "\"${element.value()}\""
                    else -> element?.toString() ?: "null"
                }
                values.add(elementStr)
            }
        }
    }
})
```

**핵심 교훈:**
- IntelliJ의 XDebugger API는 추상화 레이어이며, 복잡한 값 추출에는 한계가 있음
- 배열/컬렉션 등 복잡한 구조는 **JDI (Java Debug Interface)를 직접 사용**해야 함
- `DebuggerCommandImpl`을 통해 디버거 스레드에서 안전하게 JDI에 접근할 것

---

### 2. 비동기 처리와 타이밍 이슈

#### 문제
- `computePresentation()` 호출 시 콜백이 즉시 실행되지 않음
- `addChildren()` 호출 시 자식 값들이 아직 계산되지 않은 상태
- `descriptor.value`를 즉시 호출하면 에러 발생

#### 해결
- `CompletableFuture`를 사용한 비동기 처리
- 타임아웃 설정 (2-5초)
- `DebuggerCommandImpl`을 통한 동기화된 접근

**핵심 교훈:**
- IntelliJ 디버거 API는 **비동기적**으로 동작함
- 값이 준비될 때까지 기다려야 하며, 즉시 접근하면 안 됨
- JDI 사용 시 반드시 `DebuggerCommandImpl` 사용

---

### 3. API 버전 호환성

#### 문제
IntelliJ Platform SDK 2023.2.5 기준:
- `XValueTextRenderer` 인터페이스에 여러 버전의 메서드 존재
- `renderValue(String)` vs `renderValue(String, TextAttributesKey)`
- `renderStringValue(String)` vs `renderStringValue(String, String?, TextAttributesKey, Int)`
- `XCompositeNode.setErrorMessage(String)` vs `setErrorMessage(String, XDebuggerTreeNodeHyperlink?)`

#### 해결
- 모든 오버로드를 명시적으로 구현하려 했으나 실패
- 최종적으로 JDI 직접 사용으로 우회

**핵심 교훈:**
- IntelliJ Platform API는 버전별로 시그니처가 다를 수 있음
- 복잡한 인터페이스는 최대한 피하고, 저수준 API (JDI) 사용 고려
- SDK 문서와 실제 구현이 다를 수 있으므로 테스트 필수

---

### 4. 초기화 순서 문제

#### 문제
```kotlin
init {
    setupDebuggerListener()  // ❌ evaluateButton이 아직 초기화되지 않음
    expressionField = ...
    evaluateButton = ...
}
```

`NullPointerException` 발생: `evaluateButton`이 `null`

#### 해결
```kotlin
init {
    val topPanel = createTopPanel()
    expressionField = extractFromPanel(topPanel)
    evaluateButton = extractFromPanel(topPanel)
    setupDebuggerListener()  // ✅ 모든 필드 초기화 후 호출
}
```

**핵심 교훈:**
- UI 컴포넌트는 **모든 필드를 먼저 초기화**한 후 리스너 등록
- `lateinit var` 사용 시 초기화 순서에 특히 주의

---

## 🔧 기술적 세부사항

### ExpressionEvaluator.kt 구조

```
evaluateAndExtract()
  ├─ evaluateExpression()              // 표현식 평가
  │    └─ XDebuggerEvaluator.evaluate()
  │         └─ XEvaluationCallback
  │              ├─ evaluated(XValue)
  │              └─ errorOccurred(String)
  │
  └─ extractValueString()              // 값 추출
       └─ computePresentation()         // ❌ 배열 요소는 제대로 추출 안 됨
       └─ extractChildValues()          // ✅ JDI 직접 사용
            └─ DebuggerCommandImpl
                 └─ ArrayReference.getValue(i)
```

### JDI 타입별 처리

```kotlin
when (element) {
    is CharValue -> "'${element.value()}'"        // char: '1'
    is StringReference -> "\"${element.value()}\""  // String: "hello"
    is PrimitiveValue -> element.toString()       // int, boolean 등
    is ArrayReference -> extractChildValues(...)  // 중첩 배열 (재귀)
    is ObjectReference -> element.toString()      // 객체 참조
    else -> element?.toString() ?: "null"
}
```

---

## 📝 코드 품질

### 로깅 전략
```kotlin
// ✅ 좋은 예: 명확한 구분과 상세 정보
logger.warn("=== EXTRACTING value from XValue type: ${xValue.javaClass.name} ===")
logger.warn("=== Array[$i]: $elementStr ===")

// ❌ 나쁜 예: 불명확하거나 정보 부족
logger.info("Extracting...")
```

**핵심 교훈:**
- 디버깅 시 `===`로 구분하여 로그 식별 용이
- `logger.warn()` 사용하여 중요 로그 눈에 띄게
- 타입, 값, 상태 등 구체적 정보 포함

---

## 🚨 주의사항

### 1. 디버거 스레드 안전성
```kotlin
// ❌ 잘못된 예
val jdiValue = descriptor.value  // EDT 스레드에서 직접 호출

// ✅ 올바른 예
debugProcess.managerThread.invoke(object : DebuggerCommandImpl() {
    override fun action() {
        val jdiValue = descriptor.value  // 디버거 스레드에서 호출
    }
})
```

### 2. CompletableFuture 타임아웃
```kotlin
CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute {
    if (!future.isDone) {
        future.complete("<timeout>")
    }
}
```
- 타임아웃 설정 필수 (무한 대기 방지)
- 적절한 기본값 제공 (`<timeout>`, `<error>` 등)

### 3. 리소스 정리
```kotlin
override fun dispose() {
    jsToJavaQuery.dispose()
    browser.dispose()
}
```
- JCEF 브라우저는 반드시 `dispose()` 호출
- 메모리 누수 방지

---

## 🎓 배운 점 요약

1. **추상화의 한계**: 고수준 API가 항상 충분하지 않음. 필요시 저수준 API 사용
2. **비동기의 복잡성**: 콜백, 타이밍, 스레드 안전성 모두 고려 필요
3. **API 버전 관리**: SDK 버전별 차이 주의, 실제 테스트 필수
4. **초기화 순서**: UI 컴포넌트 초기화 순서 중요
5. **로깅의 중요성**: 상세한 로깅이 디버깅 시간 단축

---

## 🔮 향후 개선 방향

### Phase 1-9에서 구현할 사항
1. **타입별 추출기 확장**
   - `List`, `Set`, `Map` 등 컬렉션
   - 사용자 정의 클래스 (재귀 처리)
   - 순환 참조 감지 및 처리

2. **성능 최적화**
   - 큰 배열 (1000+ 요소) 처리
   - 페이지네이션 또는 샘플링
   - 캐싱 전략

3. **에러 처리 개선**
   - 더 구체적인 에러 메시지
   - 복구 가능한 에러 vs 치명적 에러 구분
   - 사용자 친화적 에러 표시

---

**작성일**: 2025-11-10
**작성자**: Claude Code
**버전**: 1.0.0
