# Phase 3 계획: 실시간 스텝 추적 및 고급 기능

**시작 예정일**: Phase 2 완료 후
**예상 기간**: 2-3주
**TDD 필수**: 모든 작업

---

## 🎯 목표

디버거 스텝 실행과 동기화된 실시간 시각화 및 고급 인터랙티브 기능을 구현합니다.

---

## 📋 작업 항목

### 3.1 실시간 스텝 추적 (1주)

#### 3.1.1 StepListener
**목표**: 디버거 스텝 이벤트 자동 감지

**기능**:
- Step Over/Into/Out 이벤트 리스닝
- 변경된 변수 자동 감지
- 델타 계산 (이전 상태 vs 현재 상태)

**TDD 체크리스트**:
- [ ] 스텝 이벤트 리스닝 테스트
- [ ] 변수 변경 감지 테스트
- [ ] 델타 계산 테스트
- [ ] 에러 처리 테스트

**예상 시간**: 3일

---

#### 3.1.2 자동 업데이트
**목표**: Step Over 시 시각화 자동 새로고침

**기능**:
- 표현식 재평가
- 시각화 자동 업데이트
- 변경 사항 하이라이트

**TDD 체크리스트**:
- [ ] 재평가 트리거 테스트
- [ ] UI 업데이트 테스트
- [ ] 하이라이트 효과 테스트

**예상 시간**: 2일

---

### 3.2 애니메이션 시스템 (1주)

#### 3.2.1 TimeMachine
**목표**: 과거 상태 재생 시스템

**기능**:
- 스냅샷 히스토리 저장
- 타임라인 슬라이더
- 이전/다음 상태 이동

**TDD 체크리스트**:
- [ ] 히스토리 저장 테스트
- [ ] 상태 복원 테스트
- [ ] 슬라이더 동작 테스트
- [ ] 메모리 관리 테스트

**예상 시간**: 3일

---

#### 3.2.2 애니메이션 엔진
**목표**: 부드러운 상태 전환

**기능**:
- Tween 애니메이션
- Easing 함수
- 애니메이션 큐

**TDD 체크리스트**:
- [ ] Tween 계산 테스트
- [ ] Easing 함수 테스트
- [ ] 큐 관리 테스트

**예상 시간**: 2일

---

### 3.3 코드-시각화 연동 (4-5일)

#### 3.3.1 양방향 점프
**목표**: 시각화 요소 클릭 → 코드로 점프

**기능**:
- 요소-코드 매핑
- 에디터 커서 이동
- 라인 하이라이트

**TDD 체크리스트**:
- [ ] 매핑 데이터 생성 테스트
- [ ] 점프 액션 테스트
- [ ] 하이라이트 테스트

**예상 시간**: 2일

---

#### 3.3.2 컨텍스트 메뉴
**목표**: 시각화 우클릭 메뉴

**기능**:
- "Show in Code" 옵션
- "Watch Expression" 추가
- "Export as Image" 기능

**TDD 체크리스트**:
- [ ] 메뉴 렌더링 테스트
- [ ] 액션 실행 테스트

**예상 시간**: 1-2일

---

### 3.4 비교 모드 (3-4일)

#### 3.4.1 Side-by-Side 비교
**목표**: 두 상태를 나란히 비교

**기능**:
- 2개 스냅샷 동시 표시
- 차이점 하이라이트
- 동기화 스크롤

**TDD 체크리스트**:
- [ ] 2-pane 레이아웃 테스트
- [ ] 차이점 계산 테스트
- [ ] 동기화 테스트

**예상 시간**: 2-3일

---

### 3.5 커스터마이징 (보너스)

#### 3.5.1 시각화 설정
**목표**: 사용자 맞춤 설정

**기능**:
- 색상 테마
- 레이아웃 옵션
- 애니메이션 속도

**TDD 체크리스트**:
- [ ] 설정 저장/로드 테스트
- [ ] 테마 적용 테스트

**예상 시간**: 2일

---

#### 3.5.2 커스텀 시각화 플러그인
**목표**: 사용자 정의 시각화

**기능**:
- 플러그인 API
- JavaScript 코드 실행
- 샌드박스 환경

**TDD 체크리스트**:
- [ ] API 테스트
- [ ] 샌드박스 격리 테스트

**예상 시간**: 3-4일 (우선순위 낮음)

---

## 🏗️ 아키텍처 변경

### 새로운 컴포넌트

#### Kotlin 플러그인

```kotlin
// 실시간 추적
class StepListener : XDebuggerManagerListener {
    override fun processStopped(debuggerManager: XDebuggerManager, session: XDebugSession) {
        // 변수 변경 감지
        // 시각화 자동 업데이트
    }
}

// 히스토리 관리
class VisualizationHistory {
    private val snapshots = mutableListOf<Snapshot>()

    fun addSnapshot(data: VisualizationData)
    fun goToSnapshot(index: Int)
    fun clear()
}

// 코드 매핑
class CodeMapper {
    fun getCodeLocation(visualElement: String): CodeLocation?
    fun jumpToCode(location: CodeLocation)
}
```

