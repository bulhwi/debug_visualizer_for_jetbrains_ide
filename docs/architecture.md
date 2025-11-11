# 아키텍처 설계 (v2.0.0)

## 개요

**Debug Visualizer for JetBrains IDEs**는 VSCode Debug Visualizer의 핵심 아키텍처를 JetBrains 생태계로 포팅한 플러그인입니다. IntelliJ Platform을 기반으로 구축되며 다음 핵심 컴포넌트로 구성됩니다:

1. **플러그인 코어**: 디버거 통합, 실시간 이벤트 리스너
2. **Dispatching Backend System**: 언어별 백엔드 자동 선택
3. **Priority-based Data Extractor**: 타입 자동 감지 및 JSON 변환
4. **시각화 엔진**: React + D3.js/Plotly/vis.js 기반 다중 렌더러

## 시스템 아키텍처 (Phase 2 완료 후)

```
┌─────────────────────────────────────────────────────────────┐
│                     JetBrains IDE                           │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              플러그인 코어 (Kotlin)                     │  │
│  │                                                         │  │
│  │  ┌────────────────────────────────────────────────┐   │  │
│  │  │ DebuggerListener (XDebugSessionListener)      │   │  │
│  │  │  - sessionPaused() → F8 스텝 감지            │   │  │
│  │  │  - onSuspend() → refresh() 트리거            │   │  │
│  │  └────────────────────────────────────────────────┘   │  │
│  │         ↓                                              │  │
│  │  ┌────────────────────────────────────────────────┐   │  │
│  │  │ VisualizationWatchModel (Observable)          │   │  │
│  │  │  - StateFlow<DataExtractionState>            │   │  │
│  │  │  - CancellationToken (이전 요청 취소)         │   │  │
│  │  └────────────────────────────────────────────────┘   │  │
│  │         ↓                                              │  │
│  │  ┌────────────────────────────────────────────────┐   │  │
│  │  │ DispatchingVisualizationBackend               │   │  │
│  │  │  ┌───────────────────────────────────────┐    │   │  │
│  │  │  │ JvmVisualizationBackend (JDI)        │    │   │  │
│  │  │  │  - ExpressionEvaluator               │    │   │  │
│  │  │  │  - JdiValueConverter                 │    │   │  │
│  │  │  │  - Priority-based Extractors         │    │   │  │
│  │  │  └───────────────────────────────────────┘    │   │  │
│  │  │  ┌───────────────────────────────────────┐    │   │  │
│  │  │  │ JsVisualizationBackend (Phase 3)     │    │   │  │
│  │  │  │  - Chrome DevTools Protocol          │    │   │  │
│  │  │  │  - 런타임 코드 주입                   │    │   │  │
│  │  │  └───────────────────────────────────────┘    │   │  │
│  │  │  ┌───────────────────────────────────────┐    │   │  │
│  │  │  │ GenericVisualizationBackend          │    │   │  │
│  │  │  │  - 변수 참조 BFS 탐색 (모든 언어)     │    │   │  │
│  │  │  └───────────────────────────────────────┘    │   │  │
│  │  └────────────────────────────────────────────────┘   │  │
│  │         ↓                                              │  │
│  │  ┌────────────────────────────────────────────────┐   │  │
│  │  │ DataExtractorRegistry                        │   │  │
│  │  │  - GetVisualizationDataExtractor (priority: 600) │   │
│  │  │  - TreeExtractor (priority: 550)            │   │  │
│  │  │  - GraphExtractor (priority: 540)           │   │  │
│  │  │  - ArrayExtractor (priority: 500)           │   │  │
│  │  │  - MapExtractor (priority: 490)             │   │  │
│  │  │  - ToStringExtractor (priority: 50)         │   │  │
│  │  └────────────────────────────────────────────────┘   │  │
│  │         ↓                                              │  │
│  │  ┌────────────────────────────────────────────────┐   │  │
│  │  │ VisualizationData (JSON)                      │   │  │
│  │  │  kind: { array: true } | { tree: true } | ... │   │  │
│  │  └────────────────────────────────────────────────┘   │  │
│  │         ↓                                              │  │
│  │  ┌────────────────────────────────────────────────┐   │  │
│  │  │ JCEFVisualizationPanel                       │   │  │
│  │  │  - JBCefBrowser                              │   │  │
│  │  │  - showVisualization(json)                   │   │  │
│  │  └────────────────────────────────────────────────┘   │  │
│  └───────────────────────────────────────────────────────┘  │
│         ↓                                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ JCEF WebView (React UI)                              │  │
│  │                                                       │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │ window.visualizerAPI                          │  │  │
│  │  │  - updateVisualization(data)                  │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  │         ↓                                             │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │ VisualizationRouter (타입 판별)              │  │  │
│  │  │  - getVisualizationKind(data.kind)            │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  │         ↓                                             │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │ 다중 렌더러 (13개)                            │  │  │
│  │  │  - ArrayRenderer (D3.js 막대 그래프) ✅       │  │  │
│  │  │  - GraphRenderer (vis.js)                    │  │  │
│  │  │  - TreeRenderer (SVG)                        │  │  │
│  │  │  - TableRenderer (Perspective.js)            │  │  │
│  │  │  - PlotlyRenderer                            │  │  │
│  │  │  - ... (13개 타입)                           │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 컴포넌트 상세

### 1. 플러그인 코어

**책임:**
- 디버거 세션 관리 및 실시간 이벤트 리스닝
- 표현식 평가 (JDI 기반)
- JCEF 웹뷰 통합
- 도구 윈도우 생명주기 관리

**주요 클래스:**
```kotlin
// 메인 플러그인 진입점
class AlgorithmVisualizerPlugin : DumbAware

