# 제품 요구사항 정의서 (PRD)
# Debug Visualizer for JetBrains IDEs

**버전**: 2.0.0
**작성일**: 2025-01-12
**상태**: 재정의 (VSCode Debug Visualizer 기반)
**참고**: [VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer)

---

## 1. 개요

### 1.1 제품 비전

**Debug Visualizer for JetBrains IDEs**는 VSCode Debug Visualizer에서 영감을 받아, JetBrains 생태계에 동일한 수준의 디버깅 시각화 경험을 제공하는 플러그인입니다.

**핵심 개념:**
> "A visual watch window that lets you visualize your data structures while debugging"

기존 Watch 창의 텍스트 기반 표시를 넘어, **F8 스텝마다 자동으로 업데이트되는 실시간 시각화**를 제공합니다.

### 1.2 VSCode Debug Visualizer와의 차이점

| 특징 | VSCode Debug Visualizer | JetBrains Debug Visualizer |
|------|------------------------|---------------------------|
| 플랫폼 | Visual Studio Code | IntelliJ IDEA, PyCharm, WebStorm, CLion 등 |
| 디버거 API | DAP (Debug Adapter Protocol) | XDebugger API + JDI |
| 데이터 추출 (JS) | 런타임 코드 주입 | JDI 직접 사용 |
| 데이터 추출 (Java) | DAP 변수 참조 탐색 | JDI 네이티브 접근 (더 강력) |
| 웹뷰 | Electron WebView | JCEF (Chromium Embedded Framework) |
| 시각화 라이브러리 | 동일 (D3.js, Plotly, vis.js 등) | 동일 |

### 1.3 핵심 원칙

**1. 실시간 모니터링 우선**
- ❌ "Play" 버튼으로 애니메이션 재생
- ✅ F8 스텝마다 즉시 UI 업데이트

**2. 브레이크포인트 위치 무관**
- ❌ 특정 알고리즘 메서드에서만 동작
- ✅ Variables 패널에 보이는 모든 변수 시각화 가능

**3. 자료구조 중립적**
- ❌ 정렬 알고리즘만 지원
- ✅ 배열, 트리, 그래프, 스택, 큐, 맵 등 모든 자료구조

**4. 타입 자동 감지**
- ❌ 사용자가 시각화 타입 수동 선택
- ✅ 데이터 구조를 분석하여 자동으로 최적 시각화 선택

**5. 다중 언어 지원**
- ✅ Java, Kotlin, Python, JavaScript, TypeScript, C++, C#, Go, Rust 등

---

## 2. 핵심 기능

### 2.1 시각화 타입 (13종)

VSCode Debug Visualizer와 동일한 시각화 타입 지원:

| 타입 | 설명 | 사용 예시 |
|------|------|-----------|
| **graph** | 노드-엣지 그래프 | 그래프 알고리즘 (DFS, BFS, Dijkstra) |
| **tree** | 계층적 트리 | 이진 트리, AVL, Red-Black Tree |
| **array** | 1D 배열 막대 그래프 | 정렬 알고리즘, 투 포인터 |
| **table** | 2D 테이블 | DP 테이블, 행렬 |
| **grid** | 2D 그리드 | 체스판, 미로, 2D 게임 |
| **plotly** | Plotly.js 차트 | 통계, 시계열 데이터 |
| **text** | 포맷된 텍스트 | JSON, XML, 로그 |
| **monacoText** | 코드 에디터 | 소스 코드, Diff |
| **image** | 이미지 (PNG base64) | 이미지 처리 알고리즘 |
| **svg** | SVG | 벡터 그래픽 |
| **graphviz-dot** | Graphviz DOT | 복잡한 그래프 |
| **ast** | 추상 구문 트리 | 컴파일러, 파서 |
| **object-graph** | 객체 참조 그래프 | 메모리 구조, 포인터 관계 |

### 2.2 실시간 모니터링 워크플로우