#### React UI

```typescript
// 타임라인
interface TimelineProps {
  snapshots: Snapshot[]
  currentIndex: number
  onSeek: (index: number) => void
}

// 비교 뷰
interface CompareViewProps {
  before: VisualizationData
  after: VisualizationData
}

// 애니메이션 컨트롤
interface AnimationState {
  isPlaying: boolean
  speed: number
  currentFrame: number
}
```

---

## 📊 TDD 전략

### 통합 테스트

Phase 3에서는 **통합 테스트**가 중요합니다.

```kotlin
// E2E 테스트 예시
class StepTrackingE2ETest : LightPlatformCodeInsightTestCase() {
    fun testAutoUpdateOnStepOver() {
        // 1. 테스트 코드 로드
        myFixture.configureByText("Test.java", """
            int[] arr = {3, 1, 2};
            Arrays.sort(arr); // 브레이크포인트
        """)

        // 2. 디버깅 시작
        val session = startDebugging()

        // 3. 시각화 확인
        val panel = getVisualizerPanel()
        assertThat(panel.currentData).isNotNull()

        // 4. Step Over
        session.stepOver()

        // 5. 자동 업데이트 확인
        waitForUpdate()
        assertThat(panel.currentData!!.value).isEqualTo("[1, 2, 3]")
    }
}
```

---

## 🎨 UI/UX 개선

### 타임라인 UI

```
┌─────────────────────────────────────────┐
│  ◀  ▶  ⏸  ⏵  ⏩                        │
│  ═══════●═══════════════════════════   │
│  Step 5 of 20                          │
└─────────────────────────────────────────┘
```

### 비교 뷰 UI

```
┌──────────────┬──────────────┐
│   Before     │    After     │
├──────────────┼──────────────┤
│  [3, 1, 2]   │  [1, 2, 3]   │
│              │              │
│  ▂ ▅ ▃       │  ▂ ▃ ▅       │
│              │              │
└──────────────┴──────────────┘
      ↑ 차이점 하이라이트
```

---

## 📈 성공 지표

### 기능 완성도
- [ ] 실시간 스텝 추적
- [ ] 타임라인 재생
- [ ] 코드-시각화 양방향 점프
- [ ] 비교 모드

### 성능
- [ ] 스텝 이벤트 반응 < 50ms
- [ ] 애니메이션 60fps
- [ ] 히스토리 메모리 < 100MB

### 사용성
- [ ] 직관적인 타임라인
- [ ] 부드러운 애니메이션
- [ ] 빠른 코드 점프

---

## 🚧 리스크 및 대응

### 리스크 1: 성능 저하
**영향**: 높음
**대응**:
- 스냅샷 압축
- Virtual scrolling
- Web Worker 사용

### 리스크 2: 디버거 이벤트 누락
**영향**: 높음
**대응**:
- 이벤트 큐 구현
- 재시도 메커니즘
- 에러 복구 로직

### 리스크 3: 복잡한 상태 관리
**영향**: 중간
**대응**:
- Redux/Zustand 도입
- 명확한 상태 머신
- 테스트 강화

---

## 📚 참고 자료

### 애니메이션
- [React Spring](https://www.react-spring.dev/)
- [Framer Motion](https://www.framer.com/motion/)
- [GreenSock (GSAP)](https://greensock.com/gsap/)

### 상태 관리
- [Zustand](https://github.com/pmndrs/zustand)
- [Redux Toolkit](https://redux-toolkit.js.org/)
- [XState](https://xstate.js.org/)

### 디버거 API
- [XDebuggerManagerListener](https://github.com/JetBrains/intellij-community/blob/master/platform/xdebugger-api/src/com/intellij/xdebugger/XDebuggerManagerListener.java)
- [XDebuggerListener](https://github.com/JetBrains/intellij-community/blob/master/platform/xdebugger-api/src/com/intellij/xdebugger/XDebuggerListener.java)

---

## 🎯 Phase 3 완료 기준

- [ ] 실시간 스텝 추적 동작
- [ ] 타임라인 재생 기능 완성
- [ ] 코드 점프 기능 동작
- [ ] 비교 모드 구현
- [ ] E2E 테스트 최소 10개
- [ ] 성능 벤치마크 통과
- [ ] 사용자 가이드 문서

---

## 🚀 Phase 4 이후 (미래)

### Phase 4: 다국어 지원 및 플러그인 마켓 배포
- Python, JavaScript, C++ 지원
- 플러그인 마켓 배포
- 사용자 피드백 수집

### Phase 5: AI 기반 기능
- 알고리즘 자동 추천
- 성능 병목 자동 감지
- 최적화 제안

---

**Phase 3는 사용자 경험을 크게 향상시킬 것입니다!** ⚡

실시간 추적과 애니메이션으로 알고리즘 이해도를 극대화합니다.
