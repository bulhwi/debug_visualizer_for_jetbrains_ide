# Architecture Design

## Overview

The Algorithm Debug Visualizer is built on the IntelliJ Platform and consists of three main components:

1. **Plugin Core**: Integrates with JetBrains IDE debugger
2. **Visualization Engine**: Renders data structures using web technologies
3. **Data Extractors**: Language-specific modules for extracting debug data

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     JetBrains IDE                           │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Plugin Core (Kotlin)                      │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐ │  │
│  │  │   Debugger   │  │  Expression  │  │    Tool     │ │  │
│  │  │  Integration │←→│   Evaluator  │←→│   Window    │ │  │
│  │  └──────────────┘  └──────────────┘  └─────────────┘ │  │
│  │         ↓                  ↓                 ↓        │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │         Data Extraction Layer                    │ │  │
│  │  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────┐ │ │  │
│  │  │  │  Java  │ │ Kotlin │ │ Python │ │ JS/TS    │ │ │  │
│  │  │  └────────┘ └────────┘ └────────┘ └──────────┘ │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  │         ↓                                              │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │            JSON Data Bridge                      │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  │         ↓                                              │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │       JCEF WebView Container                     │ │  │
│  │  │  ┌────────────────────────────────────────────┐ │ │  │
│  │  │  │    Visualization UI (TypeScript/React)     │ │ │  │
│  │  │  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────────┐ │ │ │  │
│  │  │  │  │ D3.js│ │Plotly│ │Cytosc│ │ Controls │ │ │ │  │
│  │  │  │  └──────┘ └──────┘ └──────┘ └──────────┘ │ │ │  │
│  │  │  └────────────────────────────────────────────┘ │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Component Details

### 1. Plugin Core

**Responsibilities:**
- Integrate with IntelliJ Platform debugger APIs
- Manage tool window lifecycle
- Handle user interactions (expression input, settings)
- Coordinate between debugger and visualization

**Key Classes:**
```kotlin
// Main plugin entry point
class AlgorithmVisualizerPlugin : DumbAware

// Tool window factory
class VisualizerToolWindowFactory : ToolWindowFactory

// Debugger integration
class DebuggerIntegration {
    fun evaluateExpression(expression: String): Any?
    fun getCurrentStackFrame(): XStackFrame?
    fun addBreakpointListener()
}

// Expression evaluator
class ExpressionEvaluator {
    fun evaluate(expression: String, context: XDebuggerEvaluator): XValue
}
```

**APIs Used:**
- `com.intellij.xdebugger.XDebuggerManager`
- `com.intellij.xdebugger.evaluation.XDebuggerEvaluator`
- `com.intellij.xdebugger.frame.XValue`
- `com.intellij.ui.jcef.JBCefBrowser`

### 2. Data Extraction Layer

**Purpose:** Convert language-specific data structures into a universal JSON format for visualization.

**Universal Visualization Data Format:**
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
    // Kind-specific data
  },
  "config": {
    "layout": "hierarchical" | "force-directed" | "grid",
    "animation": true,
    "theme": "light" | "dark"
  }
}
```

**Java/Kotlin Extractor:**
```kotlin
interface VisualizationExtractor {
    fun canExtract(value: XValue): Boolean
    fun extract(value: XValue): VisualizationData
}

class TreeNodeExtractor : VisualizationExtractor {
    override fun extract(value: XValue): VisualizationData {
        // Use JDI to inspect object fields
        // Build tree structure
        return TreeVisualizationData(nodes, edges)
    }
}
```

**Python Extractor:**
```python
# Injected into debug session
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

### 3. Visualization Engine

**Technology Stack:**
- **Framework**: React + TypeScript
- **Rendering**: D3.js (graphs/trees), Plotly.js (charts), Cytoscape.js (complex networks)
- **Communication**: CEF JavaScript Bridge

**Component Structure:**
```typescript
// Main visualizer controller
class VisualizerController {
    private renderers: Map<string, Renderer>;

    render(data: VisualizationData) {
        const renderer = this.getRenderer(data.kind);
        renderer.render(data.data, data.config);
    }
}

// Graph renderer using D3.js
class GraphRenderer implements Renderer {
    render(data: GraphData, config: Config) {
        // D3.js force-directed graph
    }
}

// Tree renderer
class TreeRenderer implements Renderer {
    render(data: TreeData, config: Config) {
        // D3.js hierarchical layout
    }
}
```

## Data Flow

### Expression Evaluation Flow

```
User Input → Plugin Core → Debugger API → Language Runtime
                ↓
         Data Extractor → Universal JSON Format
                ↓
         JCEF Bridge → WebView
                ↓
         Visualization Engine → Rendered Output
```

### Step-by-Step Process

1. **User enters expression** (e.g., `myArray`)
2. **Plugin evaluates expression** using `XDebuggerEvaluator`
3. **XValue is retrieved** from current stack frame
4. **Data extractor identifies type** and converts to JSON
5. **JSON sent to WebView** via JCEF JavaScript bridge
6. **React component receives data** and selects appropriate renderer
7. **Renderer visualizes data** using D3.js/Plotly/etc.

## Configuration & Extensibility

### Custom Extractors

Users can define custom extractors for their data structures:

```kotlin
// plugin.xml
<extensions defaultExtensionNs="com.example.algorithmvisualizer">
    <visualizationExtractor implementation="com.mycompany.MyCustomExtractor"/>
</extensions>

// Implementation
class MyCustomExtractor : VisualizationExtractor {
    override fun canExtract(value: XValue): Boolean {
        return value.type == "com.mycompany.MyDataStructure"
    }

    override fun extract(value: XValue): VisualizationData {
        // Custom extraction logic
    }
}
```

### Custom Visualizers

Users can add custom visualization types:

```typescript
// Register custom visualizer
VisualizerRegistry.register('myCustomViz', {
    canRender: (data) => data.kind === 'myCustom',
    render: (data, container) => {
        // Custom D3.js/Canvas rendering
    }
});
```

## Performance Considerations

### 1. Large Data Structures
- **Pagination**: Limit nodes/elements displayed (default 1000)
- **Virtual scrolling**: For array/table views
- **Level-based rendering**: For deep trees (show first N levels)

### 2. Real-time Updates
- **Debouncing**: Limit refresh rate during stepping
- **Incremental updates**: Only update changed nodes
- **WebWorker**: Heavy computation in background thread

### 3. Memory Management
- **Lazy loading**: Load visualization data on-demand
- **Cleanup**: Dispose of old visualizations when switching
- **Weak references**: Avoid memory leaks in debugger integration

## Security

### 1. Code Injection Prevention
- Sanitize all user input expressions
- Use safe evaluation APIs only
- No `eval()` in visualization code

### 2. Sandbox
- JCEF runs in isolated context
- No file system access from WebView
- CSP (Content Security Policy) headers

## Testing Strategy

### 1. Unit Tests
- Data extractors (mock XValue objects)
- Visualization components (mock data)
- Expression evaluation logic

### 2. Integration Tests
- Full plugin in test IDE instance
- Sample projects with known data structures
- Debugger state simulation

### 3. UI Tests
- Screenshot comparison
- Interaction testing (click, zoom, pan)
- Performance benchmarks

## Future Enhancements

1. **Animation playback**: Record and replay execution
2. **Diff view**: Compare data structures between breakpoints
3. **Export**: Save visualizations as PNG/SVG
4. **Collaboration**: Share visualizations with team
5. **AI assistance**: Suggest optimal visualization for data type