```
1. 사용자가 디버깅 세션 시작 (중단점 설정)
   ↓
2. Debug Visualizer 툴 윈도우 열기
   - Command: "Debug Visualizer: New View"
   - 또는 단축키: Ctrl+Shift+V (예정)
   ↓
3. 표현식 입력
   - 예: myTree
   - 예: graph.adjacencyList
   - 예: dpTable
   ↓
4. 디버거가 중단점에서 멈춤
   ↓
5. 자동으로 표현식 평가 및 시각화 렌더링
   ↓
6. 사용자가 F8 (Step Over) 실행
   ↓
7. 즉시 표현식 재평가 → 시각화 자동 업데이트
   ↓
8. 변경된 부분 하이라이트 (diff)
   ↓
9. 반복 (디버깅 세션 종료까지)
```

### 2.3 데이터 추출 방식 (언어별)

#### **Tier 1: 완전 지원 (JavaScript/TypeScript)**
- **방식**: 런타임 코드 주입
- **구현**: VSCode와 동일하게 Data Extractor API 주입
- **추출기**: 13개 기본 추출기 자동 등록
- **사용자 정의**: Custom Extractor 지원

```javascript
// 사용자 코드에 자동으로 주입되는 API
class MyTree {
  getVisualizationData() {
    return {
      kind: { tree: true },
      root: {
        id: String(this.value),
        label: String(this.value),
        children: this.children.map(c => c.getVisualizationData().root)
      }
    };
  }
}
```

#### **Tier 1: 완전 지원 (Java/Kotlin)**
- **방식**: JDI (Java Debug Interface) 직접 사용
- **장점**: VSCode보다 더 강력한 제어 (네이티브 접근)
- **구현**: 현재 Phase 1 완료
- **추출기**: 리플렉션 기반 자동 타입 감지

```kotlin
// ExpressionEvaluator + JdiValueConverter
val jdiValue = debugSession.evaluate("myTree")
val visualization = when (jdiValue.type().name()) {
    "TreeNode" -> convertToTreeVisualization(jdiValue)
    "int[]" -> convertToArrayVisualization(jdiValue)
    "HashMap" -> convertToTableVisualization(jdiValue)
    else -> convertToObjectGraph(jdiValue)
}
```

#### **Tier 2: 기본 지원 (Python)**
- **방식**: 외부 모듈 (`pydebugvisualizer` - 예정)
- **구현**: debugpy 프로토콜 연동
- **사용자 요구**: `pip install pydebugvisualizer`

```python
# 사용자 코드
class TreeNode:
    def get_visualization_data(self):
        return {
            "kind": {"tree": True},
            "root": {
                "id": str(id(self)),
                "label": str(self.value),
                "children": [c.get_visualization_data()["root"] for c in self.children]
            }
        }

# 디버거에서
# visualize(myTree) → JSON 반환
```

#### **Tier 3: 범용 지원 (C++, C#, Go, Rust 등)**
- **방식**: XDebugger API 변수 참조 재귀 탐색
- **제한**: 최대 50개 노드
- **구현**: GenericVisualizationBackend

```kotlin
// 모든 언어에 적용 가능한 범용 방식
fun constructObjectGraph(expression: String): VisualizationData {
    val rootValue = debugSession.evaluate(expression)
    val nodes = mutableListOf<GraphNode>()
    val edges = mutableListOf<GraphEdge>()

    // BFS로 변수 참조 탐색
    val queue = ArrayDeque<XValue>()
    queue.add(rootValue)

    while (queue.isNotEmpty() && nodes.size < 50) {
        val value = queue.removeFirst()
        val children = value.computeChildren() // XDebugger API

        children.forEach { child ->
            nodes.add(GraphNode(child.name, child.value))
            edges.add(GraphEdge(value.name, child.name))
            queue.add(child)
        }
    }

    return VisualizationData(kind = "graph", nodes, edges)
}
```

### 2.4 우선순위 기반 추출기 시스템

VSCode Debug Visualizer의 핵심 메커니즘:

