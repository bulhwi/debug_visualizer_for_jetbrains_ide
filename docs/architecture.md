# 아키텍처 설계

## 개요

알고리즘 디버그 시각화 도구는 IntelliJ Platform을 기반으로 구축되며 세 가지 주요 컴포넌트로 구성됩니다:

1. **플러그인 코어**: JetBrains IDE 디버거와 통합
2. **시각화 엔진**: 웹 기술을 사용하여 데이터 구조 렌더링
3. **데이터 추출기**: 디버그 데이터 추출을 위한 언어별 모듈

## 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                     JetBrains IDE                           │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              플러그인 코어 (Kotlin)                     │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐ │  │
│  │  │   디버거     │  │  표현식      │  │    도구     │ │  │
│  │  │   통합       │←→│  평가기      │←→│   윈도우    │ │  │
│  │  └──────────────┘  └──────────────┘  └─────────────┘ │  │
│  │         ↓                  ↓                 ↓        │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │         데이터 추출 레이어                        │ │  │
│  │  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────┐ │ │  │
│  │  │  │  Java  │ │ Kotlin │ │ Python │ │ JS/TS    │ │ │  │
│  │  │  └────────┘ └────────┘ └────────┘ └──────────┘ │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  │         ↓                                              │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │            JSON 데이터 브리지                     │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  │         ↓                                              │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │       JCEF WebView 컨테이너                      │ │  │
│  │  │  ┌────────────────────────────────────────────┐ │ │  │
│  │  │  │    시각화 UI (TypeScript/React)           │ │ │  │
│  │  │  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────────┐ │ │ │  │
│  │  │  │  │ D3.js│ │Plotly│ │Cytosc│ │ Controls │ │ │ │  │
│  │  │  │  └──────┘ └──────┘ └──────┘ └──────────┘ │ │ │  │
│  │  │  └────────────────────────────────────────────┘ │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 컴포넌트 상세

### 1. 플러그인 코어

**책임:**
- IntelliJ Platform 디버거 API와 통합
- 도구 윈도우 생명주기 관리
- 사용자 인터랙션 처리 (표현식 입력, 설정)
- 디버거와 시각화 간 조정

**주요 클래스:**
```kotlin
// 메인 플러그인 진입점
class AlgorithmVisualizerPlugin : DumbAware

// 도구 윈도우 팩토리
class VisualizerToolWindowFactory : ToolWindowFactory

// 디버거 통합
class DebuggerIntegration {
    fun evaluateExpression(expression: String): Any?
    fun getCurrentStackFrame(): XStackFrame?
    fun addBreakpointListener()
}

// 표현식 평가기
class ExpressionEvaluator {
    fun evaluate(expression: String, context: XDebuggerEvaluator): XValue
}
```

**사용된 API:**
- `com.intellij.xdebugger.XDebuggerManager`
- `com.intellij.xdebugger.evaluation.XDebuggerEvaluator`
- `com.intellij.xdebugger.frame.XValue`
- `com.intellij.ui.jcef.JBCefBrowser`

### 2. 데이터 추출 레이어

**목적:** 언어별 데이터 구조를 시각화를 위한 범용 JSON 형식으로 변환합니다.

**범용 시각화 데이터 형식:**
```json
{
  "kind": "graph" | "tree" | "array" | "table" | "plotly" | "grid",
  "timestamp": 1234567890,
  "metadata": {
    "language": "java",
    "expression": "myTree",
    "type": "TreeNode"
  },
  "data": {
    // kind별 데이터
  },
  "config": {
    "layout": "hierarchical" | "force-directed" | "grid",
    "animation": true,
    "theme": "light" | "dark"
  }
}
```

**Java/Kotlin 추출기:**
```kotlin
interface VisualizationExtractor {
    fun canExtract(value: XValue): Boolean
    fun extract(value: XValue): VisualizationData
}

class TreeNodeExtractor : VisualizationExtractor {
    override fun extract(value: XValue): VisualizationData {
        // JDI를 사용하여 객체 필드 검사
        // 트리 구조 구축
        return TreeVisualizationData(nodes, edges)
    }
}
```

**Python 추출기:**
```python
# 디버그 세션에 주입됨
class DebugVisualizerHelper:
    @staticmethod
    def extract_tree(obj):
        if hasattr(obj, '__dict__'):
            return {
                'kind': 'tree',
                'data': {
                    'value': str(obj.val),
                    'left': extract_tree(obj.left) if hasattr(obj, 'left') else None,
                    'right': extract_tree(obj.right) if hasattr(obj, 'right') else None
                }
            }
```

### 3. 시각화 엔진

**기술 스택:**
- **프레임워크**: React + TypeScript
- **렌더링**: D3.js (그래프/트리), Plotly.js (차트), Cytoscape.js (복잡한 네트워크)
- **통신**: CEF JavaScript Bridge

