# 🎯 GitHub Issues 요약

**생성일**: 2025-01-12
**프로젝트**: Debug Visualizer for JetBrains IDEs (v2.0)

---

## 📋 현재 상태

| Phase | 이슈 수 | 상태 |
|-------|--------|------|
| **Phase 2: 실시간 모니터링** | 5개 | 🔴 진행 예정 |
| **Phase 3: 다중 언어 지원** | 3개 | 🟡 대기 중 |
| **Phase 4: 고급 기능** | 1개 | 🟢 대기 중 |
| **총계** | **9개** | - |

---

## 🔴 Phase 2: 실시간 모니터링 시스템 (Critical)

### [#23] 디버거 이벤트 리스너 구현 (XDebugSessionListener)
**목표**: F8 스텝마다 자동으로 표현식을 재평가하고 시각화를 업데이트

**세부 태스크**:
1. XDebugSessionListener 구현
   - `sessionPaused()` - F8 스텝 감지
   - `sessionResumed()` - 실행 재개 감지
   - `stackFrameChanged()` - 스택 프레임 변경 감지
2. 스택 프레임 ID 추적
3. 자동 재평가 메커니즘 (취소 토큰, 디바운싱)
4. 테스트 (10개 이상)

**수용 기준**: F8 스텝 시 자동 재평가, 이전 요청 취소

**참고**: VSCode `DebuggerProxy.ts`, `DebuggerViewProxy.ts`

---

### [#24] Observable 상태 관리 시스템 (VisualizationWatchModel)
**목표**: MobX 스타일의 Observable 상태 관리를 Kotlin으로 구현

**세부 태스크**:
1. VisualizationWatchModel 인터페이스 설계
   - `StateFlow<DataExtractionState>` (Loading/Data/Error)
2. 취소 토큰 구현 (CancellationToken)
3. 디바운싱 메커니즘 (에러 후 330ms 대기)
4. Kotlin Flow 통합
5. 테스트 (15개 이상)

**수용 기준**: 상태 변경 시 자동 UI 업데이트, 취소 토큰 동작

**참고**: VSCode `VisualizationWatchModelImpl.ts`

---

### [#25] 우선순위 기반 데이터 추출기 시스템
**목표**: VSCode의 핵심 메커니즘인 우선순위 기반 추출기 시스템 구현

**세부 태스크**:
1. DataExtractor 인터페이스 설계
   ```kotlin
   interface DataExtractor {
       val id: String
       val priority: Int
       fun canExtract(value: Any): Boolean
       fun extract(value: Any): VisualizationData
   }
   ```
2. 8개 기본 추출기 구현
   - GetVisualizationDataExtractor (600)
   - TreeExtractor (550)
   - GraphExtractor (540)
   - ArrayExtractor (500)
   - MapExtractor (490)
   - ListExtractor (480)
   - ObjectGraphExtractor (98)
   - ToStringExtractor (50)
3. DataExtractorRegistry (등록/선택 로직)
4. JvmVisualizationBackend 통합
5. 테스트 (30개 이상)

**수용 기준**: 8개 추출기 구현, 우선순위 자동 선택

**참고**: VSCode `DataExtractorApiImpl.ts`, `default-extractors/`

---

### [#26] React UI 다중 렌더러 구현 (Graph, Tree, Table)
**목표**: VSCode의 시각화 렌더러를 React로 포팅

**세부 태스크**:
1. VisualizationRouter 구현 (타입 판별 및 렌더러 선택)
2. GraphRenderer (vis.js 기반)
   - 노드/엣지 DataSet
   - 물리 시뮬레이션
   - 테스트 (5개)
3. TreeRenderer (SVG 기반)
   - 재귀적 렌더링
   - 접기/펼치기
   - 테스트 (7개)
4. TableRenderer (Perspective.js)
   - 정렬/필터링
   - 가상 스크롤링
   - 테스트 (5개)
5. 공통 컴포넌트 (NoData, Message, Theme)
6. 통합 테스트

**수용 기준**: 3개 렌더러 구현, 타입 자동 판별, 25개 이상 테스트

**참고**: VSCode `visualization-bundle/src/visualizers/`

---

### [#27] 실시간 업데이트 파이프라인 통합
**목표**: 모든 컴포넌트를 연결하여 F8 → 시각화 전체 파이프라인 완성

**세부 태스크**:
1. 전체 파이프라인 구축
   ```
   F8 → sessionPaused() → refresh() → getVisualizationData()
   → selectBestExtractor() → extract() → JSON
   → showVisualization() → updateVisualization()
   → VisualizationRouter → 렌더링
   ```
2. Kotlin Flow 기반 상태 동기화
3. JCEF 브리지 개선
4. 성능 최적화 (JSON 직렬화, 디바운싱)
5. 에러 처리
6. E2E 테스트 (10개 이상)

**수용 기준**: F8 → 시각화 < 200ms, 모든 컴포넌트 연결, 에러 처리

**참고**: VSCode `WebviewConnection.ts`

