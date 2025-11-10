# Visualization Data Schema

This document defines the JSON schema for visualization data that flows between the data extraction layer and the visualization engine.

## Base Schema

All visualization data must conform to this base structure:

```typescript
interface VisualizationData {
  kind: VisualizationKind;
  timestamp: number;
  metadata: Metadata;
  data: any; // Kind-specific data
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

## Kind-Specific Schemas

### 1. Graph Visualization

For general graphs (directed/undirected, weighted/unweighted).

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

**Example:**
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

### 2. Tree Visualization

For hierarchical tree structures.

```typescript
interface TreeVisualizationData extends VisualizationData {
  kind: 'tree';
  data: TreeNode;
}

interface TreeNode {
  value: any;
  children?: TreeNode[];
  left?: TreeNode;  // For binary trees
  right?: TreeNode; // For binary trees
  color?: string;
  metadata?: {
    height?: number;
    balanceFactor?: number; // For AVL trees
    color?: 'red' | 'black'; // For Red-Black trees
    [key: string]: any;
  };
}
```

**Example (Binary Tree):**
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

### 3. Array Visualization

For one-dimensional arrays and lists.

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

**Example:**
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

### 4. Table Visualization

For 2D arrays, matrices, DP tables.

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

**Example (DP Table):**
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

### 5. Plotly Visualization

For charts and plots using Plotly.js.

```typescript
interface PlotlyVisualizationData extends VisualizationData {
  kind: 'plotly';
  data: {
    data: Plotly.Data[];
    layout?: Partial<Plotly.Layout>;
  };
}
```

**Example (Line Chart):**
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
      "name": "Random Walk"
    }],
    "layout": {
      "title": "Random Walk Visualization",
      "xaxis": {"title": "Step"},
      "yaxis": {"title": "Position"}
    }
  }
}
```

### 6. Grid Visualization

For 2D grids (chess board, maze, etc.).

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
  symbol?: string; // Unicode symbol or emoji
  metadata?: Record<string, any>;
}
```

**Example (N-Queens):**
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

### 7. Text Visualization

For formatted text output (HTML, JSON, etc.).

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

## Color Schemes

### Predefined Colors

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

### Theme Support

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

## Validation

All visualization data should be validated against the schema before rendering:

```typescript
function validateVisualizationData(data: any): boolean {
  if (!data.kind || !data.timestamp || !data.metadata || !data.data) {
    return false;
  }

  // Kind-specific validation
  switch (data.kind) {
    case 'graph':
      return validateGraphData(data.data);
    case 'tree':
      return validateTreeData(data.data);
    case 'array':
      return validateArrayData(data.data);
    // ... other kinds
    default:
      return false;
  }
}
```

## Extensions

Custom visualization kinds can be added by extending the schema:

```typescript
interface CustomVisualizationData extends VisualizationData {
  kind: 'custom:myViz';
  data: {
    // Custom data structure
  };
}
```

Register the custom renderer:

```typescript
VisualizerRegistry.register('custom:myViz', MyCustomRenderer);
```