```kotlin
interface DataExtractor {
    val id: String
    val priority: Int // 높을수록 우선

    fun canExtract(value: Any): Boolean
    fun extract(value: Any): VisualizationData
}

// 기본 추출기 등록 (우선순위 순)
val extractors = listOf(
    GetVisualizationDataExtractor(priority = 600),  // .getVisualizationData() 메서드
    TreeExtractor(priority = 550),                  // TreeNode 타입
    GraphExtractor(priority = 540),                 // Graph 타입
    ArrayExtractor(priority = 500),                 // 배열
    MapExtractor(priority = 490),                   // Map, HashMap
    ListExtractor(priority = 480),                  // List, ArrayList
    ObjectGraphExtractor(priority = 98),            // 일반 객체 (낮은 우선순위)
    ToStringExtractor(priority = 50)                // 마지막 fallback
)

// 추출 로직
fun extractVisualizationData(value: Any): VisualizationData {
    val applicableExtractors = extractors
        .filter { it.canExtract(value) }
        .sortedByDescending { it.priority }

    return applicableExtractors.first().extract(value)
}
```

---

## 3. 기능 요구사항

### 3.1 MVP (Phase 1) ✅ **완료**

#### F1. JDI 기반 값 추출 ✅
- [x] ExpressionEvaluator 구현
- [x] JdiValueConverter 구현
- [x] 모든 프리미티브 타입 (int, long, float, double, boolean, char, byte, short)
- [x] 문자열 및 배열 (중첩 배열 포함)
- [x] null 값 처리
- [x] 타임아웃 처리 (5초)
- [x] 35개 단위 테스트 (100% 성공)

#### F2. JCEF 웹뷰 통합 ✅
- [x] JBCefBrowser 초기화
- [x] React UI 번들링 (Vite)
- [x] 인라인 HTML/CSS/JS 로딩
- [x] JavaScript ↔ Kotlin 브리지 (`window.visualizerAPI`)
- [x] Fallback HTML 지원

#### F3. React UI 기본 시각화 ✅
- [x] React 18.2 + TypeScript
- [x] D3.js 기반 배열 막대 그래프
- [x] Vitest + React Testing Library (10개 테스트)
- [x] 인터랙티브 마우스 오버
- [x] Viridis 색상 그라디언트

#### F4. 툴 윈도우 ✅
- [x] VisualizerToolWindowFactory
- [x] 표현식 입력 필드
- [x] Evaluate 버튼
- [x] 시각화 영역 (JCEF 패널)

### 3.2 Phase 2: 실시간 모니터링 시스템 (현재 진행 중)

#### F5. 디버거 이벤트 추적
- [ ] XDebugSessionListener 구현
  - [ ] `sessionPaused()` - F8 스텝 감지
  - [ ] `sessionResumed()` - 실행 재개
  - [ ] `stackFrameChanged()` - 스택 프레임 변경
- [ ] 활성 스택 프레임 ID 추적
- [ ] 표현식 자동 재평가

#### F6. 실시간 업데이트 파이프라인
```kotlin
// 목표 아키텍처
XDebugSession.sessionPaused()
  ↓
DebuggerListener.onSuspend()
  ↓
VisualizationWatchModel.refresh()
  ↓
VisualizationBackend.getVisualizationData(expression)
  ↓
extractors.selectBestExtractor(value).extract()
  ↓
JSON 변환
  ↓
JCEFVisualizationPanel.showVisualization(json)
  ↓
window.visualizerAPI.updateVisualization(data)
  ↓
React 리렌더링 (< 100ms)
```

- [ ] Observable 상태 관리 (MobX 또는 Kotlin Flow)
- [ ] 취소 토큰 (이전 요청 취소)
- [ ] 디바운싱 (에러 후 330ms 대기)
- [ ] WebSocket 또는 JCEF 브리지로 실시간 전송

