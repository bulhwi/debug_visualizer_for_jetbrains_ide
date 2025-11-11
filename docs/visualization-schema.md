# 시각화 데이터 스키마 (v2.0.0 - VSCode 호환)

이 문서는 **VSCode Debug Visualizer**와 100% 호환되는 시각화 데이터 JSON 스키마를 정의합니다.

**참고**: [VSCode Visualization Data Schema (공식)](https://hediet.github.io/visualization/docs/visualization-data-schema.json)

## 기본 구조 (Discriminated Union)

VSCode Debug Visualizer는 `kind` 필드를 discriminated union 방식으로 사용합니다:

```typescript
interface VisualizationData {
  kind: {
    [visualizationType: string]: true;
  };
  // kind별 추가 필드
}
```

**예시**:
```json
{
  "kind": { "array": true },
  "items": [...]
}
```

## 13개 시각화 타입

### 1. Graph (그래프 구조)

일반 그래프(방향/무방향, 가중치/비가중치)용입니다.

```typescript
interface GraphVisualizationData {
  kind: { graph: true };
  nodes: Array<{
    id: string;
    label?: string;
    color?: string;
    shape?: "box" | "circle" | "ellipse" | "diamond";
  }>;
  edges: Array<{
    from: string;
    to: string;
    label?: string;
    color?: string;
    dashes?: boolean;
    arrows?: "to" | "from" | "both";
  }>;
}
```

**예시 (DFS 그래프)**:
```json
{
  "kind": { "graph": true },
  "nodes": [
    { "id": "A", "label": "Node A", "color": "#4ecdc4" },
    { "id": "B", "label": "Node B", "color": "#ff6b6b" },
    { "id": "C", "label": "Node C", "color": "#4ecdc4" }
  ],
  "edges": [
    { "from": "A", "to": "B", "label": "edge 1" },
    { "from": "A", "to": "C", "label": "edge 2" }
  ]
}
```

### 2. Tree (트리 구조)

이진 트리, AVL, Red-Black 트리 등에 사용됩니다.

```typescript
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
```

**예시 (이진 트리)**:
```json
{
  "kind": { "tree": true },
  "root": {
    "id": "1",
    "label": "10",
    "children": [
      {
        "id": "2",
        "label": "5",
        "children": []
      },
      {
        "id": "3",
        "label": "15",
        "children": []
      }
    ]
  }
}
```

### 3. Array (배열/리스트)

배열, 리스트, 정렬 알고리즘 등에 사용됩니다.

```typescript
interface ArrayVisualizationData {
  kind: { array: true };
  items: Array<{
    value: number | string;
    label?: string;
    color?: string;
    isHighlighted?: boolean;
  }>;
}
```

**예시 (정수 배열)**:
```json
{
  "kind": { "array": true },
  "items": [
    { "value": 5, "color": "#4ecdc4" },
    { "value": 1, "color": "#ff6b6b", "isHighlighted": true },
    { "value": 9, "color": "#4ecdc4" }
  ]
}
```

### 4. Table (2D 테이블)

2D 배열, DP 테이블, 행렬 등에 사용됩니다.

```typescript
interface TableVisualizationData {
  kind: { table: true };
  rows: Array<{ [key: string]: any }>;
  columns?: Array<{
    key: string;
    label: string;
  }>;
}
```

**예시 (DP 테이블)**:
```json
{
  "kind": { "table": true },
  "columns": [
    { "key": "i", "label": "Index" },
    { "key": "value", "label": "Value" }
  ],
  "rows": [
    { "i": 0, "value": 0 },
    { "i": 1, "value": 1 },
    { "i": 2, "value": 1 }
  ]
}
```

### 5. Plotly (차트)

히스토그램, 선 그래프, 산점도 등 Plotly.js 기반 차트에 사용됩니다.

```typescript
interface PlotlyVisualizationData {
  kind: { plotly: true };
  data: any[];  // Plotly.js data
  layout?: any;  // Plotly.js layout
}
```

**예시 (히스토그램)**:
```json
{
  "kind": { "plotly": true },
  "data": [
    {
      "x": [1, 2, 3, 4, 5],
      "y": [10, 15, 13, 17, 20],
      "type": "bar"
    }
  ],
  "layout": {
    "title": "Sample Histogram"
  }
}
```

### 6. Grid (2D 그리드)

체스판, 미로, 게임 맵 등에 사용됩니다.

```typescript
interface GridVisualizationData {
  kind: { grid: true };
  rows: number;
  cols: number;
  cells: Array<{
    row: number;
    col: number;
    value?: any;
    color?: string;
    label?: string;
  }>;
}
```

**예시 (미로)**:
```json
{
  "kind": { "grid": true },
  "rows": 5,
  "cols": 5,
  "cells": [
    { "row": 0, "col": 0, "color": "#000000", "label": "Wall" },
    { "row": 0, "col": 1, "color": "#ffffff", "label": "Path" },
    { "row": 0, "col": 2, "color": "#ffffff" }
  ]
}
```

### 7. Text (포맷된 텍스트)

단순 텍스트 출력, JSON 데이터 등에 사용됩니다.

```typescript
interface TextVisualizationData {
  kind: { text: true };
  text: string;
  mimeType?: string;  // "text/plain" | "text/html" | "text/markdown"
}
```

**예시**:
```json
{
  "kind": { "text": true },
  "text": "Hello World",
  "mimeType": "text/plain"
}
```

### 8. MonacoText (코드 하이라이팅)

소스 코드, JSON, XML 등 구문 강조가 필요한 텍스트에 사용됩니다.

```typescript
interface MonacoTextVisualizationData {
  kind: { monacoText: true };
  text: string;
  language?: string;  // "javascript" | "python" | "java" | "json" | ...
}
```

**예시 (JavaScript 코드)**:
```json
{
  "kind": { "monacoText": true },
  "text": "function hello() {\n  console.log('Hello World');\n}",
  "language": "javascript"
}
```

### 9. Image (이미지)

Base64 인코딩 이미지 또는 URL 이미지에 사용됩니다.

```typescript
interface ImageVisualizationData {
  kind: { image: true };
  src: string;  // data:image/png;base64,... 또는 URL
  alt?: string;
  width?: number;
  height?: number;
}
```

**예시**:
```json
{
  "kind": { "image": true },
  "src": "data:image/png;base64,iVBORw0KG...",
  "alt": "Algorithm Diagram",
  "width": 800,
  "height": 600
}
```

### 10. SVG (SVG 직접 렌더링)

SVG 마크업을 직접 렌더링합니다.

```typescript
interface SvgVisualizationData {
  kind: { svg: true };
  svgContent: string;  // SVG XML 문자열
}
```

**예시**:
```json
{
  "kind": { "svg": true },
  "svgContent": "<svg><circle cx='50' cy='50' r='40' fill='red' /></svg>"
}
```

### 11. Graphviz-Dot (DOT 언어 그래프)

Graphviz DOT 언어로 정의된 그래프에 사용됩니다.

```typescript
interface GraphvizDotVisualizationData {
  kind: { "graphviz-dot": true };
  dotSrc: string;  // DOT 언어 문자열
}
```

**예시**:
```json
{
  "kind": { "graphviz-dot": true },
  "dotSrc": "digraph G { A -> B; B -> C; }"
}
```

### 12. AST (추상 구문 트리)

프로그래밍 언어 AST 시각화에 사용됩니다.

```typescript
interface AstVisualizationData {
  kind: { ast: true };
  root: AstNode;
}

interface AstNode {
  id: string;
  label: string;
  kind: string;  // "FunctionDeclaration" | "BinaryExpression" | ...
  children?: AstNode[];
  properties?: { [key: string]: any };
}
```

**예시 (JavaScript AST)**:
```json
{
  "kind": { "ast": true },
  "root": {
    "id": "1",
    "label": "Program",
    "kind": "Program",
    "children": [
      {
        "id": "2",
        "label": "FunctionDeclaration",
        "kind": "FunctionDeclaration",
        "properties": { "name": "hello" },
        "children": []
      }
    ]
  }
}
```

### 13. Object-Graph (객체 참조 그래프)

모든 언어의 fallback으로 사용되며, 객체 간 참조 관계를 시각화합니다.

```typescript
interface ObjectGraphVisualizationData {
  kind: { "object-graph": true };
  objects: Array<{
    id: string;
    label: string;
    properties?: Array<{
      key: string;
      value: string;
    }>;
  }>;
  references: Array<{
    from: string;
    to: string;
    label?: string;
  }>;
}
```

**예시**:
```json
{
  "kind": { "object-graph": true },
  "objects": [
    {
      "id": "obj1",
      "label": "MyObject",
      "properties": [
        { "key": "name", "value": "John" },
        { "key": "age", "value": "30" }
      ]
    },
    {
      "id": "obj2",
      "label": "AnotherObject",
      "properties": []
    }
  ],
  "references": [
    { "from": "obj1", "to": "obj2", "label": "parent" }
  ]
}
```

## Kotlin 데이터 클래스 (플러그인 구현용)

```kotlin
// 기본 인터페이스
sealed class VisualizationData {
    abstract val kind: Map<String, Boolean>
}

// 1. Graph
data class GraphVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("graph" to true),
    val nodes: List<GraphNode>,
    val edges: List<GraphEdge>
) : VisualizationData()

data class GraphNode(
    val id: String,
    val label: String? = null,
    val color: String? = null,
    val shape: String? = null
)

data class GraphEdge(
    val from: String,
    val to: String,
    val label: String? = null,
    val color: String? = null,
    val dashes: Boolean? = null
)

// 2. Tree
data class TreeVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("tree" to true),
    val root: TreeNode
) : VisualizationData()

data class TreeNode(
    val id: String,
    val label: String,
    val children: List<TreeNode>? = null,
    val color: String? = null,
    val isMarked: Boolean? = null
)

// 3. Array
data class ArrayVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("array" to true),
    val items: List<ArrayItem>
) : VisualizationData()

data class ArrayItem(
    val value: Any,
    val label: String? = null,
    val color: String? = null,
    val isHighlighted: Boolean? = null
)

// 4. Table
data class TableVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("table" to true),
    val rows: List<Map<String, Any>>,
    val columns: List<TableColumn>? = null
) : VisualizationData()

data class TableColumn(
    val key: String,
    val label: String
)

// 5. Plotly
data class PlotlyVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("plotly" to true),
    val data: List<Any>,
    val layout: Map<String, Any>? = null
) : VisualizationData()

// 6. Grid
data class GridVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("grid" to true),
    val rows: Int,
    val cols: Int,
    val cells: List<GridCell>
) : VisualizationData()

data class GridCell(
    val row: Int,
    val col: Int,
    val value: Any? = null,
    val color: String? = null,
    val label: String? = null
)

// 7. Text
data class TextVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("text" to true),
    val text: String,
    val mimeType: String? = "text/plain"
) : VisualizationData()

// 8. MonacoText
data class MonacoTextVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("monacoText" to true),
    val text: String,
    val language: String? = null
) : VisualizationData()

// 9. Image
data class ImageVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("image" to true),
    val src: String,
    val alt: String? = null,
    val width: Int? = null,
    val height: Int? = null
) : VisualizationData()

// 10. SVG
data class SvgVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("svg" to true),
    val svgContent: String
) : VisualizationData()

// 11. Graphviz-Dot
data class GraphvizDotVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("graphviz-dot" to true),
    val dotSrc: String
) : VisualizationData()

// 12. AST
data class AstVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("ast" to true),
    val root: AstNode
) : VisualizationData()

data class AstNode(
    val id: String,
    val label: String,
    val kind: String,
    val children: List<AstNode>? = null,
    val properties: Map<String, Any>? = null
)

// 13. Object-Graph
data class ObjectGraphVisualizationData(
    override val kind: Map<String, Boolean> = mapOf("object-graph" to true),
    val objects: List<ObjectGraphObject>,
    val references: List<ObjectGraphReference>
) : VisualizationData()

data class ObjectGraphObject(
    val id: String,
    val label: String,
    val properties: List<ObjectProperty>? = null
)

data class ObjectProperty(
    val key: String,
    val value: String
)

data class ObjectGraphReference(
    val from: String,
    val to: String,
    val label: String? = null
)
```

## TypeScript 타입 정의 (React UI용)

```typescript
// visualizer-ui/src/types/visualization.ts
export type VisualizationData =
  | GraphVisualizationData
  | TreeVisualizationData
  | ArrayVisualizationData
  | TableVisualizationData
  | PlotlyVisualizationData
  | GridVisualizationData
  | TextVisualizationData
  | MonacoTextVisualizationData
  | ImageVisualizationData
  | SvgVisualizationData
  | GraphvizDotVisualizationData
  | AstVisualizationData
  | ObjectGraphVisualizationData

export interface GraphVisualizationData {
  kind: { graph: true }
  nodes: GraphNode[]
  edges: GraphEdge[]
}

export interface GraphNode {
  id: string
  label?: string
  color?: string
  shape?: 'box' | 'circle' | 'ellipse' | 'diamond'
}

export interface GraphEdge {
  from: string
  to: string
  label?: string
  color?: string
  dashes?: boolean
  arrows?: 'to' | 'from' | 'both'
}

// ... (나머지 13개 타입 정의)
```

## 사용 예시

### DataExtractor 구현

```kotlin
class ArrayExtractor : DataExtractor {
    override val id = "array"
    override val priority = 500

    override fun canExtract(value: Any): Boolean {
        return value is IntArray || value is Array<*> || value is List<*>
    }

    override fun extract(value: Any): VisualizationData {
        val items = when (value) {
            is IntArray -> value.map { ArrayItem(it) }
            is Array<*> -> value.map { ArrayItem(it ?: "null") }
            is List<*> -> value.map { ArrayItem(it ?: "null") }
            else -> emptyList()
        }

        return ArrayVisualizationData(items = items)
    }
}
```

### React 렌더러 구현

```typescript
export function ArrayRenderer({ data }: { data: ArrayVisualizationData }) {
  const svgRef = useRef<SVGSVGElement>(null)

  useEffect(() => {
    if (!svgRef.current) return

    const svg = d3.select(svgRef.current)
    // D3.js 막대 그래프 렌더링
    // ...
  }, [data])

  return <svg ref={svgRef}></svg>
}
```

---

**마지막 업데이트**: 2025-01-12
**문서 버전**: 2.0.0 (VSCode 100% 호환)
**참고**: [VSCode Visualization Data Schema](https://hediet.github.io/visualization/docs/visualization-data-schema.json)
