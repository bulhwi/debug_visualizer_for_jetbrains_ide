# Claude Code를 위한 프로젝트 가이드

이 문서는 Claude Code가 이 프로젝트에서 작업할 때 참조해야 할 핵심 정보를 담고 있습니다.

## 프로젝트 개요

**Debug Visualizer for JetBrains IDEs**는 JetBrains IDE(IntelliJ IDEA, PyCharm, WebStorm 등)를 위한 범용 디버깅 시각화 플러그인입니다. VSCode Debug Visualizer의 핵심 기능을 JetBrains 생태계로 포팅하여, 모든 자료구조와 데이터 타입을 실시간으로 시각화합니다.

## 프로젝트 상태

현재 프로젝트는 **Phase 1 완료** 상태이며, **Phase 2-0 (코드 정리) 진행 중**입니다.

### Phase 1 완료 (2025-11-11) ✅
**기본 프로토타입 구축 완료**

#### 플러그인 (Kotlin)
- ✅ Phase 1-1: 프로젝트 초기화
- ✅ Phase 1-2: 툴 윈도우 UI 구현
- ✅ Phase 1-3: 디버거 API 통합
- ✅ Phase 1-4: 표현식 평가 시스템
- ✅ Phase 1-5: JCEF 웹뷰 통합
- ✅ JDI 기반 값 추출 (프리미티브, 배열, 문자열)
- ✅ TDD 환경 구축 (35개 테스트, 100% 성공, < 1초)

#### React UI (TypeScript)
- ✅ Phase 1-6: React 프로젝트 초기화 (React 18.2 + Vite 5.0)
- ✅ Phase 1-7: D3.js 통합 및 기본 렌더러 (배열 막대 그래프)
- ✅ Phase 1-8: JSON 데이터 브리지 (플러그인 ↔ React UI)
- ✅ Vitest + React Testing Library (10개 테스트, 100% 성공)

### Phase 1 핵심 성과
1. **JDI 기반 값 추출**: IntelliJ XDebugger API의 제한을 극복
   - ✅ 모든 프리미티브 타입 (int, long, float, double, boolean, char, byte, short)
   - ✅ 문자열 및 배열 (중첩 배열 포함)
   - ✅ null 값 처리
   - ✅ 타임아웃 처리 (10초)

2. **JCEF + React 통합**: 웹 기반 시각화 완료
   - ✅ 인라인 HTML/CSS/JS로 안정적 로드
   - ✅ window.visualizerAPI 브리지
   - ✅ Fallback HTML 지원

3. **D3.js 시각화**: 첫 번째 시각화 구현
   - ✅ 배열 → 막대 그래프
   - ✅ 인터랙티브 마우스 오버
   - ✅ char 배열 ASCII 변환
   - ✅ Viridis 색상 그라디언트

4. **TDD 환경**: 빠른 개발 사이클
   - Kotlin: 35개 테스트 (MockK, AssertJ)
   - React: 10개 테스트 (Vitest, RTL)
   - 빌드 자동화: Gradle + npm

### Phase 2: 실시간 모니터링 시스템 (진행 중)