#### F7. 다중 시각화 타입 지원
- [ ] **graph**: vis.js 기반 그래프 렌더러
- [ ] **tree**: SVG 기반 트리 렌더러
- [ ] **table**: Perspective.js 테이블
- [ ] **plotly**: Plotly.js 차트
- [ ] **grid**: HTML 그리드
- [ ] **text**: Monaco Editor (코드)

#### F8. 타입 자동 감지 시스템
```kotlin
// 우선순위 기반 추출기
interface VisualizationBackend {
    fun getVisualizationData(expression: String): VisualizationData
}

class DispatchingVisualizationBackend(
    private val backends: List<VisualizationBackend>
) {
    fun getVisualizationData(expression: String): VisualizationData {
        // 언어별 백엔드 선택
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

- [ ] JvmVisualizationBackend (현재 구현 완료)
- [ ] JsVisualizationBackend (Phase 3)
- [ ] PythonVisualizationBackend (Phase 3)
- [ ] GenericVisualizationBackend (모든 언어)

### 3.3 Phase 3: 다중 언어 지원

#### F9. JavaScript/TypeScript 완전 지원
- [ ] Chrome DevTools Protocol 연동
- [ ] 런타임 코드 주입 메커니즘
- [ ] Data Extractor API 번들 생성
- [ ] 13개 기본 추출기 포팅

#### F10. Python 기본 지원
- [ ] debugpy 프로토콜 연동
- [ ] `pydebugvisualizer` 모듈 개발
- [ ] PyPI 배포

#### F11. 범용 언어 지원 (C++, C#, Go, Rust)
- [ ] XDebugger 변수 참조 재귀 탐색
- [ ] 객체 그래프 자동 생성
- [ ] 최대 50개 노드 제한

### 3.4 Phase 4: 고급 기능

#### F12. 사용자 정의 추출기
- [ ] Extension Point 제공
- [ ] Kotlin DSL로 추출기 정의
- [ ] 우선순위 설정 가능

```kotlin
// 사용자 정의 추출기 예시
class MyCustomExtractor : DataExtractor {
    override val id = "my-custom-tree"
    override val priority = 700 // 기본 TreeExtractor보다 높음

    override fun canExtract(value: Any) =
        value.javaClass.name == "com.example.MyTree"

    override fun extract(value: Any): VisualizationData {
        val tree = value as MyTree
        return VisualizationData(
            kind = "tree",
            root = TreeNode(
                id = tree.id.toString(),
                label = tree.label,
                children = tree.children.map { extract(it).root }
            )
        )
    }
}
```

#### F13. 실행 히스토리 (Time-Travel Debugging)
- [ ] 모든 스텝의 스냅샷 저장
- [ ] 이전/다음 버튼으로 탐색
- [ ] 슬라이더로 특정 시점 점프

#### F14. 내보내기
- [ ] PNG/SVG 이미지
- [ ] 애니메이션 GIF
- [ ] HTML 리포트

---

## 4. 데이터 스키마

VSCode Debug Visualizer와 **100% 호환**되는 JSON 스키마 사용:

```typescript
// 공통 구조
interface VisualizationData {
  kind: {
    [key: string]: true; // discriminated union
  };
  // kind별 추가 필드
}

// 1. Graph
interface GraphVisualizationData {
  kind: { graph: true };
  nodes: Array<{
    id: string;
    label?: string;
    color?: string;
    shape?: "box" | "circle" | "ellipse";
  }>;
  edges: Array<{
    from: string;
    to: string;
    label?: string;
    color?: string;
    dashes?: boolean;
  }>;
}

// 2. Tree
interface TreeVisualizationData {
  kind: { tree: true };
  root: TreeNode;
}

interface TreeNode {
  id: string;
  label: string;
  children?: TreeNode[];
  color?: string;
  isMarked?: boolean;
}

// 3. Array
interface ArrayVisualizationData {
  kind: { array: true };
  items: Array<{
    value: number;
    label?: string;
    color?: string;
    isHighlighted?: boolean;
  }>;
}

