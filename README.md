# JetBrains IDE용 알고리즘 디버그 시각화 도구

JetBrains IDE(IntelliJ IDEA, PyCharm, WebStorm, CLion 등)를 위한 강력한 디버깅 시각화 플러그인으로, 풍부한 시각적 표현을 통해 개발자가 알고리즘 실행을 이해할 수 있도록 돕습니다.

## 개발 동기

[VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer)에서 영감을 받아, 이 플러그인은 JetBrains 생태계에 포괄적인 알고리즘 시각화 기능을 제공하며, 다양한 프로그래밍 언어와 알고리즘 유형에 대한 향상된 지원을 제공합니다.

## 주요 기능

### 지원하는 시각화

- **그래프 구조**: 노드와 엣지가 있는 그래프 시각화 (방향/무방향, 가중치)
- **트리 구조**: 이진 트리, BST, AVL, Red-Black 트리, Trie, 세그먼트 트리
- **배열 & 리스트**: 막대 그래프, 그리드 뷰, 포인터/윈도우 하이라이팅이 있는 히트맵
- **테이블**: DP 테이블, 인접 행렬, 거리 테이블
- **차트**: 선 그래프, 히스토그램, 파이 차트 (Plotly 기반)
- **커스텀 시각화**: 자체 시각화 로직 정의 가능

### 알고리즘 유형 커버리지

1. **배열/리스트 알고리즘**
   - 정렬 (버블, 퀵, 병합, 힙 정렬)
   - 투 포인터, 슬라이딩 윈도우
   - 부분 배열 문제

2. **트리 알고리즘**
   - 순회 (전위, 중위, 후위, 레벨 순서)
   - BST 연산
   - 균형 트리 회전
   - 트리 수정

3. **그래프 알고리즘**
   - 단계별 순회가 있는 DFS/BFS
   - 최단 경로 (다익스트라, 벨만-포드, 플로이드-워셜)
   - MST (크루스칼, 프림)
   - 위상 정렬
   - 네트워크 플로우

4. **동적 프로그래밍**
   - 채우기 순서가 있는 DP 테이블 시각화
   - 백트래킹 경로 하이라이팅
   - 메모이제이션 상태 추적

5. **백트래킹**
   - 결정 트리 시각화
   - 보드/그리드 상태 (N-Queens, 스도쿠)
   - 가지치기 시각화

6. **문자열 알고리즘**
   - 패턴 매칭 (KMP, Rabin-Karp)
   - 문자 단위 하이라이팅
   - 실패 함수 테이블

7. **힙/우선순위 큐**
   - 힙 트리 구조
   - 부모-자식 관계
   - 힙 속성 검증

8. **고급 자료구조**
   - 집합 그룹화가 있는 Union-Find (Disjoint Set)
   - 펜윅 트리 (BIT)
   - LRU 캐시 상태
   - 스택/큐 연산

### 언어 지원

#### Tier 1 (완전 지원 - 자동 데이터 추출)
- Java
- Kotlin
- Python
- JavaScript/TypeScript

#### Tier 2 (기본 지원 - JSON 직렬화)
- C++
- Go
- Rust
- Scala

#### Tier 3 (제한적 지원)
- C#
- PHP
- Ruby

## 아키텍처

```
algorithm-debug-visualizer/
├── plugin/                 # IntelliJ Platform 플러그인 코드
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/    # 플러그인 로직
│   │   │   └── resources/ # 플러그인 설정
│   │   └── test/
│   └── build.gradle.kts
├── visualizer-ui/         # 웹 기반 시각화 UI
│   ├── src/
│   │   ├── components/    # React/Vue 컴포넌트
│   │   ├── visualizers/   # 시각화 구현
│   │   └── styles/
│   └── package.json
├── data-extraction/       # 언어별 데이터 추출기
│   ├── java/
│   ├── kotlin/
│   ├── python/
│   └── javascript/
└── docs/                  # 문서
    ├── architecture.md
    ├── api-reference.md
    └── developer-guide.md
```

## 기술 스택

- **플러그인 프레임워크**: IntelliJ Platform SDK
- **UI 렌더링**: JCEF (Java Chromium Embedded Framework)
- **시각화 라이브러리**: D3.js, Plotly.js, Cytoscape.js
- **디버거 통합**: IntelliJ Platform Debugger API
- **언어 지원**:
  - JVM: JDI (Java Debug Interface)
  - Python: debugpy 프로토콜
  - JavaScript: Chrome DevTools Protocol

## 시작하기

### 사전 요구사항

- JetBrains IDE (IntelliJ IDEA 2023.1+, PyCharm, WebStorm 등)
- JDK 17+
- Gradle 8.0+

