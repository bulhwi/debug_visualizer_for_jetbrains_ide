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

준비 중...

### 사용법

준비 중...

## 개발 로드맵

- [ ] Phase 1: 핵심 플러그인 인프라
  - [ ] IntelliJ Platform SDK를 사용한 플러그인 스켈레톤
  - [ ] 디버거 API 통합
  - [ ] 기본 표현식 평가

- [ ] Phase 2: 시각화 엔진
  - [ ] JCEF 웹뷰 설정
  - [ ] D3.js 통합
  - [ ] 기본 그래프/트리 렌더링

- [ ] Phase 3: 언어 지원
  - [ ] Java/Kotlin 데이터 추출
  - [ ] Python 데이터 추출
  - [ ] JavaScript/TypeScript 데이터 추출

- [ ] Phase 4: 알고리즘별 시각화
  - [ ] 배열/정렬 시각화
  - [ ] 트리 시각화
  - [ ] 그래프 알고리즘 시각화
  - [ ] DP 테이블 시각화

- [ ] Phase 5: 고급 기능
  - [ ] 커스텀 시각화 API
  - [ ] 애니메이션 컨트롤
  - [ ] 내보내기 기능
  - [ ] 테마 지원

## 기여하기

기여를 환영합니다! 자세한 내용은 [기여 가이드](CONTRIBUTING.md)를 참조해주세요.

## 라이선스

TBD

## 감사의 말

- [VSCode Debug Visualizer](https://github.com/hediet/vscode-debug-visualizer)에서 영감을 받았습니다
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)로 제작되었습니다