// 4. Table
interface TableVisualizationData {
  kind: { table: true };
  rows: Array<{ [key: string]: any }>;
  columns?: Array<{ key: string; label: string }>;
}

// 5. Plotly
interface PlotlyVisualizationData {
  kind: { plotly: true };
  data: any[]; // Plotly.js data
  layout?: any; // Plotly.js layout
}

// ... 나머지 8개 타입
```

**참고**: [VSCode Debug Visualizer JSON Schema](https://hediet.github.io/visualization/docs/visualization-data-schema.json)

---

## 5. 아키텍처

### 5.1 시스템 컴포넌트

```
┌─────────────────────────────────────────────────────┐
│                JetBrains IDE                        │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │ Debug Visualizer Plugin (Kotlin)             │  │
│  │                                              │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │ VisualizerToolWindow                   │ │  │
│  │  │  - Expression Input Field              │ │  │
│  │  │  - JCEF WebView Container             │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  │                                              │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │ DebuggerListener                       │ │  │
│  │  │  - XDebugSessionListener               │ │  │
│  │  │  - onSuspend() → refresh()            │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  │                                              │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │ VisualizationBackend (Dispatcher)      │ │  │
│  │  │                                        │ │  │
│  │  │  ┌──────────────────────────────────┐ │ │  │
│  │  │  │ JvmVisualizationBackend          │ │ │  │
│  │  │  │  - ExpressionEvaluator           │ │ │  │
│  │  │  │  - JdiValueConverter             │ │ │  │
│  │  │  │  - TreeExtractor                 │ │ │  │
│  │  │  │  - GraphExtractor                │ │ │  │
│  │  │  │  - ArrayExtractor                │ │ │  │
│  │  │  └──────────────────────────────────┘ │ │  │
│  │  │                                        │ │  │
│  │  │  ┌──────────────────────────────────┐ │ │  │
│  │  │  │ JsVisualizationBackend (Phase 3) │ │ │  │
│  │  │  └──────────────────────────────────┘ │ │  │
│  │  │                                        │ │  │
│  │  │  ┌──────────────────────────────────┐ │ │  │
│  │  │  │ PythonVisualizationBackend       │ │ │  │
│  │  │  └──────────────────────────────────┘ │ │  │
│  │  │                                        │ │  │
│  │  │  ┌──────────────────────────────────┐ │ │  │
│  │  │  │ GenericVisualizationBackend      │ │ │  │
│  │  │  │  - Object Graph 생성              │ │ │  │
│  │  │  └──────────────────────────────────┘ │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  │                                              │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │ JCEFVisualizationPanel                 │ │  │
│  │  │  - JBCefBrowser                        │ │  │
│  │  │  - JavaScript Bridge                   │ │  │
│  │  │  - showVisualization(json)            │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │ JCEF WebView (React UI)                      │  │
│  │                                              │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │ window.visualizerAPI                   │ │  │
│  │  │  - updateVisualization(data)          │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  │                                              │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │ VisualizationRouter                    │ │  │
│  │  │  - 타입 판별 (data.kind)              │ │  │
│  │  │  - 적절한 렌더러 선택                 │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  │                                              │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │ Renderers                              │ │  │
│  │  │  - GraphRenderer (vis.js)             │ │  │
│  │  │  - TreeRenderer (SVG)                 │ │  │
│  │  │  - ArrayRenderer (D3.js)              │ │  │
│  │  │  - TableRenderer (Perspective.js)     │ │  │
│  │  │  - PlotlyRenderer                     │ │  │
│  │  │  - ... (13개 타입)                    │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

### 5.2 실시간 업데이트 흐름

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
autorun 트리거 (MobX 또는 Kotlin Flow)
        ↓
JCEFVisualizationPanel.showVisualization(json)
   ├─ JSON 이스케이프 처리
   └─ browser.executeJavaScript("window.visualizerAPI.updateVisualization('...')")
        ↓
[JCEF WebView - React]
        ↓