**컴포넌트 구조:**
```typescript
// 메인 시각화 컨트롤러
class VisualizerController {
    private renderers: Map<string, Renderer>;

    render(data: VisualizationData) {
        const renderer = this.getRenderer(data.kind);
        renderer.render(data.data, data.config);
    }
}

// D3.js를 사용한 그래프 렌더러
class GraphRenderer implements Renderer {
    render(data: GraphData, config: Config) {
        // D3.js force-directed 그래프
    }
}

// 트리 렌더러
class TreeRenderer implements Renderer {
    render(data: TreeData, config: Config) {
        // D3.js 계층적 레이아웃
    }
}
```

## 데이터 흐름

### 표현식 평가 흐름

```
사용자 입력 → 플러그인 코어 → 디버거 API → 언어 런타임
                ↓
         데이터 추출기 → 범용 JSON 형식
                ↓
         JCEF 브리지 → WebView
                ↓
         시각화 엔진 → 렌더링된 출력
```

### 단계별 프로세스

1. **사용자가 표현식 입력** (예: `myArray`)
2. **플러그인이 표현식 평가** `XDebuggerEvaluator` 사용
3. **현재 스택 프레임에서 XValue 검색**
4. **데이터 추출기가 타입 식별** 및 JSON으로 변환
5. **JCEF JavaScript 브리지를 통해 JSON을 WebView로 전송**
6. **React 컴포넌트가 데이터 수신** 및 적절한 렌더러 선택
7. **렌더러가 D3.js/Plotly 등을 사용하여 데이터 시각화**

## 설정 및 확장성

### 커스텀 추출기

사용자는 자신의 데이터 구조에 대한 커스텀 추출기를 정의할 수 있습니다:

```kotlin
// plugin.xml
<extensions defaultExtensionNs="com.example.algorithmvisualizer">
    <visualizationExtractor implementation="com.mycompany.MyCustomExtractor"/>
</extensions>

// 구현
class MyCustomExtractor : VisualizationExtractor {
    override fun canExtract(value: XValue): Boolean {
        return value.type == "com.mycompany.MyDataStructure"
    }

    override fun extract(value: XValue): VisualizationData {
        // 커스텀 추출 로직
    }
}
```

### 커스텀 시각화 도구

사용자는 커스텀 시각화 타입을 추가할 수 있습니다:

```typescript
// 커스텀 시각화 도구 등록
VisualizerRegistry.register('myCustomViz', {
    canRender: (data) => data.kind === 'myCustom',
    render: (data, container) => {
        // 커스텀 D3.js/Canvas 렌더링
    }
});
```

## 성능 고려사항

### 1. 대용량 데이터 구조
- **페이지네이션**: 표시되는 노드/요소 제한 (기본 1000개)
- **가상 스크롤링**: 배열/테이블 뷰용
- **레벨 기반 렌더링**: 깊은 트리용 (처음 N 레벨만 표시)

### 2. 실시간 업데이트
- **디바운싱**: 스테핑 중 새로고침 빈도 제한
- **증분 업데이트**: 변경된 노드만 업데이트
- **WebWorker**: 백그라운드 스레드에서 무거운 계산

### 3. 메모리 관리
- **지연 로딩**: 필요 시 시각화 데이터 로드
- **정리**: 전환 시 이전 시각화 폐기
- **약한 참조**: 디버거 통합에서 메모리 누수 방지

## 보안

### 1. 코드 인젝션 방지
- 모든 사용자 입력 표현식 새니타이제이션
- 안전한 평가 API만 사용
- 시각화 코드에 `eval()` 사용 금지

### 2. 샌드박스
- JCEF는 격리된 컨텍스트에서 실행
- WebView에서 파일 시스템 접근 불가
- CSP (Content Security Policy) 헤더

## 테스트 전략

### 1. 단위 테스트
- 데이터 추출기 (mock XValue 객체)
- 시각화 컴포넌트 (mock 데이터)
- 표현식 평가 로직

### 2. 통합 테스트
- 테스트 IDE 인스턴스에서 전체 플러그인
- 알려진 데이터 구조가 있는 샘플 프로젝트
- 디버거 상태 시뮬레이션

### 3. UI 테스트
- 스크린샷 비교
- 인터랙션 테스트 (클릭, 줌, 팬)
- 성능 벤치마크

## 향후 개선사항

1. **애니메이션 재생**: 실행 기록 및 재생
2. **차이점 뷰**: 중단점 간 데이터 구조 비교
3. **내보내기**: 시각화를 PNG/SVG로 저장
4. **협업**: 팀과 시각화 공유
5. **AI 지원**: 데이터 타입에 최적의 시각화 제안