### 설치

1. **JetBrains Marketplace에서 설치** (향후 제공 예정)
   - IDE의 Settings/Preferences → Plugins
   - "Algorithm Debug Visualizer" 검색 및 설치

2. **소스에서 빌드하여 설치**
   ```bash
   # 저장소 클론
   git clone https://github.com/bulhwi/debug_visualizer_for_jetbrains_ide.git
   cd debug_visualizer_for_jetbrains_ide/plugin

   # 빌드
   ./gradlew buildPlugin

   # 빌드된 플러그인은 plugin/build/distributions/에 생성됩니다
   # IDE의 Settings/Preferences → Plugins → ⚙️ → Install Plugin from Disk...
   # 에서 생성된 .zip 파일을 선택하여 설치
   ```

### 개발 환경 설정

```bash
# 프로젝트 클론
git clone https://github.com/bulhwi/debug_visualizer_for_jetbrains_ide.git
cd debug_visualizer_for_jetbrains_ide/plugin

# 의존성 다운로드 및 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 테스트 IDE 인스턴스 실행 (플러그인이 설치된 상태로)
./gradlew runIde

# 플러그인 배포 파일 생성
./gradlew buildPlugin
```

### 사용법

1. **디버깅 세션 시작**
   - 코드에 중단점 설정
   - 디버그 모드로 프로그램 실행

2. **시각화 도구 윈도우 열기**
   - View → Tool Windows → Algorithm Visualizer

3. **변수 시각화**
   - 표현식 입력 필드에 변수명 입력 (예: `myArray`, `myTree`)
   - Enter 키 또는 "Evaluate" 버튼 클릭
   - 시각화가 자동으로 표시됩니다

4. **스테핑**
   - Step Over / Step Into 실행 시 시각화가 자동 업데이트됩니다

**현재 상태**: ✅ Phase 1 완료 → Phase 2 진행 예정

## 프로젝트 상태

### ✅ Phase 1: 기본 프로토타입 (완료 - 2025-11-11)
- ✅ IntelliJ Platform Plugin 초기화
- ✅ 디버거 API 통합 (XDebuggerManager)
- ✅ JDI 기반 표현식 평가 (모든 프리미티브 타입 지원)
- ✅ JCEF 웹뷰 통합
- ✅ React UI + D3.js 기본 시각화 (배열 막대 그래프)
- ✅ 데이터 브리지 (Kotlin ↔ React)
- ✅ TDD 환경 구축 (45개 테스트, 100% 통과)

**기술 스택**:
- Kotlin 1.9.21 + IntelliJ Platform 2023.2.5
- React 18.2 + TypeScript 5.2 + Vite 5.0
- D3.js 7.8.5
- JUnit 5 + MockK + Vitest + React Testing Library

### 🚀 Phase 2: 알고리즘별 맞춤 시각화 (진행 예정 - 2-3주)
- [ ] **정렬 알고리즘 시각화** (버블, 퀵, 병합 정렬)
  - [ ] SortVisualizer 컴포넌트 (TDD)
  - [ ] 알고리즘 자동 감지
  - [ ] 스냅샷 수집 및 애니메이션
- [ ] **트리 구조 시각화** (이진 트리, BST, AVL)
  - [ ] TreeVisualizer 컴포넌트 (TDD)
  - [ ] TreeNode 파서 (JDI 기반)
  - [ ] 삽입/삭제/탐색 애니메이션
- [ ] **DP 테이블 시각화**
  - [ ] DPTableVisualizer 컴포넌트 (TDD)
  - [ ] 2D 히트맵
  - [ ] 최적 경로 추적
- [ ] **그래프 시각화** (보너스)

### 📋 Phase 3: 실시간 스텝 추적 (예정)
- [ ] 디버거 스텝 이벤트 자동 감지
- [ ] 실시간 UI 업데이트
- [ ] 타임라인 재생 (TimeMachine)
- [ ] 코드-시각화 양방향 점프
- [ ] 애니메이션 엔진
- [ ] 비교 모드

**참고 문서**:
- [Phase 1 완료 보고서](./docs/PHASE1_COMPLETE.md)
- [Phase 2 상세 계획](./docs/PHASE2_PLAN.md)
- [Phase 3 상세 계획](./docs/PHASE3_PLAN.md)
- [다음 작업 가이드](./NEXT_STEPS.md)

## 기여하기

기여를 환영합니다! 자세한 내용은 [기여 가이드](CONTRIBUTING.md)를 참조해주세요.

## 라이선스

TBD

## 감사의 말

- [VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer)에서 영감을 받았습니다
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)로 제작되었습니다