window.visualizerAPI.updateVisualization(jsonStr)
   ├─ JSON 파싱
   └─ setState({ visualizationData: data })
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

---

## 6. 비기능 요구사항

### 6.1 성능

| 메트릭 | 목표 | VSCode 기준 |
|--------|------|------------|
| 표현식 평가 시간 | < 500ms | < 500ms |
| 시각화 렌더링 시간 | < 100ms (1000개 요소) | < 100ms |
| F8 스텝 → UI 업데이트 | < 100ms | < 100ms |
| 메모리 사용량 | < 200MB | < 150MB |
| 최대 노드 수 (범용 모드) | 50개 | 50개 |

### 6.2 호환성

| 카테고리 | 요구사항 |
|---------|---------|
| IDE | IntelliJ IDEA 2023.2+, PyCharm, WebStorm, CLion |
| JDK | 17+ |
| JCEF | 지원하는 IDE 버전 |
| OS | Windows 10+, macOS 11+, Linux (Ubuntu 20.04+) |

### 6.3 다중 언어 지원

| 언어 | 지원 레벨 | 데이터 추출 방식 | 우선순위 |
|------|----------|-----------------|---------|
| Java | **Tier 1** | JDI 네이티브 | Phase 1 ✅ |
| Kotlin | **Tier 1** | JDI 네이티브 | Phase 1 ✅ |
| JavaScript | **Tier 1** | 런타임 코드 주입 | Phase 3 |
| TypeScript | **Tier 1** | 런타임 코드 주입 | Phase 3 |
| Python | **Tier 2** | 외부 모듈 | Phase 3 |
| C++ | **Tier 3** | 변수 참조 탐색 | Phase 4 |
| C# | **Tier 3** | 변수 참조 탐색 | Phase 4 |
| Go | **Tier 3** | 변수 참조 탐색 | Phase 4 |
| Rust | **Tier 3** | 변수 참조 탐색 | Phase 4 |

---

## 7. 개발 로드맵

### Phase 0: 기획 및 분석 (2주) ✅ **완료**
- [x] VSCode Debug Visualizer 코드 분석
- [x] 핵심 아키텍처 파악
- [x] PRD 재작성
- [x] 기술 스택 확정

### Phase 1: 기본 인프라 (4주) ✅ **완료**
- [x] 플러그인 프로젝트 초기화
- [x] JDI 기반 값 추출 (ExpressionEvaluator)
- [x] JCEF 웹뷰 통합
- [x] React UI 기본 구조
- [x] 배열 시각화 (D3.js)
- [x] TDD 환경 구축 (35개 테스트)

### Phase 2: 실시간 모니터링 (4주) ⏳ **진행 중**
- [ ] 디버거 이벤트 리스너 (XDebugSessionListener)
- [ ] 실시간 업데이트 파이프라인
- [ ] Observable 상태 관리
- [ ] 취소 토큰 및 디바운싱
- [ ] Graph, Tree, Table 렌더러
- [ ] 타입 자동 감지 시스템
- [ ] 우선순위 기반 추출기

**목표**: VSCode Debug Visualizer의 핵심 기능 재현 (Java/Kotlin)

