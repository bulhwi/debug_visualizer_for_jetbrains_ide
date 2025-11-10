# 시각화 데이터 스키마

이 문서는 데이터 추출 레이어와 시각화 엔진 간에 흐르는 시각화 데이터의 JSON 스키마를 정의합니다.

## 기본 스키마

모든 시각화 데이터는 다음 기본 구조를 따라야 합니다:

```typescript
interface VisualizationData {
  kind: VisualizationKind;
  timestamp: number;
  metadata: Metadata;
  data: any; // kind별 데이터
  config?: Config;
}

interface Metadata {
  language: string;
  expression: string;
  type: string;
  lineNumber?: number;
  fileName?: string;
}

interface Config {
  layout?: 'hierarchical' | 'force-directed' | 'grid' | 'circular';
  animation?: boolean;
  theme?: 'light' | 'dark';
  highlightNodes?: number[] | string[];
  autoFit?: boolean;
}

type VisualizationKind =
  | 'graph'
  | 'tree'
  | 'array'
  | 'table'
  | 'plotly'
  | 'grid'
  | 'text';
```

## Kind별 스키마

### 1. 그래프 시각화

일반 그래프(방향/무방향, 가중치/비가중치)용입니다.

```typescript
interface GraphVisualizationData extends VisualizationData {
  kind: 'graph';
  data: {
    nodes: GraphNode[];
    edges: GraphEdge[];
    directed?: boolean;
  };
}

interface GraphNode {
  id: string | number;
  label: string;
  color?: string;
  size?: number;
  metadata?: Record<string, any>;
}

interface GraphEdge {
  source: string | number;
  target: string | number;
  weight?: number;
  label?: string;
  color?: string;
  directed?: boolean;
}
```

**예제:**
```json
{
  "kind": "graph",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "java",
    "expression": "graph",
    "type": "Graph<Integer>"
  },
  "data": {
    "nodes": [
      {"id": 1, "label": "A", "color": "#ff6b6b"},
      {"id": 2, "label": "B", "color": "#4ecdc4"},
      {"id": 3, "label": "C", "color": "#45b7d1"}
    ],
    "edges": [
      {"source": 1, "target": 2, "weight": 5},
      {"source": 2, "target": 3, "weight": 3}
    ],
    "directed": true
  },
  "config": {
    "layout": "force-directed",
    "highlightNodes": [1]
  }
}
```

### 2. 트리 시각화

계층적 트리 구조용입니다.

```typescript
interface TreeVisualizationData extends VisualizationData {
  kind: 'tree';
  data: TreeNode;
}

interface TreeNode {
  value: any;
  children?: TreeNode[];
  left?: TreeNode;  // 이진 트리용
  right?: TreeNode; // 이진 트리용
  color?: string;
  metadata?: {
    height?: number;
    balanceFactor?: number; // AVL 트리용
    color?: 'red' | 'black'; // Red-Black 트리용
    [key: string]: any;
  };
}
```

**예제 (이진 트리):**
```json
{
  "kind": "tree",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "python",
    "expression": "root",
    "type": "TreeNode"
  },
  "data": {
    "value": 10,
    "left": {
      "value": 5,
      "left": {"value": 3},
      "right": {"value": 7}
    },
    "right": {
      "value": 15,
      "left": {"value": 12},
      "right": {"value": 20}
    }
  },
  "config": {
    "layout": "hierarchical"
  }
}
```

### 3. 배열 시각화

1차원 배열 및 리스트용입니다.

```typescript
interface ArrayVisualizationData extends VisualizationData {
  kind: 'array';
  data: {
    values: any[];
    pointers?: Pointer[];
    ranges?: Range[];
  };
}

interface Pointer {
  index: number;
  label: string;
  color?: string;
}

interface Range {
  start: number;
  end: number;
  label?: string;
  color?: string;
}
```

**예제:**
```json
{
  "kind": "array",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "kotlin",
    "expression": "arr",
    "type": "IntArray"
  },
  "data": {
    "values": [3, 1, 4, 1, 5, 9, 2, 6],
    "pointers": [
      {"index": 0, "label": "left", "color": "#ff6b6b"},
      {"index": 7, "label": "right", "color": "#4ecdc4"}
    ],
    "ranges": [
      {"start": 2, "end": 5, "label": "window", "color": "#ffe66d"}
    ]
  }
}
```