// 도구 윈도우 팩토리
class VisualizerToolWindowFactory : ToolWindowFactory

// 디버거 통합
class DebuggerIntegration {
    fun getCurrentSession(): XDebugSession?
    fun isDebugging(): Boolean
    fun isSuspended(): Boolean
    fun addListener(listener: DebuggerStateListener)
}

// 실시간 이벤트 리스너 (Phase 2-1)
class DebuggerListener(session: XDebugSession) : XDebugSessionListener {
    override fun sessionPaused() {
        // F8 스텝마다 호출
        onSuspend()
    }

    private fun onSuspend() {
        // 1. 알고리즘 감지 (Phase 2-0에서 제거 예정)
        // 2. 스냅샷 수집
        // 3. 시각화 트리거
    }
}

// 표현식 평가기 (JDI 기반)
class ExpressionEvaluator {
    fun evaluateAndExtract(
        session: XDebugSession,
        expression: String
    ): CompletableFuture<Pair<String?, String?>>
}

// JCEF 웹뷰 통합
class JCEFVisualizationPanel : JPanel {
    fun showVisualization(json: String)
    fun executeJavaScript(code: String)
}
```

**사용된 API:**
- `com.intellij.xdebugger.XDebuggerManager`
- `com.intellij.xdebugger.XDebugSessionListener`
- `com.intellij.xdebugger.evaluation.XDebuggerEvaluator`
- `com.intellij.debugger.engine.JavaValue` (JDI 기반)
- `com.sun.jdi.*` (ArrayReference, IntegerValue 등)
- `com.intellij.ui.jcef.JBCefBrowser`

### 2. Dispatching Backend System (Phase 2)

**목적:** 언어를 자동으로 감지하고 적절한 백엔드를 선택합니다.

```kotlin
interface VisualizationBackend {
    fun getVisualizationData(expression: String): VisualizationData
}

