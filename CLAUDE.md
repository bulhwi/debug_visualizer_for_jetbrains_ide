# Claude Code를 위한 프로젝트 가이드

이 문서는 Claude Code가 이 프로젝트에서 작업할 때 참조해야 할 핵심 정보를 담고 있습니다.

## 프로젝트 개요

**Algorithm Debug Visualizer**는 JetBrains IDE(IntelliJ IDEA, PyCharm, WebStorm 등)를 위한 디버깅 시각화 플러그인입니다. VSCode Debug Visualizer에서 영감을 받아, 알고리즘 학습과 디버깅을 위한 강력한 시각화 도구를 JetBrains 생태계에 제공합니다.

## 프로젝트 상태

현재 프로젝트는 **Phase 1 구현 단계**에 있습니다.

### 완료된 작업
- ✅ Phase 1-1: 프로젝트 초기화
- ✅ Phase 1-2: 툴 윈도우 UI 구현
- ✅ Phase 1-3: 디버거 API 통합
- ✅ Phase 1-4: 표현식 평가 시스템
- ✅ Phase 1-5: JCEF 웹뷰 통합
- ✅ JDI 기반 값 추출 (프리미티브, 배열, 문자열)
- ✅ TDD 환경 구축 (35개 테스트, 100% 성공)

### 진행 중
- 🔄 Phase 1-6: React 프로젝트 초기화
- 🔄 Phase 1-7: D3.js 통합 및 기본 렌더러
- 🔄 Phase 1-8: JSON 데이터 브리지
- 🔄 Phase 1-9: Java/Kotlin 데이터 추출기 확장

### 핵심 성과
1. **JDI 기반 값 추출**: IntelliJ XDebugger API의 제한을 극복하고 JDI를 직접 사용
   - ✅ 모든 프리미티브 타입 (int, long, float, double, boolean, char, byte, short)
   - ✅ 문자열 및 배열 (중첩 배열 포함)
   - ✅ null 값 처리
2. **JCEF 통합**: 웹 기반 시각화를 위한 브라우저 컴포넌트 통합 완료
3. **비동기 평가**: CompletableFuture를 사용한 안전한 비동기 표현식 평가
4. **TDD 환경**: 빠른 피드백 루프 (< 1초, 35개 테스트)

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
├── visualizer-ui/       # (예정) React 시각화 UI
├── data-extraction/     # (예정) 언어별 데이터 추출기
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
- `ExpressionEvaluator`: 표현식 평가

### 2. 시각화 UI (`visualizer-ui/`)

**책임:**
- 시각화 렌더링
- 사용자 인터랙션 처리
- 테마 지원
- 애니메이션 제어

**주요 컴포넌트:**
- `VisualizerController`: 메인 컨트롤러
- `GraphRenderer`: 그래프 시각화
- `TreeRenderer`: 트리 시각화
- `ArrayRenderer`: 배열 시각화

### 3. 데이터 추출 (`data-extraction/`)

**책임:**
- 언어별 데이터 구조 추출
- JSON 변환
- 타입 감지

**모듈:**
- `java/`: Java 데이터 추출기
- `kotlin/`: Kotlin 데이터 추출기
- `python/`: Python 데이터 추출기
- `javascript/`: JS/TS 데이터 추출기

## 데이터 흐름

```
1. 사용자가 표현식 입력 (예: "myTree")
   ↓
2. 플러그인이 디버거 API로 표현식 평가
   ↓
3. 데이터 추출기가 값을 JSON으로 변환
   ↓
4. JCEF 브리지를 통해 WebView로 전송
   ↓
5. React 컴포넌트가 적절한 렌더러 선택
   ↓
6. D3.js/Plotly로 시각화 렌더링
```

## 시각화 타입

1. **graph**: 그래프 구조 (DFS, BFS, 최단 경로 등)
2. **tree**: 트리 구조 (이진 트리, AVL, RB 등)
3. **array**: 배열 (정렬, 투 포인터 등)
4. **table**: 2D 테이블 (DP, 행렬 등)
5. **plotly**: 차트 (히스토그램, 선 그래프 등)
6. **grid**: 2D 그리드 (체스판, 미로 등)
7. **text**: 포맷된 텍스트

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

### E2E 테스트 (예정)
- 샘플 프로젝트로 실제 디버깅 시나리오 테스트
- 각 알고리즘 타입별 테스트 케이스

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
1. `docs/visualization-schema.md`에 스키마 정의
2. 데이터 추출기에서 변환 로직 구현
3. `visualizer-ui/src/visualizers/`에 렌더러 추가
4. `VisualizerController`에 등록
5. 테스트 및 문서 작성

### Q: 새로운 언어 지원을 어떻게 추가하나요?
1. `data-extraction/<language>/` 디렉토리 생성
2. 언어별 디버거 프로토콜 연구
3. 데이터 추출 로직 구현
4. 플러그인 코어에 통합
5. 테스트 케이스 작성

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

## 최근 업데이트 (2025-11-10)

### ✅ 완료된 주요 작업
1. **int 타입 추출 문제 해결**: JDI를 사용하여 모든 프리미티브 타입 추출 성공
2. **TDD 환경 구축**: 35개 단위 테스트, 100% 성공률, < 1초 실행
3. **코드 리팩토링**: ExpressionEvaluator 상수 추출, 헬퍼 메서드 분리
4. **문서 작성**:
   - `docs/LESSONS_LEARNED.md`: 디버깅 과정에서 얻은 교훈
   - `docs/TESTING.md`: TDD 워크플로우 및 테스트 가이드

### 🔑 핵심 교훈
- IntelliJ XDebugger API는 추상화 레이어이며, 복잡한 값 추출에는 JDI 직접 사용 필요
- JDI 클래스는 final class라서 mocking 불가 → 로직 분리 필요
- DebuggerCommandImpl을 통해 디버거 스레드에서 안전하게 JDI 접근

---

**마지막 업데이트**: 2025-11-10
**문서 버전**: 1.1.0
