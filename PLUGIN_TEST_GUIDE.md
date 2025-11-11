# 🔌 플러그인 실제 동작 테스트 가이드

**IntelliJ에서 디버거로 정렬 시각화 테스트하기**

---

## 🎯 목표

실제 IntelliJ 플러그인을 실행하고, Java 코드를 디버깅하면서 정렬 알고리즘 시각화를 확인합니다.

---

## 📋 준비사항

1. IntelliJ IDEA 설치
2. JDK 17 설치
3. 이 프로젝트가 IntelliJ에서 열려 있어야 함

---

## 🚀 1단계: 플러그인 실행

### 방법 1: Gradle 태스크 사용

```bash
./gradlew runIde
```

### 방법 2: IntelliJ에서 실행

1. IntelliJ에서 이 프로젝트 열기
2. Gradle 탭 열기 (오른쪽 사이드바)
3. `Tasks` → `intellij` → `runIde` 더블클릭

**실행되면**: 새 IntelliJ IDEA 창이 열림 (플러그인이 설치된 테스트 IDE)

---

## 🧪 2단계: 테스트 Java 프로젝트 생성

### 새 창(테스트 IDE)에서:

1. **File** → **New** → **Project**
2. **Java** 선택, JDK 17 설정
3. 프로젝트 이름: `SortVisualizerTest`
4. **Create** 클릭

### 테스트 Java 파일 생성

`src/Main.java` 생성:

```java
public class Main {
    public static void main(String[] args) {
        // 테스트할 배열
        int[] arr = {5, 2, 8, 1, 9};

        System.out.println("정렬 전:");
        printArray(arr);

        bubbleSort(arr);

        System.out.println("\n정렬 후:");
        printArray(arr);
    }

    // 버블 정렬 구현
    public static void bubbleSort(int[] arr) {
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // ⭐ 여기에 브레이크포인트 설정!
                if (arr[j] > arr[j + 1]) {
                    // 스왑
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static void printArray(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }
}
```

---

## 🐛 3단계: 디버깅 시작

### 1. 브레이크포인트 설정

`if (arr[j] > arr[j + 1])` 줄에 브레이크포인트 설정:
- 줄 번호 왼쪽 클릭 → 빨간 점 생성

### 2. 디버그 모드 실행

- **Run** → **Debug 'Main'** 클릭
- 또는 **Shift + F9**

### 3. 툴 윈도우 열기

디버거가 브레이크포인트에서 멈추면:
1. **View** → **Tool Windows** → **Algorithm Visualizer** 클릭
2. 또는 하단/측면에서 "Algorithm Visualizer" 탭 찾기

---

## 📊 4단계: 정렬 시각화 확인

### 현재 상태 (Phase 2 #12 완료)

아직 **자동 통합은 안됨**이지만, 수동으로 테스트 가능:

#### 방법 1: Kotlin 코드로 JSON 생성

테스트 IDE의 **Scratch File** 생성:
1. **File** → **New** → **Scratch File** → **Kotlin**
2. 다음 코드 붙여넣기:

```kotlin
import com.github.algorithmvisualizer.collectors.SnapshotCollector

val collector = SnapshotCollector()
collector.setAlgorithm("bubble")
collector.setMetadata("java", "arr", "int[]")

// 초기 배열
val arr = intArrayOf(5, 2, 8, 1, 9)
collector.captureSnapshot(arr, action = "compare", description = "초기 배열")

// 비교
collector.captureSnapshot(arr, comparing = intArrayOf(0, 1), action = "compare", description = "arr[0]=5와 arr[1]=2 비교")

// 스왑
collector.captureSnapshot(intArrayOf(2, 5, 8, 1, 9), swapping = intArrayOf(0, 1), action = "swap", description = "교환")

// ... 더 많은 단계 추가 ...

// JSON 출력
println(collector.toJson())
```

3. **Run** 버튼 클릭
4. JSON 복사

#### 방법 2: 브라우저 개발자 도구에서 직접 주입

1. 플러그인 툴 윈도우의 웹뷰 영역에서 **오른쪽 클릭** → **Inspect** (개발자 도구 열기)
2. **Console** 탭에서 다음 코드 실행:

```javascript
window.visualizerAPI.showData(JSON.stringify({
  "kind": "sort",
  "timestamp": Date.now(),
  "metadata": {
    "language": "java",
    "expression": "arr",
    "type": "int[]"
  },
  "data": {
    "algorithm": "bubble",
    "snapshots": [
      {
        "array": [5, 2, 8, 1, 9],
        "action": "compare",
        "description": "초기 배열"
      },
      {
        "array": [5, 2, 8, 1, 9],
        "comparing": [0, 1],
        "action": "compare",
        "description": "arr[0]=5와 arr[1]=2 비교"
      },
      {
        "array": [2, 5, 8, 1, 9],
        "swapping": [0, 1],
        "action": "swap",
        "description": "교환"
      },
      {
        "array": [2, 5, 1, 8, 9],
        "swapping": [2, 3],
        "action": "swap"
      },
      {
        "array": [1, 2, 5, 8, 9],
        "sorted": [0, 1, 2, 3, 4],
        "action": "sorted",
        "description": "정렬 완료!"
      }
    ]
  }
}))
```