class DispatchingVisualizationBackend(
    private val backends: List<VisualizationBackend>
) {
    fun getVisualizationData(expression: String): VisualizationData {
        // 언어 감지
        val currentLanguage = detectLanguage()

        // 백엔드 선택
        val backend = when (currentLanguage) {
            "Java", "Kotlin" -> jvmBackend
            "JavaScript", "TypeScript" -> jsBackend
            "Python" -> pythonBackend
            else -> genericBackend
        }

        return backend.getVisualizationData(expression)
    }
}
```

**언어별 백엔드:**

1. **JvmVisualizationBackend** (Phase 1 완료)
   - JDI (Java Debug Interface) 네이티브 접근
   - ExpressionEvaluator로 표현식 평가
   - JdiValueConverter로 JDI Value → Kotlin 객체 변환

2. **JsVisualizationBackend** (Phase 3)
   - Chrome DevTools Protocol 사용
   - 런타임 코드 주입 (Data Extractor API)
   - 13개 기본 추출기 포팅

3. **PythonVisualizationBackend** (Phase 3)
   - debugpy 프로토콜 사용
   - 외부 모듈 `pydebugvisualizer` (PyPI 배포)

4. **GenericVisualizationBackend** (Phase 4)
   - XDebugger API로 변수 참조 BFS 탐색
   - 객체 그래프 생성 (모든 언어 fallback)
   - 최대 50개 노드 제한

### 3. Priority-based Data Extractor System

**핵심 개념:** VSCode Debug Visualizer와 동일한 우선순위 기반 시스템

```kotlin
interface DataExtractor {
    val id: String
    val priority: Int  // 높을수록 우선

    fun canExtract(value: Any): Boolean
    fun extract(value: Any): VisualizationData
}

class DataExtractorRegistry {
    private val extractors = mutableListOf<DataExtractor>()

    fun register(extractor: DataExtractor) {
        extractors.add(extractor)
        extractors.sortByDescending { it.priority }
    }

    fun selectBestExtractor(value: Any): DataExtractor? {
        return extractors.firstOrNull { it.canExtract(value) }
    }
}
```

**기본 추출기 (우선순위 순):**

1. **GetVisualizationDataExtractor** (priority: 600)
   ```kotlin
   // 객체에 getVisualizationData() 메서드가 있으면 사용
   fun canExtract(value: Any) =
       value.javaClass.methods.any { it.name == "getVisualizationData" }
   ```

2. **TreeExtractor** (priority: 550)
   ```kotlin
   // TreeNode 타입 감지
   fun canExtract(value: Any) =
       value.javaClass.name.contains("TreeNode") ||
       hasFields(value, listOf("left", "right", "val"))
   ```

3. **GraphExtractor** (priority: 540)
   ```kotlin
   // Graph 타입 감지
   fun canExtract(value: Any) =
       value.javaClass.name.contains("Graph") ||
       hasFields(value, listOf("nodes", "edges"))
   ```

4. **ArrayExtractor** (priority: 500)
   ```kotlin
   // 배열 및 리스트
   fun canExtract(value: Any) =
       value is IntArray || value is Array<*> || value is List<*>
   ```

5. **MapExtractor** (priority: 490)
   ```kotlin
   // Map, HashMap 등
   fun canExtract(value: Any) = value is Map<*, *>
   ```

6. **ListExtractor** (priority: 480)
   ```kotlin
   // List, ArrayList 등
   fun canExtract(value: Any) = value is List<*>
   ```

7. **ObjectGraphExtractor** (priority: 98)
   ```kotlin
   // 일반 객체 (낮은 우선순위)
   fun canExtract(value: Any) = true
   ```

8. **ToStringExtractor** (priority: 50)
   ```kotlin
   // 마지막 fallback (toString() 호출)
   fun canExtract(value: Any) = true
   ```

### 4. 시각화 엔진 (React UI)

**기술 스택:**
- **프레임워크**: React 18.2 + TypeScript
- **빌드**: Vite 5.0
- **렌더링**: D3.js (배열), vis.js (그래프), Perspective.js (테이블)
- **테스트**: Vitest + React Testing Library

**VSCode 호환 데이터 스키마:**
```typescript
interface VisualizationData {
  kind: {
    graph?: true
    tree?: true
    array?: true
    table?: true
    plotly?: true
    grid?: true
    text?: true
    monacoText?: true
    image?: true
    svg?: true
    'graphviz-dot'?: true
    ast?: true
    'object-graph'?: true
  }
  // kind별 추가 필드
}
```

**컴포넌트 구조:**
```typescript
// 메인 애플리케이션
function App() {
  const [data, setData] = useState<VisualizationData | null>(null)

  useEffect(() => {
    window.visualizerAPI = {
      updateVisualization: (dataStr: string) => {
        const parsed = JSON.parse(dataStr)
        setData(parsed)
      }
    }
  }, [])

  return (
    <VisualizationRouter data={data} />
  )
}