### 4. 테이블 시각화

2D 배열, 행렬, DP 테이블용입니다.

```typescript
interface TableVisualizationData extends VisualizationData {
  kind: 'table';
  data: {
    rows: any[][];
    rowHeaders?: string[];
    columnHeaders?: string[];
    cellColors?: CellColor[][];
    highlightCells?: CellPosition[];
  };
}

interface CellColor {
  row: number;
  col: number;
  color: string;
}

interface CellPosition {
  row: number;
  col: number;
}
```

**예제 (DP 테이블):**
```json
{
  "kind": "table",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "java",
    "expression": "dp",
    "type": "int[][]"
  },
  "data": {
    "rows": [
      [0, 1, 1, 1, 1],
      [1, 1, 2, 3, 4],
      [1, 2, 3, 5, 8]
    ],
    "rowHeaders": ["", "a", "ab"],
    "columnHeaders": ["", "b", "ba", "bab", "baba"],
    "cellColors": [
      [2, 4, "#4ecdc4"]
    ],
    "highlightCells": [
      {"row": 2, "col": 4}
    ]
  }
}
```

### 5. Plotly 시각화

Plotly.js를 사용한 차트 및 플롯용입니다.

```typescript
interface PlotlyVisualizationData extends VisualizationData {
  kind: 'plotly';
  data: {
    data: Plotly.Data[];
    layout?: Partial<Plotly.Layout>;
  };
}
```

**예제 (선 차트):**
```json
{
  "kind": "plotly",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "python",
    "expression": "walk",
    "type": "list"
  },
  "data": {
    "data": [{
      "x": [0, 1, 2, 3, 4, 5],
      "y": [0, 1, 0, -1, 0, 1],
      "type": "scatter",
      "mode": "lines+markers",
      "name": "랜덤 워크"
    }],
    "layout": {
      "title": "랜덤 워크 시각화",
      "xaxis": {"title": "단계"},
      "yaxis": {"title": "위치"}
    }
  }
}
```

### 6. 그리드 시각화

2D 그리드(체스판, 미로 등)용입니다.

```typescript
interface GridVisualizationData extends VisualizationData {
  kind: 'grid';
  data: {
    grid: GridCell[][];
    width: number;
    height: number;
  };
}

interface GridCell {
  value: any;
  color?: string;
  symbol?: string; // 유니코드 심볼 또는 이모지
  metadata?: Record<string, any>;
}
```

**예제 (N-Queens):**
```json
{
  "kind": "grid",
  "timestamp": 1699876543210,
  "metadata": {
    "language": "cpp",
    "expression": "board",
    "type": "vector<vector<int>>"
  },
  "data": {
    "width": 8,
    "height": 8,
    "grid": [
      [
        {"value": 0, "color": "#ffffff"},
        {"value": 1, "color": "#ff6b6b", "symbol": "♕"}
      ]
    ]
  }
}
```

### 7. 텍스트 시각화

포맷된 텍스트 출력(HTML, JSON 등)용입니다.

```typescript
interface TextVisualizationData extends VisualizationData {
  kind: 'text';
  data: {
    content: string;
    format: 'plain' | 'html' | 'markdown' | 'json' | 'xml';
    highlights?: TextHighlight[];
  };
}

interface TextHighlight {
  start: number;
  end: number;
  color?: string;
  label?: string;
}
```

## 색상 스킴

### 사전 정의된 색상

```typescript
enum VisualizationColor {
  PRIMARY = '#4ecdc4',
  SECONDARY = '#ff6b6b',
  SUCCESS = '#51cf66',
  WARNING = '#ffd43b',
  ERROR = '#ff6b6b',
  INFO = '#339af0',
  VISITED = '#adb5bd',
  CURRENT = '#ff922b',
  PATH = '#51cf66'
}
```

### 테마 지원

```typescript
interface ThemeColors {
  light: {
    background: '#ffffff',
    foreground: '#000000',
    node: '#4ecdc4',
    edge: '#adb5bd',
    text: '#212529'
  };
  dark: {
    background: '#1a1a1a',
    foreground: '#ffffff',
    node: '#4ecdc4',
    edge: '#495057',
    text: '#f1f3f5'
  };
}
```

## 검증

모든 시각화 데이터는 렌더링 전에 스키마에 대해 검증되어야 합니다:

