# Claude Code를 위한 프로젝트 가이드

이 문서는 Claude Code가 이 프로젝트에서 작업할 때 참조해야 할 핵심 정보를 담고 있습니다.

## 프로젝트 개요

**Algorithm Debug Visualizer**는 JetBrains IDE(IntelliJ IDEA, PyCharm, WebStorm 등)를 위한 디버깅 시각화 플러그인입니다. VSCode Debug Visualizer에서 영감을 받아, 알고리즘 학습과 디버깅을 위한 강력한 시각화 도구를 JetBrains 생태계에 제공합니다.

## 프로젝트 상태

현재 프로젝트는 **기획 및 설계 단계**에 있습니다.

- ✅ 프로젝트 문서화 완료 (한국어)
- ✅ 아키텍처 설계 완료
- ✅ 시각화 데이터 스키마 정의
- 🔄 다음: 플러그인 프로토타입 구현

## 디렉토리 구조

```
.
├── .claude/              # Claude 프로젝트 설정
│   └── context.md        # 프로젝트 컨텍스트
├── docs/                 # 프로젝트 문서
│   ├── architecture.md   # 시스템 아키텍처
│   ├── visualization-schema.md  # 데이터 스키마
│   └── PRD.md           # 제품 요구사항 정의서
├── plugin/              # (예정) IntelliJ 플러그인 코드
├── visualizer-ui/       # (예정) React 시각화 UI
├── data-extraction/     # (예정) 언어별 데이터 추출기
├── README.md            # 프로젝트 README
├── CONTRIBUTING.md      # 기여 가이드
└── CLAUDE.md           # 이 파일
```

## 핵심 기술

### 플러그인 개발
- **언어**: Kotlin
- **SDK**: IntelliJ Platform SDK 2023.1+
- **빌드**: Gradle 8.0+
- **JDK**: 17+

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

### 단위 테스트
```bash
cd plugin
./gradlew test

cd visualizer-ui
npm test
```

### 통합 테스트
```bash
cd plugin
./gradlew runIde  # 테스트 IDE 인스턴스 실행
```

### E2E 테스트
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

**마지막 업데이트**: 2024-11-10
**문서 버전**: 1.0.0