// 타입 판별 및 라우팅
function VisualizationRouter({ data }: { data: VisualizationData }) {
  const kind = getVisualizationKind(data)

  switch (kind) {
    case 'array': return <ArrayRenderer data={data} />
    case 'tree': return <TreeRenderer data={data} />
    case 'graph': return <GraphRenderer data={data} />
    case 'table': return <TableRenderer data={data} />
    // ... 13개 타입
    default: return <TextRenderer data={data} />
  }
}

// D3.js 기반 배열 렌더러
function ArrayRenderer({ data }: { data: ArrayVisualizationData }) {
  const svgRef = useRef<SVGSVGElement>(null)

  useEffect(() => {
    // D3.js 막대 그래프 렌더링
    const svg = d3.select(svgRef.current)
    // ...
  }, [data])

  return <svg ref={svgRef} />
}
```

## 데이터 흐름 (Phase 2 완료 후)

### 실시간 모니터링 파이프라인

```
[사용자 F8 스텝 실행]
        ↓
XDebugSession.sessionPaused()
        ↓
DebuggerListener.onSuspend()
        ↓
VisualizationWatchModel.refresh()
   ├─ 1. 이전 요청 취소 (CancellationToken)
   ├─ 2. 상태 = "loading"
   └─ 3. 비동기 평가 시작
        ↓
DispatchingVisualizationBackend.getVisualizationData(expression)
   ├─ 언어 감지 (Java/Kotlin/Python/JS/Generic)
   └─ 적절한 백엔드 선택
        ↓
[예: JvmVisualizationBackend]
   ├─ ExpressionEvaluator.evaluate(expression)
   ├─ JdiValueConverter.convert(jdiValue)
   ├─ extractors.selectBestExtractor(value)
   └─ extractor.extract(value) → VisualizationData
        ↓
JSON 직렬화
        ↓
상태 = "data" (Observable 변경)
        ↓
autorun 트리거 (Kotlin Flow)
        ↓
JCEFVisualizationPanel.showVisualization(json)
   ├─ JSON 이스케이프 처리
   └─ browser.executeJavaScript("window.visualizerAPI.updateVisualization('...')")
        ↓
[JCEF WebView - React]
        ↓
window.visualizerAPI.updateVisualization(jsonStr)
   ├─ JSON 파싱
   └─ setState({ data })
        ↓
React 리렌더링
        ↓
VisualizationRouter
   ├─ if (data.kind.graph) → <GraphRenderer />
   ├─ if (data.kind.tree) → <TreeRenderer />
   ├─ if (data.kind.array) → <ArrayRenderer />
   └─ ...
        ↓
[사용자에게 시각화 표시] (< 100ms)
```

## 설정 및 확장성

### 1. 커스텀 추출기 (Extension Point)

사용자가 자신의 데이터 구조를 위한 추출기를 정의할 수 있습니다:

```kotlin
// plugin.xml
<extensions defaultExtensionNs="com.github.algorithmvisualizer">
    <dataExtractor implementation="com.mycompany.MyCustomExtractor"/>
</extensions>

// 구현
class MyCustomExtractor : DataExtractor {
    override val id = "my-custom-tree"
    override val priority = 700  // 기본 TreeExtractor보다 높음