**Phase 2-0: 코드 정리** (2025-01-12)
- 🔴 [#32] AlgorithmDetector 제거 및 범용화
- 🔴 [#33] SnapshotCollector 리팩토링 (VSCode 스키마 호환)
- 🟠 [#34] React UI 리팩토링 (Play 버튼 제거, 실시간 모니터링)
- 🟡 [#35] 통합 테스트 및 문서 업데이트

**Phase 2 핵심 목표**:
- ✅ 실시간 모니터링 (F8 스텝 → 즉시 UI 업데이트)
- ✅ 브레이크포인트 위치 무관 (모든 변수 시각화)
- ✅ 자료구조 중립적 (배열, 트리, 그래프, 스택, 큐 등)
- ✅ 타입 자동 감지 (Priority-based Extractor System)
- ✅ 다중 언어 지원 (Java, Kotlin, Python, JS/TS, C++, C#, Go, Rust)

**다음 단계**:
- 📋 Phase 2-1: 디버거 이벤트 리스너 (#23)
- 📋 Phase 2-2: Observable 상태 관리 (#24)
- 📋 Phase 2-3: Priority-based Extractor System (#25)
- 📋 Phase 2-4: React 다중 렌더러 (#26)
- 📋 Phase 2-5: 실시간 파이프라인 통합 (#27)

## 디렉토리 구조

```
.
├── .claude/              # Claude 프로젝트 설정
│   └── context.md        # 프로젝트 컨텍스트
├── docs/                 # 프로젝트 문서
│   ├── architecture.md   # 시스템 아키텍처
│   ├── visualization-schema.md  # 데이터 스키마
│   ├── PRD.md           # 제품 요구사항 정의서
│   ├── LESSONS_LEARNED.md  # 교훈 및 이슈 정리
│   └── TESTING.md       # 테스트 가이드
├── plugin/              # ✅ IntelliJ 플러그인 코드
│   ├── src/main/kotlin/ # 플러그인 소스
│   │   ├── debugger/    # 디버거 통합 및 표현식 평가
│   │   ├── toolwindow/  # 툴 윈도우 UI
│   │   └── ui/          # JCEF 시각화 패널
│   ├── src/test/kotlin/ # 테스트 코드 (35개)
│   └── build.gradle.kts # Gradle 빌드 설정
├── visualizer-ui/       # ✅ React 시각화 UI (React 18.2 + Vite + D3.js)
├── data-extraction/     # (Phase 3) 언어별 데이터 추출기
├── README.md            # 프로젝트 README
├── CONTRIBUTING.md      # 기여 가이드
└── CLAUDE.md           # 이 파일
```

## 핵심 기술

### 플러그인 개발
- **언어**: Kotlin 1.9.21
- **SDK**: IntelliJ Platform SDK 2023.2.5
- **빌드**: Gradle 8.5
- **JDK**: 17
- **테스트**: JUnit 5 + MockK + AssertJ

### 시각화 UI
- **프레임워크**: React + TypeScript
- **라이브러리**: D3.js, Plotly.js, Cytoscape.js
- **번들러**: Vite 또는 Webpack

### 디버거 통합
- **JVM 언어**: JDI (Java Debug Interface)
- **Python**: debugpy 프로토콜
- **JavaScript**: Chrome DevTools Protocol

## 개발 가이드라인

### 코드 스타일

**Kotlin:**
```kotlin
// 들여쓰기: 4칸
// 최대 줄 길이: 120자
class VisualizerPlugin : DumbAware {
    private val logger = Logger.getInstance(VisualizerPlugin::class.java)

    fun initialize() {
        logger.info("Initializing Algorithm Visualizer")
    }
}
```

**TypeScript:**
```typescript
// 들여쓰기: 2칸
// ESLint + Prettier 사용
export class GraphRenderer implements Renderer {
  render(data: GraphData, container: HTMLElement): void {
    // 구현
  }
}
```

### 커밋 메시지

Conventional Commits 사용:
```
<타입>(<범위>): <제목>

<본문>

<푸터>
```

**타입:**
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 코드 포맷팅
- `refactor`: 리팩토링
- `test`: 테스트 추가/수정
- `chore`: 빌드/설정 변경

### 브랜치 전략

```
main                # 안정 버전
├── develop         # 개발 브랜치
├── feature/*       # 기능 개발
├── fix/*          # 버그 수정
└── docs/*         # 문서 작업
```

## 주요 작업 영역

### 1. 플러그인 코어 (`plugin/`)

**책임:**
- IntelliJ Platform 통합
- 디버거 API 연동
- 표현식 평가
- 도구 윈도우 관리

**주요 클래스:**
- `AlgorithmVisualizerPlugin`: 플러그인 진입점
- `VisualizerToolWindowFactory`: 도구 윈도우 생성
- `DebuggerIntegration`: 디버거 통합
- `DebuggerListener`: 실시간 이벤트 리스너 (XDebugSessionListener)
- `ExpressionEvaluator`: 표현식 평가 (JDI 기반)
- `JCEFVisualizationPanel`: JCEF 웹뷰 통합

### 2. 시각화 UI (`visualizer-ui/`)

**책임:**
- 시각화 렌더링
- 사용자 인터랙션 처리
- 테마 지원
- 애니메이션 제어

**주요 컴포넌트:**
- `App.tsx`: 메인 애플리케이션 (VisualizationRouter)
- `ArrayVisualizer.tsx`: 배열 시각화 (D3.js 막대 그래프) ✅
- `GraphRenderer`: 그래프 시각화 (vis.js) - Phase 2-4
- `TreeRenderer`: 트리 시각화 (SVG) - Phase 2-4
- `TableRenderer`: 테이블 시각화 (Perspective.js) - Phase 2-4

### 3. 데이터 추출 (`data-extraction/`)

**책임:**
- 언어별 데이터 구조 추출
- JSON 변환
- 타입 감지

**언어별 지원 전략 (Tier 시스템)**:
- **Tier 1** (Phase 1-2): Java/Kotlin (JDI 네이티브), JS/TS (런타임 코드 주입)
- **Tier 2** (Phase 3): Python (외부 모듈 `pydebugvisualizer`)
- **Tier 3** (Phase 4): C++, C#, Go, Rust (변수 참조 탐색)

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
JCEFVisualizationPanel.showVisualization(json)
        ↓
[JCEF WebView - React]
        ↓
window.visualizerAPI.updateVisualization(data)
        ↓
VisualizationRouter (타입 판별)
   ├─ if (data.kind.graph) → <GraphRenderer />
   ├─ if (data.kind.tree) → <TreeRenderer />
   ├─ if (data.kind.array) → <ArrayRenderer />
   └─ ...
        ↓
[사용자에게 시각화 표시] (< 100ms)
```

## 시각화 타입 (VSCode 호환 13개)

### Phase 1 구현 완료 ✅
1. **array**: 배열, 리스트 (막대 그래프, D3.js)

### Phase 2-4 구현 예정
2. **graph**: 그래프 구조 (vis.js)
3. **tree**: 트리 구조 (SVG 기반)
4. **table**: 2D 테이블 (Perspective.js)
5. **plotly**: 차트 (Plotly.js)
6. **grid**: 2D 그리드 (HTML Canvas)
7. **text**: 포맷된 텍스트 (단순 렌더링)

### Phase 3-4 구현 예정
8. **monacoText**: 코드 하이라이팅 (Monaco Editor)
9. **image**: 이미지 시각화 (base64, URL)
10. **svg**: SVG 직접 렌더링
11. **graphviz-dot**: DOT 언어 그래프 (Graphviz)
12. **ast**: 추상 구문 트리 (AST)
13. **object-graph**: 객체 참조 그래프 (모든 언어 fallback)

## 테스트 전략

### 단위 테스트 (✅ 구축 완료)
```bash
cd plugin
./gradlew test  # 35개 테스트, < 1초 실행

# 연속 테스트 (TDD 모드)
./gradlew test --continuous

# 특정 테스트만 실행
./gradlew test --tests "*ExpressionEvaluator*"
```

**현재 커버리지:**
- `ExpressionEvaluatorTest`: 13개 테스트
- `JdiValueConverterTest`: 16개 테스트
- `VisualizerToolWindowPanelTest`: 8개 테스트
- 성공률: 100% (35/35)

**참고:** [docs/TESTING.md](docs/TESTING.md) 전체 테스트 가이드 참조

### 통합 테스트
```bash
cd plugin
./gradlew runIde  # 테스트 IDE 인스턴스 실행
```

### E2E 테스트 (Phase 2-5)
- 샘플 프로젝트로 실제 디버깅 시나리오 테스트
- 각 시각화 타입별 테스트 케이스 (13개)
- F8 스텝 → 실시간 업데이트 검증

## 성능 고려사항

1. **대용량 데이터**: 1000개 노드 이상 시 페이지네이션
2. **렌더링 최적화**: 가상 스크롤링, 레벨 제한
3. **메모리 관리**: 이전 시각화 정리, 약한 참조 사용
4. **디바운싱**: 빠른 스테핑 중 업데이트 제한

## 보안

1. **표현식 평가**: 사용자 입력 새니타이제이션
2. **샌드박스**: JCEF 격리된 컨텍스트
3. **CSP**: Content Security Policy 적용
4. **코드 인젝션**: `eval()` 사용 금지

## 디버깅 팁

### 플러그인 디버깅
```bash
cd plugin
./gradlew runIde --debug-jvm
```

IntelliJ의 "Remote JVM Debug" 설정으로 연결

### UI 디버깅
JCEF WebView에서 Chrome DevTools 사용:
```kotlin
browser.jbCefClient.setProperty(
    JBCefClient.Properties.JS_QUERY_POOL_SIZE,
    1000
)
```

## 유용한 명령어

```bash
# 플러그인 빌드
cd plugin && ./gradlew build

# 플러그인 실행
cd plugin && ./gradlew runIde

# UI 개발 서버
cd visualizer-ui && npm run dev

# UI 빌드
cd visualizer-ui && npm run build

# 전체 테스트
./gradlew test && cd visualizer-ui && npm test
```

## 리소스

### 공식 문서
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- [React 문서](https://react.dev/)
- [D3.js 문서](https://d3js.org/)
- [Plotly.js 문서](https://plotly.com/javascript/)

### 참고 프로젝트
- [VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer)
- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

## 자주 묻는 질문 (FAQ)

### Q: 새로운 시각화 타입을 어떻게 추가하나요?
1. `docs/visualization-schema.md`에 VSCode 호환 스키마 정의
2. **Priority-based Extractor** 구현 (`DataExtractor` 인터페이스)
   ```kotlin
   class MyDataExtractor : DataExtractor {
       override val id = "my-data-type"
       override val priority = 550  // 높을수록 우선

       override fun canExtract(value: Any) =
           value.javaClass.name == "com.example.MyData"

       override fun extract(value: Any): VisualizationData {
           // JSON 변환 로직
       }
   }
   ```
3. `DataExtractorRegistry`에 등록
4. `visualizer-ui/src/components/`에 React 렌더러 추가
5. `App.tsx`의 `VisualizationRouter`에 등록
6. 테스트 및 문서 작성

### Q: 새로운 언어 지원을 어떻게 추가하나요?

**Tier 1 (런타임 코드 주입 또는 네이티브 API)**:
1. 언어별 디버거 프로토콜 연구 (예: Chrome DevTools Protocol for JS)
2. `VisualizationBackend` 구현 (예: `JsVisualizationBackend`)
3. 런타임 코드 주입 또는 네이티브 API 사용
4. 13개 기본 추출기 포팅
5. `DispatchingVisualizationBackend`에 등록

**Tier 2 (외부 모듈)**:
1. 외부 모듈 개발 (예: `pydebugvisualizer` for Python)
2. PyPI, npm 등에 배포
3. 플러그인에서 모듈 호출

**Tier 3 (변수 참조 탐색 - 범용 fallback)**:
- 모든 언어 자동 지원 (`GenericVisualizationBackend`)
- XDebugger API로 변수 참조 BFS 탐색
- 객체 그래프 생성 (최대 50개 노드)

### Q: 성능 문제가 있을 때 어떻게 하나요?
1. Chrome DevTools Profiler 사용
2. 렌더링 노드 수 제한 확인
3. 가상 스크롤링/페이지네이션 적용
4. 디바운싱 간격 조정
5. WebWorker 사용 고려

## 도움이 필요할 때

1. **문서 확인**: `docs/` 디렉토리의 문서들
2. **이슈 검색**: GitHub Issues에서 유사 문제 검색
3. **토론 시작**: GitHub Discussions에서 질문
4. **메인테이너 연락**: 이슈 생성 또는 PR 코멘트

## Claude Code 작업 시 주의사항

1. **문서 우선 참조**: 변경 전 관련 문서 확인
2. **타입 안전성**: TypeScript/Kotlin 타입 엄격히 사용
3. **테스트 작성**: 새 기능에는 테스트 필수
4. **커밋 메시지**: Conventional Commits 준수
5. **코드 리뷰**: PR 전 self-review 수행

---

## 최근 업데이트

### 2025-01-12: 프로젝트 방향 재정립 (v2.0.0)
**배경**: 사용자 피드백으로 전체 방향 전환
- ❌ 기존: 정렬 알고리즘 전용, Play 버튼 애니메이션
- ✅ 새로운: 범용 Debug Visualizer, F8 실시간 모니터링

**주요 변경**:
1. **PRD v2.0.0 작성**: VSCode Debug Visualizer 기반 재설계
2. **GitHub Issues 재구성**: #23-#31 (Phase 2-4 로드맵)
3. **코드 정리 계획**: #32-#35 (Phase 2-0)

**핵심 원칙**:
- 실시간 모니터링 우선 (F8 → 즉시 업데이트)
- 브레이크포인트 위치 무관
- 자료구조 중립적 (모든 타입 지원)
- 타입 자동 감지 (Priority-based Extractor)
- 다중 언어 지원 (Tier 1/2/3)

### 2025-11-10: Phase 1 완료
1. **JDI 기반 값 추출**: 모든 프리미티브 타입 지원
2. **TDD 환경 구축**: 35개 테스트, 100% 성공률
3. **JCEF + React 통합**: 웹 기반 시각화 완료
4. **D3.js 배열 시각화**: 막대 그래프 구현

### 🔑 핵심 교훈
- IntelliJ XDebugger API는 추상화 레이어 → 복잡한 값 추출은 JDI 직접 사용
- JDI 클래스는 final → mocking 불가, 로직 분리 필요
- VSCode Debug Visualizer는 Priority-based Extractor System 사용 → 우리도 동일하게 구현

---

**마지막 업데이트**: 2025-01-12
**문서 버전**: 2.0.0 (VSCode 기반)
