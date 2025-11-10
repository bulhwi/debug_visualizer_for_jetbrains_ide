# 알고리즘 디버그 시각화 도구 기여 가이드

기여에 관심을 가져주셔서 감사합니다! 이 문서는 프로젝트 기여를 위한 가이드라인과 지침을 제공합니다.

## 시작하기

### 사전 요구사항

- **JDK 17+**: 플러그인 개발에 필요
- **IntelliJ IDEA 2023.1+**: 권장 IDE
- **Gradle 8.0+**: 빌드 도구
- **Node.js 18+**: 시각화 UI 개발용
- **Git**: 버전 관리

### 개발 환경 설정

1. **저장소 클론**
   ```bash
   git clone https://github.com/yourusername/algorithm-debug-visualizer.git
   cd algorithm-debug-visualizer
   ```

2. **플러그인 빌드**
   ```bash
   cd plugin
   ./gradlew build
   ```

3. **UI 의존성 설치**
   ```bash
   cd visualizer-ui
   npm install
   ```

4. **개발 모드로 실행**
   ```bash
   cd plugin
   ./gradlew runIde
   ```

## 프로젝트 구조

```
algorithm-debug-visualizer/
├── plugin/                 # IntelliJ Platform 플러그인
│   ├── src/main/kotlin/   # 플러그인 소스 코드
│   ├── src/main/resources/# 플러그인 리소스
│   └── src/test/          # 플러그인 테스트
├── visualizer-ui/         # 웹 기반 UI
│   ├── src/components/    # React 컴포넌트
│   ├── src/visualizers/   # 시각화 구현
│   └── src/styles/        # CSS/SCSS 스타일
├── data-extraction/       # 언어별 데이터 추출기
└── docs/                  # 문서
```

## 기여 방법

### 버그 리포트

버그를 발견하신 경우, 다음 정보와 함께 이슈를 생성해주세요:
- 명확한 제목과 설명
- 재현 단계
- 예상 동작 vs 실제 동작
- 스크린샷 (해당되는 경우)
- 환경 정보 (OS, IDE 버전, 플러그인 버전)

### 기능 제안

기능 요청을 환영합니다! 다음 사항을 포함해주세요:
- 기능이 이미 존재하거나 계획되어 있는지 확인
- 사용 사례를 명확하게 설명
- 이 기능이 유용한 이유 설명
- 가능하면 예제 제공

### 코드 기여

#### 1. Fork 및 브랜치

```bash
git checkout -b feature/새로운-기능
# 또는
git checkout -b fix/버그-설명
```

#### 2. 코드 스타일

**Kotlin:**
- [Kotlin 코딩 컨벤션](https://kotlinlang.org/docs/coding-conventions.html) 준수
- 들여쓰기는 공백 4칸 사용
- 최대 줄 길이: 120자

**TypeScript:**
- [TypeScript 스타일 가이드](https://google.github.io/styleguide/tsguide.html) 준수
- 들여쓰기는 공백 2칸 사용
- ESLint와 Prettier 사용

**Kotlin 예제:**
```kotlin
class MyClass(
    private val property: String,
    private val anotherProperty: Int
) {
    fun myFunction(): String {
        return "안녕하세요, $property"
    }
}
```

#### 3. 커밋 메시지

[Conventional Commits](https://www.conventionalcommits.org/) 준수:

```
<타입>(<범위>): <제목>

<본문>

<푸터>
```

**타입:**
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 코드 스타일 변경 (포맷팅 등)
- `refactor`: 코드 리팩토링
- `test`: 테스트 추가 또는 수정
- `chore`: 유지보수 작업

**예제:**
```
feat(visualizer): Red-Black 트리 시각화 지원 추가

- 트리 노드에 색상 속성 구현
- 회전 애니메이션 추가
- RB 속성을 처리하도록 트리 렌더러 업데이트

Closes #123
```

#### 4. 테스트

- 새 기능에 대한 단위 테스트 작성
- PR 제출 전 모든 테스트 통과 확인
- 주요 기능에 대한 통합 테스트 추가

**테스트 실행:**
```bash
cd plugin
./gradlew test

cd visualizer-ui
npm test
```

#### 5. 문서화

- 필요한 경우 README.md 업데이트
- 공개 API에 JSDoc/KDoc 주석 추가
- 중요한 변경사항은 architecture.md 업데이트
- 새로운 시각화 타입에 대한 예제 추가

#### 6. Pull Request

- 브랜치를 푸시하고 PR 생성
- PR 템플릿을 완전히 작성
- 관련 이슈 연결
- 메인테이너에게 리뷰 요청
- 리뷰 피드백에 신속히 대응

## 개발 가이드라인

### 새로운 시각화 타입 추가

1. **스키마 정의** `docs/visualization-schema.md`에
2. **데이터 추출기 생성** 적절한 언어 모듈에
3. **렌더러 구현** `visualizer-ui/src/visualizers/`에
4. **시각화 도구 등록** 메인 컨트롤러에
5. **테스트 및 예제 추가**
6. **문서 업데이트**

**예제:**

```kotlin
// 1. 데이터 추출기
class HeapExtractor : VisualizationExtractor {
    override fun canExtract(value: XValue): Boolean {
        return value.type.contains("PriorityQueue") ||
               value.type.contains("Heap")
    }

    override fun extract(value: XValue): VisualizationData {
        // 힙 구조 추출
        return TreeVisualizationData(
            kind = "tree",
            data = buildHeapTree(value)
        )
    }
}
```

```typescript
// 2. 렌더러
export class HeapRenderer implements Renderer {
  render(data: TreeData, container: HTMLElement) {
    // 힙을 위한 D3.js 렌더링 로직
    const svg = d3.select(container).append('svg');
    // ... 시각화 코드
  }
}

// 3. 등록
VisualizerRegistry.register('heap', new HeapRenderer());
```

### 언어 지원 추가

1. **추출기 모듈 생성** `data-extraction/<language>/`에
2. **일반적인 데이터 구조에 대한 데이터 추출 구현**
3. **플러그인 코어에 디버거 통합 추가**
4. **샘플 프로그램으로 테스트 작성**
5. **문서 업데이트**

### 성능 최적화

- 기본적으로 시각화를 1000개 노드로 제한
- 큰 배열에 가상 스크롤링 사용
- 큰 그래프에 페이지네이션 구현
- 빠른 스테핑 중 업데이트 디바운싱
- 핫 패스 프로파일링 및 최적화

### 접근성

- 키보드 네비게이션 작동 확인
- 인터랙티브 요소에 ARIA 레이블 추가
- 고대비 테마 지원
- 스크린 리더로 테스트

## 릴리스 프로세스

1. `plugin.xml`과 `package.json`의 버전 업데이트
2. CHANGELOG.md 업데이트
3. 릴리스 브랜치 생성
4. 전체 테스트 스위트 실행
5. 릴리스 아티팩트 빌드
6. 노트와 함께 GitHub 릴리스 생성
7. JetBrains Marketplace에 게시

## 커뮤니티

- GitHub Discussions에서 토론 참여
- 프로젝트 업데이트 팔로우
- 다른 사용자의 질문에 답변 지원
- 사용 사례와 예제 공유

## 라이선스

기여함으로써, 귀하의 기여가 프로젝트 라이선스(TBD)에 따라 라이선스가 부여되는 것에 동의하게 됩니다.

## 질문이 있으신가요?

질문이 있는 경우:
- 기존 문서 확인
- 종료된 이슈 검색
- 새로운 토론 열기
- 메인테이너에게 연락

기여해 주셔서 감사합니다!