```typescript
function validateVisualizationData(data: any): boolean {
  if (!data.kind || !data.timestamp || !data.metadata || !data.data) {
    return false;
  }

  // kind별 검증
  switch (data.kind) {
    case 'graph':
      return validateGraphData(data.data);
    case 'tree':
      return validateTreeData(data.data);
    case 'array':
      return validateArrayData(data.data);
    // ... 기타 kind
    default:
      return false;
  }
}
```

## 확장

스키마를 확장하여 커스텀 시각화 kind를 추가할 수 있습니다:

```typescript
interface CustomVisualizationData extends VisualizationData {
  kind: 'custom:myViz';
  data: {
    // 커스텀 데이터 구조
  };
}
```

커스텀 렌더러 등록:

```typescript
VisualizerRegistry.register('custom:myViz', MyCustomRenderer);
```

## 알고리즘별 시각화 예제

### DFS/BFS 시각화

```json
{
  "kind": "graph",
  "metadata": {
    "language": "java",
    "expression": "graph",
    "type": "Graph"
  },
  "data": {
    "nodes": [
      {"id": 1, "label": "1", "color": "#51cf66"},  // 방문됨
      {"id": 2, "label": "2", "color": "#ff922b"},  // 현재
      {"id": 3, "label": "3", "color": "#ffffff"}   // 미방문
    ],
    "edges": [
      {"source": 1, "target": 2, "color": "#51cf66"}, // 방문된 엣지
      {"source": 2, "target": 3, "color": "#adb5bd"}  // 미방문 엣지
    ]
  },
  "config": {
    "highlightNodes": [2]
  }
}
```

### 정렬 알고리즘 시각화

```json
{
  "kind": "array",
  "metadata": {
    "language": "python",
    "expression": "arr",
    "type": "list"
  },
  "data": {
    "values": [5, 2, 8, 1, 9],
    "pointers": [
      {"index": 1, "label": "i", "color": "#ff6b6b"},
      {"index": 3, "label": "j", "color": "#4ecdc4"}
    ]
  }
}
```

### DP 테이블 시각화

```json
{
  "kind": "table",
  "metadata": {
    "language": "kotlin",
    "expression": "dp",
    "type": "Array<IntArray>"
  },
  "data": {
    "rows": [
      [0, 0, 0, 0],
      [0, 1, 1, 1],
      [0, 1, 2, 2]
    ],
    "rowHeaders": ["", "item1", "item2"],
    "columnHeaders": ["0", "1", "2", "3"],
    "highlightCells": [
      {"row": 2, "col": 3}
    ]
  }
}
```

### AVL 트리 회전 시각화

```json
{
  "kind": "tree",
  "metadata": {
    "language": "cpp",
    "expression": "root",
    "type": "AVLNode*"
  },
  "data": {
    "value": 10,
    "left": {
      "value": 5,
      "metadata": {"height": 2, "balanceFactor": 0}
    },
    "right": {
      "value": 15,
      "color": "#ff922b",
      "metadata": {"height": 1, "balanceFactor": -1}
    },
    "metadata": {"height": 3, "balanceFactor": 1}
  }
}
```

## 성능 최적화 가이드

### 대용량 데이터 처리

**페이지네이션:**
```json
{
  "kind": "array",
  "data": {
    "values": [...], // 전체 데이터
    "pagination": {
      "pageSize": 100,
      "currentPage": 0,
      "totalPages": 10
    }
  }
}
```

**레벨 제한 (트리):**
```json
{
  "kind": "tree",
  "data": {...},
  "config": {
    "maxDepth": 5,
    "collapsible": true
  }
}
```

## 애니메이션 지원

시각화는 애니메이션 단계를 포함할 수 있습니다:

```typescript
interface AnimatedVisualizationData extends VisualizationData {
  animation?: {
    steps: VisualizationData[];
    duration: number; // 밀리초
    loop?: boolean;
  };
}
```

**예제 (정렬 애니메이션):**
```json
{
  "kind": "array",
  "data": {...},
  "animation": {
    "steps": [
      {"kind": "array", "data": {"values": [5,2,8,1,9]}},
      {"kind": "array", "data": {"values": [2,5,8,1,9]}},
      {"kind": "array", "data": {"values": [2,5,1,8,9]}},
      {"kind": "array", "data": {"values": [1,2,5,8,9]}}
    ],
    "duration": 500
  }
}
```
