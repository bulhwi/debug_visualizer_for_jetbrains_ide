# Contributing to Algorithm Debug Visualizer

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to the project.

## Getting Started

### Prerequisites

- **JDK 17+**: Required for plugin development
- **IntelliJ IDEA 2023.1+**: Recommended IDE
- **Gradle 8.0+**: Build tool
- **Node.js 18+**: For visualization UI development
- **Git**: Version control

### Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/algorithm-debug-visualizer.git
   cd algorithm-debug-visualizer
   ```

2. **Build the plugin**
   ```bash
   cd plugin
   ./gradlew build
   ```

3. **Install UI dependencies**
   ```bash
   cd visualizer-ui
   npm install
   ```

4. **Run in development mode**
   ```bash
   cd plugin
   ./gradlew runIde
   ```

## Project Structure

```
algorithm-debug-visualizer/
├── plugin/                 # IntelliJ Platform plugin
│   ├── src/main/kotlin/   # Plugin source code
│   ├── src/main/resources/# Plugin resources
│   └── src/test/          # Plugin tests
├── visualizer-ui/         # Web-based UI
│   ├── src/components/    # React components
│   ├── src/visualizers/   # Visualization implementations
│   └── src/styles/        # CSS/SCSS styles
├── data-extraction/       # Language-specific extractors
└── docs/                  # Documentation

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with:
- Clear title and description
- Steps to reproduce
- Expected vs actual behavior
- Screenshots (if applicable)
- Environment details (OS, IDE version, plugin version)

### Suggesting Features

Feature requests are welcome! Please:
- Check if the feature already exists or is planned
- Describe the use case clearly
- Explain why this feature would be useful
- Provide examples if possible

### Code Contributions

#### 1. Fork and Branch

```bash
git checkout -b feature/my-new-feature
# or
git checkout -b fix/bug-description
```

#### 2. Code Style

**Kotlin:**
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation
- Maximum line length: 120 characters

**TypeScript:**
- Follow [TypeScript Style Guide](https://google.github.io/styleguide/tsguide.html)
- Use 2 spaces for indentation
- Use ESLint and Prettier

**Example Kotlin:**
```kotlin
class MyClass(
    private val property: String,
    private val anotherProperty: Int
) {
    fun myFunction(): String {
        return "Hello, $property"
    }
}
```

#### 3. Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(visualizer): add support for Red-Black tree visualization

- Implement color property for tree nodes
- Add rotation animation
- Update tree renderer to handle RB properties

Closes #123
```

#### 4. Testing

- Write unit tests for new features
- Ensure all tests pass before submitting PR
- Add integration tests for major features

**Run tests:**
```bash
cd plugin
./gradlew test

cd visualizer-ui
npm test
```

#### 5. Documentation

- Update README.md if needed
- Add JSDoc/KDoc comments for public APIs
- Update architecture.md for significant changes
- Add examples for new visualization types

#### 6. Pull Request

- Push your branch and create a PR
- Fill out the PR template completely
- Link related issues
- Request review from maintainers
- Address review feedback promptly

## Development Guidelines

### Adding a New Visualization Type

1. **Define the schema** in `docs/visualization-schema.md`
2. **Create the data extractor** in appropriate language module
3. **Implement the renderer** in `visualizer-ui/src/visualizers/`
4. **Register the visualizer** in the main controller
5. **Add tests** and examples
6. **Update documentation**

**Example:**

```kotlin
// 1. Data extractor
class HeapExtractor : VisualizationExtractor {
    override fun canExtract(value: XValue): Boolean {
        return value.type.contains("PriorityQueue") ||
               value.type.contains("Heap")
    }

    override fun extract(value: XValue): VisualizationData {
        // Extract heap structure
        return TreeVisualizationData(
            kind = "tree",
            data = buildHeapTree(value)
        )
    }
}
```

```typescript
// 2. Renderer
export class HeapRenderer implements Renderer {
  render(data: TreeData, container: HTMLElement) {
    // D3.js rendering logic for heap
    const svg = d3.select(container).append('svg');
    // ... visualization code
  }
}

// 3. Registration
VisualizerRegistry.register('heap', new HeapRenderer());
```

### Adding Language Support

1. **Create extractor module** in `data-extraction/<language>/`
2. **Implement data extraction** for common data structures
3. **Add debugger integration** in plugin core
4. **Write tests** with sample programs
5. **Update documentation**

### Performance Optimization

- Limit visualization to 1000 nodes by default
- Use virtual scrolling for large arrays
- Implement pagination for large graphs
- Debounce updates during rapid stepping
- Profile and optimize hot paths

### Accessibility

- Ensure keyboard navigation works
- Add ARIA labels to interactive elements
- Support high contrast themes
- Test with screen readers

## Release Process

1. Update version in `plugin.xml` and `package.json`
2. Update CHANGELOG.md
3. Create a release branch
4. Run full test suite
5. Build release artifacts
6. Create GitHub release with notes
7. Publish to JetBrains Marketplace

## Community

- Join discussions in GitHub Discussions
- Follow project updates
- Help answer questions from other users
- Share your use cases and examples

## License

By contributing, you agree that your contributions will be licensed under the project's license (TBD).

## Questions?

If you have questions:
- Check existing documentation
- Search closed issues
- Open a new discussion
- Contact maintainers

Thank you for contributing!
