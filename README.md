# Algorithm Debug Visualizer for JetBrains IDEs

A powerful debugging visualization plugin for JetBrains IDEs (IntelliJ IDEA, PyCharm, WebStorm, CLion, etc.) that helps developers understand algorithm execution through rich visual representations.

## Motivation

Inspired by [VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer), this plugin brings comprehensive algorithm visualization capabilities to the JetBrains ecosystem with enhanced support for multiple programming languages and algorithm types.

## Features

### Supported Visualizations

- **Graph Structures**: Visualize graphs with nodes and edges (directed/undirected, weighted)
- **Tree Structures**: Binary trees, BST, AVL, Red-Black trees, Tries, Segment trees
- **Arrays & Lists**: Bar charts, grid views, heatmaps with pointer/window highlighting
- **Tables**: DP tables, adjacency matrices, distance tables
- **Charts**: Line graphs, histograms, pie charts (Plotly-based)
- **Custom Visualizations**: Define your own visualization logic

### Algorithm Types Coverage

1. **Array/List Algorithms**
   - Sorting (Bubble, Quick, Merge, Heap sort)
   - Two pointers, Sliding window
   - Subarray problems

2. **Tree Algorithms**
   - Traversals (Preorder, Inorder, Postorder, Level-order)
   - BST operations
   - Balanced tree rotations
   - Tree modifications

3. **Graph Algorithms**
   - DFS/BFS with step-by-step traversal
   - Shortest path (Dijkstra, Bellman-Ford, Floyd-Warshall)
   - MST (Kruskal, Prim)
   - Topological sort
   - Network flow

4. **Dynamic Programming**
   - DP table visualization with fill order
   - Backtracking path highlighting
   - Memoization state tracking

5. **Backtracking**
   - Decision tree visualization
   - Board/grid states (N-Queens, Sudoku)
   - Pruning visualization

6. **String Algorithms**
   - Pattern matching (KMP, Rabin-Karp)
   - Character-level highlighting
   - Failure function tables

7. **Heap/Priority Queue**
   - Heap tree structure
   - Parent-child relationships
   - Heap property validation

8. **Advanced Data Structures**
   - Union-Find (Disjoint Set) with set grouping
   - Fenwick Tree (BIT)
   - LRU Cache state
   - Stack/Queue operations

### Language Support

#### Tier 1 (Full Support - Automatic Data Extraction)
- Java
- Kotlin
- Python
- JavaScript/TypeScript

#### Tier 2 (Basic Support - JSON Serialization)
- C++
- Go
- Rust
- Scala

#### Tier 3 (Limited Support)
- C#
- PHP
- Ruby

## Architecture

```
algorithm-debug-visualizer/
├── plugin/                 # IntelliJ Platform plugin code
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/    # Plugin logic
│   │   │   └── resources/ # Plugin configuration
│   │   └── test/
│   └── build.gradle.kts
├── visualizer-ui/         # Web-based visualization UI
│   ├── src/
│   │   ├── components/    # React/Vue components
│   │   ├── visualizers/   # Visualization implementations
│   │   └── styles/
│   └── package.json
├── data-extraction/       # Language-specific data extractors
│   ├── java/
│   ├── kotlin/
│   ├── python/
│   └── javascript/
└── docs/                  # Documentation
    ├── architecture.md
    ├── api-reference.md
    └── developer-guide.md
```

## Technology Stack

- **Plugin Framework**: IntelliJ Platform SDK
- **UI Rendering**: JCEF (Java Chromium Embedded Framework)
- **Visualization Libraries**: D3.js, Plotly.js, Cytoscape.js
- **Debugger Integration**: IntelliJ Platform Debugger API
- **Language Support**:
  - JVM: JDI (Java Debug Interface)
  - Python: debugpy protocol
  - JavaScript: Chrome DevTools Protocol

## Getting Started

### Prerequisites

- JetBrains IDE (IntelliJ IDEA 2023.1+, PyCharm, WebStorm, etc.)
- JDK 17+
- Gradle 8.0+

### Installation

Coming soon...

### Usage

Coming soon...

## Development Roadmap

- [ ] Phase 1: Core plugin infrastructure
  - [ ] Plugin skeleton with IntelliJ Platform SDK
  - [ ] Debugger API integration
  - [ ] Basic expression evaluation

- [ ] Phase 2: Visualization engine
  - [ ] JCEF webview setup
  - [ ] D3.js integration
  - [ ] Basic graph/tree rendering

- [ ] Phase 3: Language support
  - [ ] Java/Kotlin data extraction
  - [ ] Python data extraction
  - [ ] JavaScript/TypeScript data extraction

- [ ] Phase 4: Algorithm-specific visualizers
  - [ ] Array/sorting visualizations
  - [ ] Tree visualizations
  - [ ] Graph algorithm visualizations
  - [ ] DP table visualizations

- [ ] Phase 5: Advanced features
  - [ ] Custom visualization API
  - [ ] Animation controls
  - [ ] Export capabilities
  - [ ] Theme support

## Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details.

## License

TBD

## Acknowledgments

- Inspired by [VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer)
- Built with [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