---

## 🟡 Phase 3: 다중 언어 지원 (High/Medium)

### [#28] JavaScript/TypeScript 런타임 코드 주입 시스템
**목표**: VSCode의 핵심 기능인 런타임 코드 주입을 JetBrains에 구현

**세부 태스크**:
1. Chrome DevTools Protocol 연동
2. Data Extractor API 번들 생성
3. 런타임 코드 주입
   ```kotlin
   // 1. API 초기화 (최초 1회)
   debugSession.evaluate("(function() { $initCode })()")

   // 2. 표현식을 API로 감싸서 평가
   debugSession.evaluate("api.getData(() => ($expression))")
   ```
4. 13개 기본 추출기 포팅
5. 테스트 (20개 이상)

**수용 기준**: CDP 연동, 런타임 주입 성공, 13개 추출기 동작

**참고**: VSCode `JsVisualizationSupport.ts`, `injection.ts`

---

### [#29] Python debugpy 프로토콜 연동 및 외부 모듈
**목표**: Python에서 외부 모듈을 통해 시각화 데이터 추출

**세부 태스크**:
1. debugpy 프로토콜 연동
2. `pydebugvisualizer` 모듈 개발
   ```python
   def visualize(obj):
       if hasattr(obj, 'get_visualization_data'):
           return obj.get_visualization_data()
       # ... 자료구조별 처리
   ```
3. PyVisualizationBackend 구현
4. PyPI 배포
5. 테스트 (15개 이상)

**수용 기준**: debugpy 연동, 모듈 개발, PyPI 배포

**참고**: VSCode `PyVisualizationSupport.ts`

---

### [#30] 범용 언어 지원 (GenericVisualizationBackend)
**목표**: C++, C#, Go, Rust 등 모든 언어에서 변수 참조 탐색으로 지원

**세부 태스크**:
1. XDebugger 변수 참조 탐색
2. 객체 그래프 생성 (BFS)
   - 최대 50개 노드
   - 최대 30 레벨
   - 순환 참조 처리
3. 타입별 색상 코딩
4. 테스트 (10개 이상)

**수용 기준**: 변수 참조 탐색, BFS 그래프 생성, 순환 참조 처리

**참고**: VSCode `GenericVisualizationSupport.ts`

---

## 🟢 Phase 4: 고급 기능 (Low)

### [#31] 고급 기능 및 JetBrains 전용 기능
**목표**: VSCode를 넘어 JetBrains 특화 기능 추가

**세부 태스크**:
1. 사용자 정의 추출기 API (Extension Point)
2. 실행 히스토리 (Time-Travel Debugging)
3. 내보내기 (PNG, SVG, GIF, HTML)
4. 코드 하이라이트 연동 (JetBrains 전용)
   - 시각화 노드 클릭 → 에디터 하이라이트
5. 리팩토링 제안 (JetBrains 전용)
   - 비효율적인 자료구조 감지 → Quick Fix
6. 성능 프로파일링 오버레이
7. 팀 협업 기능 (스냅샷 공유)

**수용 기준**: 3개 이상 고급 기능, 1개 이상 JetBrains 전용 기능

**참고**: IntelliJ Platform SDK - PSI, Inspections

---

## 📊 통계

### 이슈별 예상 작업량
| Phase | 이슈 | 예상 테스트 수 | 예상 기간 |
|-------|------|---------------|---------|
| Phase 2-1 | #23 | 10+ | 1주 |
| Phase 2-2 | #24 | 15+ | 1주 |
| Phase 2-3 | #25 | 30+ | 2주 |
| Phase 2-4 | #26 | 25+ | 2주 |
| Phase 2-5 | #27 | 10+ | 1주 |
| **Phase 2 합계** | **5개** | **90+** | **7주** |
| Phase 3-1 | #28 | 20+ | 2주 |
| Phase 3-2 | #29 | 15+ | 1주 |
| Phase 3-3 | #30 | 10+ | 1주 |
| **Phase 3 합계** | **3개** | **45+** | **4주** |
| Phase 4 | #31 | TBD | 2주 |
| **전체 합계** | **9개** | **135+** | **13주** |

### 우선순위 분포
- 🔴 Critical: 5개 (Phase 2)
- 🟠 High: 1개 (Phase 3-1)
- 🟡 Medium: 2개 (Phase 3-2, 3-3)
- 🟢 Low: 1개 (Phase 4)

---

## 🎯 다음 액션

1. **Phase 2-1 시작**: [#23] 디버거 이벤트 리스너 구현
2. TDD 방식으로 개발 (테스트 먼저 작성)
3. VSCode Debug Visualizer 코드 참고
4. 각 이슈 완료 시 체크리스트 업데이트

---

**관련 문서**:
- [PRD v2.0.0](docs/PRD.md)
- [VSCode Debug Visualizer 분석](https://github.com/hediet/vscode-debug-visualizer)
- [Visualization Data Schema](https://hediet.github.io/visualization/docs/visualization-data-schema.json)