### Phase 3: 다중 언어 지원 (6주)
- [ ] JavaScript/TypeScript 런타임 코드 주입
- [ ] Chrome DevTools Protocol 연동
- [ ] Data Extractor API 포팅
- [ ] Python debugpy 연동
- [ ] `pydebugvisualizer` 모듈 개발
- [ ] 범용 백엔드 (C++, C#, Go, Rust)

**목표**: VSCode와 동일한 언어 지원 수준

### Phase 4: 고급 기능 (4주)
- [ ] 사용자 정의 추출기 API
- [ ] 실행 히스토리 (Time-Travel)
- [ ] 내보내기 (PNG, SVG, GIF, HTML)
- [ ] 성능 최적화
- [ ] 문서 완성

**목표**: VSCode 기능 + α (JetBrains 전용 기능)

### Phase 5: 배포 (2주)
- [ ] 베타 테스트
- [ ] 버그 수정
- [ ] JetBrains Marketplace 등록
- [ ] 마케팅 및 홍보

---

## 8. 성공 지표

### 8.1 기술 지표

| 지표 | Phase 2 목표 | Phase 3 목표 |
|------|-------------|-------------|
| 지원 언어 | Java, Kotlin | +JS, TS, Python |
| 시각화 타입 | 5개 (graph, tree, array, table, text) | 13개 (전체) |
| 테스트 커버리지 | 80%+ | 90%+ |
| 성능 | F8 → UI < 200ms | F8 → UI < 100ms |

### 8.2 사용자 지표

| 지표 | 6개월 목표 | 1년 목표 |
|------|-----------|---------|
| 다운로드 수 | 5,000+ | 20,000+ |
| 활성 사용자 (MAU) | 500+ | 2,000+ |
| 사용자 평점 | 4.0+ / 5.0 | 4.5+ / 5.0 |
| GitHub Stars | 100+ | 500+ |

---

## 9. 위험 관리

| 위험 | 확률 | 영향 | 대응 전략 |
|------|------|------|-----------|
| JCEF API 변경 | 낮음 | 높음 | Chromium 버전 고정, 마이그레이션 가이드 모니터링 |
| XDebugger API 변경 | 중간 | 높음 | 안정 API만 사용, 여러 IDE 버전 테스트 |
| 성능 문제 (대용량 데이터) | 높음 | 중간 | 노드 수 제한, 가상 스크롤링, 페이지네이션 |
| 언어별 디버거 호환성 | 높음 | 중간 | Tier 시스템으로 점진적 지원 |
| VSCode와의 차별화 부족 | 중간 | 높음 | JetBrains 전용 기능 추가 (예: IntelliJ 코드 인사이트 통합) |

---

## 10. 차별화 전략

### 10.1 VSCode Debug Visualizer 대비 장점

1. **JDI 네이티브 접근**: Java/Kotlin에서 더 강력한 데이터 추출
2. **IntelliJ 통합**: 코드 인사이트, 리팩토링 힌트 연동 (Phase 4)
3. **JetBrains 생태계**: PyCharm, WebStorm, CLion 등 모든 IDE 지원
4. **기업 사용자**: JetBrains 사용자는 대부분 기업 환경

### 10.2 독자적인 기능 (Phase 4)

- **코드 하이라이트 연동**: 시각화에서 노드 클릭 → 에디터에서 해당 코드 하이라이트
- **리팩토링 제안**: 비효율적인 자료구조 감지 시 리팩토링 제안
- **성능 프로파일링**: 시각화에 CPU/메모리 사용량 오버레이
- **팀 협업**: 시각화 스냅샷 공유 (URL 생성)

---

## 11. 참고 자료

- [VSCode Debug Visualizer GitHub](https://github.com/hediet/vscode-debug-visualizer)
- [VSCode Debug Visualizer Marketplace](https://marketplace.visualstudio.com/items?itemName=hediet.debug-visualizer)
- [IntelliJ Platform SDK 문서](https://plugins.jetbrains.com/docs/intellij/)
- [JDI (Java Debug Interface) 문서](https://docs.oracle.com/javase/8/docs/jdk/api/jpda/jdi/)
- [JCEF 문서](https://plugins.jetbrains.com/docs/intellij/jcef.html)
- [Visualization Data Schema](https://hediet.github.io/visualization/docs/visualization-data-schema.json)

---

## 12. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 1.0.0 | 2024-11-10 | 초안 작성 (알고리즘 시각화 중심) | - |
| 2.0.0 | 2025-01-12 | 전면 재작성 (VSCode Debug Visualizer 기반, 실시간 모니터링 중심) | - |

---

**현재 상태**: Phase 1 완료, Phase 2 진행 중
**다음 마일스톤**: 실시간 모니터링 시스템 구축 (4주)
**최종 목표**: JetBrains 생태계의 VSCode Debug Visualizer
