# 프로젝트 컨텍스트

## 프로젝트 개요

**프로젝트 이름:** Algorithm Debug Visualizer for JetBrains IDEs

**목적:** JetBrains IDE 사용자들이 알고리즘 디버깅 시 데이터 구조와 알고리즘 실행 과정을 시각적으로 이해할 수 있도록 돕는 플러그인 개발

**영감:** VSCode Debug Visualizer에서 영감을 받아, JetBrains 생태계에 더 강력하고 다양한 언어를 지원하는 시각화 도구 제공

## 핵심 가치 제안

1. **다양한 알고리즘 타입 지원**: 9가지 주요 알고리즘 유형 완벽 커버
2. **다국어 지원**: Java, Kotlin, Python, JavaScript 등 4개 언어 완전 지원
3. **풍부한 시각화**: 그래프, 트리, 배열, 테이블 등 7가지 시각화 타입
4. **실시간 디버깅**: 디버거와 긴밀하게 통합되어 실시간으로 데이터 구조 변화 추적

## 기술 스택

### 플러그인 개발
- **언어**: Kotlin
- **프레임워크**: IntelliJ Platform SDK 2023.1+
- **빌드 도구**: Gradle 8.0+
- **JDK**: 17+

### 시각화 UI
- **언어**: TypeScript
- **프레임워크**: React
- **시각화 라이브러리**:
  - D3.js (그래프, 트리)
  - Plotly.js (차트)
  - Cytoscape.js (복잡한 네트워크)
- **UI 컨테이너**: JCEF (Java Chromium Embedded Framework)

### 디버거 통합
- **JVM**: JDI (Java Debug Interface)
- **Python**: debugpy 프로토콜
- **JavaScript**: Chrome DevTools Protocol

## 프로젝트 구조

```
algorithm-debug-visualizer/
├── plugin/                 # IntelliJ Platform 플러그인
│   ├── src/
│   │   ├── main/kotlin/   # 플러그인 핵심 로직
│   │   ├── main/resources/# 리소스 파일
│   │   └── test/          # 테스트 코드
│   └── build.gradle.kts   # 빌드 설정
├── visualizer-ui/         # React 기반 시각화 UI
│   ├── src/
│   │   ├── components/    # React 컴포넌트
│   │   ├── visualizers/   # 시각화 렌더러
│   │   └── styles/        # 스타일시트
│   └── package.json
├── data-extraction/       # 언어별 데이터 추출 모듈
│   ├── java/
│   ├── kotlin/
│   ├── python/
│   └── javascript/
└── docs/                  # 프로젝트 문서
```

## 개발 로드맵

### Phase 1: 핵심 인프라 (4주)
- IntelliJ Platform SDK 기반 플러그인 스켈레톤
- 디버거 API 통합
- 기본 표현식 평가 시스템
- JCEF 웹뷰 설정

### Phase 2: 시각화 엔진 (4주)
- React + TypeScript UI 구축
- D3.js 통합
- 기본 그래프/트리 렌더러 구현
- JSON 데이터 브리지 구축

### Phase 3: 언어 지원 (6주)
- Java/Kotlin 데이터 추출기 (JDI 기반)
- Python 데이터 추출기 (debugpy 프로토콜)
- JavaScript/TypeScript 추출기 (Chrome DevTools)
- 각 언어별 테스트 케이스

### Phase 4: 알고리즘별 시각화 (8주)
- 배열/정렬 시각화
- 트리 시각화 (Binary, AVL, RB)
- 그래프 알고리즘 시각화 (DFS, BFS, Dijkstra 등)
- DP 테이블 시각화
- 백트래킹 시각화

### Phase 5: 고급 기능 (4주)
- 커스텀 시각화 API
- 애니메이션 컨트롤
- 내보내기 기능 (PNG, SVG)
- 테마 지원 (Light/Dark)
- 성능 최적화

## 주요 도전 과제

1. **디버거 통합 복잡성**: 각 언어별 디버거 프로토콜이 다름
2. **성능 최적화**: 대용량 데이터 구조 시각화 시 성능 문제
3. **타입 감지**: 런타임에 데이터 구조 타입 정확히 식별
4. **크로스 플랫폼**: Windows, macOS, Linux 모두 지원
5. **버전 호환성**: 다양한 IntelliJ Platform 버전 지원

## 성공 지표

1. **사용자 채택률**: JetBrains Marketplace에서 월 1,000+ 다운로드
2. **사용자 만족도**: 4.0+ 별점 유지
3. **커뮤니티 기여**: 5+ 외부 기여자
4. **언어 커버리지**: 4개 이상 언어 완전 지원
5. **버그율**: Critical 버그 0개 유지

## 타겟 사용자

1. **알고리즘 학습자**: 코딩 테스트 준비생, 학생
2. **소프트웨어 엔지니어**: 복잡한 데이터 구조 디버깅 필요
3. **교육자**: 알고리즘 교육용 시각화 도구 필요
4. **오픈소스 기여자**: 프로젝트 디버깅 및 이해

## 경쟁 제품 분석

### VSCode Debug Visualizer
- **장점**: 성숙한 생태계, 다양한 시각화
- **단점**: VSCode 전용, 일부 언어만 완전 지원
- **차별점**: JetBrains IDE 네이티브 통합, 더 많은 알고리즘 타입 지원

### JetBrains 내장 디버거
- **장점**: 네이티브 통합, 안정적
- **단점**: 텍스트 기반, 시각화 부족
- **차별점**: 리치 시각화, 알고리즘 중심 설계

## 라이선스

TBD (MIT 또는 Apache 2.0 고려 중)

## 커뮤니티

- **GitHub**: 이슈 트래킹, PR, 토론
- **JetBrains Marketplace**: 플러그인 배포
- **문서**: GitHub Pages 또는 별도 문서 사이트

## 개발 원칙

1. **사용자 중심**: 사용자 경험 최우선
2. **성능**: 대용량 데이터에서도 빠른 렌더링
3. **확장성**: 커스텀 시각화 쉽게 추가 가능
4. **안정성**: 철저한 테스트, 버그 최소화
5. **문서화**: 명확한 문서와 예제 제공