**결과**: SortVisualizer가 표시되고 단계별 애니메이션을 볼 수 있음!

---

## 🎮 5단계: 시각화 조작

### SortVisualizer UI 사용

- **Next ▶▶**: 다음 정렬 단계로 이동
- **▶ Play**: 자동 재생 시작
- **⏸ Pause**: 일시정지
- **◀◀ Previous**: 이전 단계로 되돌리기
- **Speed**: 속도 조절 (0.5x ~ 4x)

### 색상 의미 확인

- **회청색**: 기본 상태
- **주황색**: 비교 중
- **빨간색**: 교환 중
- **초록색**: 정렬 완료

---

## 🔧 트러블슈팅

### 문제 1: "Algorithm Visualizer" 탭이 안보임

**해결**:
1. **View** → **Tool Windows** → **Algorithm Visualizer** 확인
2. 플러그인이 제대로 빌드되었는지 확인:
   ```bash
   ./gradlew build
   ```
3. 테스트 IDE 재시작

### 문제 2: 웹뷰가 비어있음

**해결**:
1. React UI가 빌드되었는지 확인:
   ```bash
   cd visualizer-ui
   npm run build
   ```
2. `./gradlew build` 다시 실행
3. `runIde` 재실행

### 문제 3: `window.visualizerAPI`가 정의되지 않음

**해결**:
1. 웹뷰가 완전히 로드될 때까지 기다리기 (3-5초)
2. Console에서 확인:
   ```javascript
   console.log(window.visualizerAPI)
   ```
3. `undefined`이면 웹뷰 재로드

---

## 📝 다음 단계 (자동 통합)

현재는 수동으로 JSON을 주입해야 하지만, Phase 2 다음 작업들:

### #13: 정렬 알고리즘 감지기
- PSI로 코드 분석
- 버블/퀵/병합 정렬 자동 감지

### #14: 스냅샷 자동 수집기
- 디버거 스텝 이벤트 리스닝
- 자동으로 SnapshotCollector 호출
- 실시간 시각화 업데이트

**완성되면**: 브레이크포인트에서 멈출 때마다 자동으로 시각화!

---

## 💡 추가 테스트 아이디어

### 1. 다른 정렬 알고리즘 테스트

```java
// 퀵소트
public static void quickSort(int[] arr, int low, int high) {
    if (low < high) {
        int pi = partition(arr, low, high);
        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
}

public static int partition(int[] arr, int low, int high) {
    int pivot = arr[high];  // ⭐ 브레이크포인트
    int i = (low - 1);

    for (int j = low; j < high; j++) {
        if (arr[j] < pivot) {
            i++;
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    int temp = arr[i + 1];
    arr[i + 1] = arr[high];
    arr[high] = temp;

    return i + 1;
}
```

### 2. 큰 배열 테스트

```java
int[] largeArr = {64, 34, 25, 12, 22, 11, 90, 88, 45, 50, 33, 17, 10, 5, 78, 65};
bubbleSort(largeArr);
```

### 3. 이미 정렬된 배열

```java
int[] sortedArr = {1, 2, 3, 4, 5};
bubbleSort(sortedArr);  // 스왑이 일어나지 않음
```

---

## 🎥 스크린샷/동영상 녹화

### 테스트 결과 기록

1. **스크린샷**: Cmd+Shift+4 (Mac) / Win+Shift+S (Windows)
2. **화면 녹화**: QuickTime Player (Mac) / Xbox Game Bar (Windows)
3. GitHub 이슈/PR에 첨부

---

## 📚 관련 문서

- **데모 가이드**: `QUICK_TEST.md` (브라우저 전용 테스트)
- **상세 데모 가이드**: `docs/DEMO_GUIDE.md`
- **스키마 문서**: `docs/sort-visualization-schema.md`
- **테스트 가이드**: `docs/TESTING.md`

---

## 🐛 버그 리포트

테스트 중 문제 발견 시:
1. 재현 단계 기록
2. 스크린샷/로그 첨부
3. GitHub 이슈 생성: https://github.com/bulhwi/debug_visualizer_for_jetbrains_ide/issues

---

**즐거운 테스트 되세요!** 🎉

궁금한 점이 있으면 이슈로 남겨주세요!