    override fun canExtract(value: Any) =
        value.javaClass.name == "com.example.MyTree"

    override fun extract(value: Any): VisualizationData {
        val tree = value as MyTree
        return VisualizationData(
            kind = mapOf("tree" to true),
            // ...
        )
    }
}
```

### 2. 커스텀 렌더러 (React 컴포넌트)

```typescript
// visualizer-ui/src/components/MyCustomRenderer.tsx
export function MyCustomRenderer({ data }: { data: MyCustomVisualizationData }) {
  // D3.js, Canvas, SVG 등 사용
  return <svg>...</svg>
}

// App.tsx에 등록
function VisualizationRouter({ data }: { data: VisualizationData }) {
  // ...
  if ('myCustom' in data.kind) {
    return <MyCustomRenderer data={data} />
  }
  // ...
}
```

## 성능 고려사항

### 1. 대용량 데이터 구조
- **페이지네이션**: 표시되는 노드/요소 제한 (기본 1000개)
- **가상 스크롤링**: 배열/테이블 뷰용
- **레벨 기반 렌더링**: 깊은 트리용 (처음 N 레벨만 표시)

### 2. 실시간 업데이트
- **디바운싱**: F8 스텝 중 업데이트 빈도 제한 (330ms)
- **취소 토큰**: 이전 요청 취소 (CancellationToken)
- **증분 업데이트**: 변경된 노드만 업데이트

### 3. 메모리 관리
- **지연 로딩**: 필요 시 시각화 데이터 로드
- **정리**: 전환 시 이전 시각화 폐기
- **약한 참조**: 디버거 통합에서 메모리 누수 방지

## 보안

### 1. 코드 인젝션 방지
- 모든 사용자 입력 표현식 새니타이제이션
- 안전한 평가 API만 사용 (XDebuggerEvaluator)
- 시각화 코드에 `eval()` 사용 금지

### 2. 샌드박스
- JCEF는 격리된 컨텍스트에서 실행
- WebView에서 파일 시스템 접근 불가
- CSP (Content Security Policy) 헤더

## 테스트 전략

### 1. 단위 테스트
- **Kotlin**: ExpressionEvaluator, DataExtractors (MockK)
- **React**: 각 렌더러 컴포넌트 (Vitest + RTL)
- 예상 테스트 수: 90개 이상 (Phase 2 완료 후)

### 2. 통합 테스트
- `./gradlew runIde`로 테스트 IDE 인스턴스 실행
- 샘플 프로젝트로 실제 디버깅 시나리오 테스트

### 3. E2E 테스트 (Phase 2-5)
- F8 스텝 → 실시간 업데이트 검증
- 13개 시각화 타입별 테스트 케이스
- 성능 벤치마크 (F8 → UI < 100ms)

## Phase 2 로드맵

### Phase 2-0: 코드 정리 (진행 중)
- [#32] AlgorithmDetector 제거
- [#33] SnapshotCollector 범용화
- [#34] React UI 리팩토링 (Play 버튼 제거)
- [#35] 통합 테스트 및 문서 업데이트

### Phase 2-1: 디버거 이벤트 리스너 (#23)
- XDebugSessionListener 구현
- sessionPaused() → refresh() 파이프라인

### Phase 2-2: Observable 상태 관리 (#24)
- VisualizationWatchModel (StateFlow)
- CancellationToken, 디바운싱

### Phase 2-3: Priority-based Extractor (#25)
- DataExtractorRegistry
- 8개 기본 추출기 구현

### Phase 2-4: React 다중 렌더러 (#26)
- GraphRenderer (vis.js)
- TreeRenderer (SVG)
- TableRenderer (Perspective.js)

### Phase 2-5: 실시간 파이프라인 통합 (#27)
- F8 → 시각화 전체 파이프라인
- E2E 테스트

---

**마지막 업데이트**: 2025-01-12
**문서 버전**: 2.0.0 (VSCode 기반)
**참고**: [PRD v2.0.0](PRD.md), [VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer)
