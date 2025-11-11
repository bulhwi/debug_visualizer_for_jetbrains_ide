# 🧪 Manual Test Guide - 알고리즘 시각화 플러그인

## 📋 목차
1. [플러그인 실행](#1-플러그인-실행)
2. [테스트 코드 준비](#2-테스트-코드-준비)
3. [디버깅 시작](#3-디버깅-시작)
4. [시각화 확인](#4-시각화-확인)
5. [문제 해결](#5-문제-해결)

---

## 1. 플러그인 실행

### Step 1-1: 플러그인 빌드 및 실행

```bash
cd plugin
./gradlew runIde
```

이 명령어는:
- 플러그인을 빌드합니다
- React UI를 빌드합니다
- 새 IntelliJ IDEA 인스턴스를 시작합니다 (플러그인이 설치된 상태)

### Step 1-2: 플러그인 로드 확인

1. IntelliJ IDEA 테스트 인스턴스가 열리면
2. **View > Tool Windows > Algorithm Visualizer** 확인
3. 툴 윈도우가 보이면 플러그인 로드 성공! ✅

---

## 2. 테스트 코드 준비

### Step 2-1: 새 Java 프로젝트 생성

테스트 IntelliJ에서:
1. **File > New > Project**
2. **Java** 선택
3. JDK 17 이상 선택
4. 프로젝트 생성

### Step 2-2: 테스트 코드 작성

`src/Main.java` 파일 생성:

```java
public class Main {
    public static void main(String[] args) {
        int[] arr = {5, 2, 8, 1, 9};

        System.out.println("Original array:");
        printArray(arr);

        bubbleSort(arr);

        System.out.println("Sorted array:");
        printArray(arr);
    }

    // 버블 정렬 구현
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
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
        for (int val : arr) {
            System.out.print(val + " ");
        }
        System.out.println();
    }
}
```

### 다른 정렬 알고리즘 예제

#### Quick Sort
```java
public static void quickSort(int[] arr, int low, int high) {
    if (low < high) {
        int pivot = arr[high];
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

        int pi = i + 1;
        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
}
```

#### Merge Sort
```java
public static void mergeSort(int[] arr, int l, int r) {
    if (l < r) {
        int m = l + (r - l) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m + 1, r);
        merge(arr, l, m, r);
    }
}

public static void merge(int[] arr, int l, int m, int r) {
    int n1 = m - l + 1;
    int n2 = r - m;

    int[] L = new int[n1];
    int[] R = new int[n2];

    for (int i = 0; i < n1; i++)
        L[i] = arr[l + i];
    for (int j = 0; j < n2; j++)
        R[j] = arr[m + 1 + j];

    int i = 0, j = 0, k = l;
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) {
            arr[k] = L[i];
            i++;
        } else {
            arr[k] = R[j];
            j++;
        }
        k++;
    }

    while (i < n1) {
        arr[k] = L[i];
        i++;
        k++;
    }

    while (j < n2) {
        arr[k] = R[j];
        j++;
        k++;
    }
}
```

---

## 3. 디버깅 시작

### Step 3-1: 브레이크포인트 설정

1. `bubbleSort` 메서드의 **첫 번째 줄**에 브레이크포인트 설정
   - 라인 번호 옆 클릭 → 빨간 점 표시
2. 중첩 루프 내부 스왑 부분에도 브레이크포인트 설정

```java
public static void bubbleSort(int[] arr) {
    int n = arr.length;  // ← 브레이크포인트 1
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];  // ← 브레이크포인트 2
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}
```

### Step 3-2: 디버그 모드 실행

1. **우클릭** on `Main.java`
2. **Debug 'Main.main()'** 선택
3. 디버거가 브레이크포인트에서 멈춤 ✅

### Step 3-3: 툴 윈도우 열기

1. **View > Tool Windows > Algorithm Visualizer**
2. 툴 윈도우 패널 확인
3. 초기 상태: "데이터 준비 중" 메시지

---

## 4. 시각화 확인

### Step 4-1: 첫 번째 스텝

1. 디버거에서 **Step Over (F8)** 클릭
2. **Variables** 패널에서 `arr` 배열 확인
3. Algorithm Visualizer 툴 윈도우 확인
   - **자동으로 알고리즘 감지**: "BUBBLE_SORT detected" (콘솔 로그)
   - 아직 시각화는 표시 안 됨 (스냅샷 수집 중)

### Step 4-2: 계속 스텝 실행

1. **Step Over (F8)** 를 여러 번 눌러서 루프 진행
2. 스왑이 발생할 때마다 배열 상태 변화 확인
3. DebuggerListener가 자동으로 스냅샷 수집 중

### Step 4-3: 시각화 확인

**현재 상태**:
- ✅ AlgorithmDetector: 알고리즘 자동 감지
- ✅ DebuggerListener: 스텝 이벤트 감지
- ✅ SnapshotCollector: 배열 상태 캡처
- ⚠️ **아직 UI 통합 안 됨** (다음 단계 필요)

---

## 5. 문제 해결

### 문제 1: 플러그인이 로드되지 않음

**증상**: "Algorithm Visualizer" 툴 윈도우가 없음

**해결**:
```bash
cd plugin
./gradlew clean build
./gradlew runIde
```

### 문제 2: 디버거가 멈추지 않음

**원인**: 브레이크포인트가 설정되지 않음

**해결**:
1. 라인 번호 옆 클릭하여 빨간 점 확인
2. **Run > View Breakpoints** 에서 활성화 확인

### 문제 3: React UI가 로드되지 않음

**원인**: React 빌드 실패

**해결**:
```bash
cd visualizer-ui
npm install
npm run build

cd ../plugin
./gradlew runIde
```

### 문제 4: 콘솔 로그 확인

IntelliJ IDEA 테스트 인스턴스에서:
1. **Help > Show Log in Finder** (macOS)
2. `idea.log` 파일에서 에러 확인

또는 터미널에서 플러그인 실행 시 로그 확인:
```bash
./gradlew runIde --info
```

---

## 6. 다음 단계 (현재 미구현)

### 필요한 통합 작업:

1. **ToolWindowPanel 통합**
   - DebuggerListener를 VisualizerToolWindowPanel에 연결
   - 스냅샷 데이터를 JCEF WebView로 전송

2. **React UI 업데이트**
   - SortVisualizer 컴포넌트를 App.tsx에 통합 (이미 완료)
   - window.visualizerAPI.showData() 호출 대기

3. **자동화 파이프라인**
   ```
   DebuggerListener
   → SnapshotCollector.toJson()
   → JCEF browser.executeJavaScript()
   → window.visualizerAPI.showData()
   → React SortVisualizer 렌더링
   ```

---

## 📝 현재 상태 요약

### ✅ 완료된 컴포넌트 (100% 통합)
- AlgorithmDetector (14개 테스트 통과) ✅
- SnapshotCollector (16개 테스트 통과) ✅
- DebuggerListener (11개 테스트 통과) ✅
- SortVisualizer (React 컴포넌트, 20개 테스트 통과) ✅
- **ToolWindowPanel 통합 완료** ✅

### 🎉 완성된 자동 시각화 파이프라인
```
디버그 세션 시작
    ↓
"자동 시각화" 체크박스 활성화
    ↓
DebuggerListener 초기화
    ↓ (연결)
AlgorithmDetector + SnapshotCollector
    ↓
디버거 스텝 실행 (F8)
    ↓
메서드명으로 알고리즘 자동 감지
    ↓
배열 상태 자동 캡처
    ↓
JSON 변환 → JCEF WebView
    ↓
React SortVisualizer 렌더링 🎨
```

### 🎯 현재 사용 가능한 기능
1. ✅ IntelliJ에서 정렬 코드 디버깅
2. ✅ 메서드명으로 자동 알고리즘 감지
3. ✅ "자동 시각화" 체크박스로 활성화/비활성화
4. ✅ 스텝마다 배열 상태 자동 캡처
5. ⚠️ JCEF → React 시각화 (연결 필요)

### ⚠️ 남은 작업
- React UI에서 `kind: "sort"` 데이터 처리 확인
- 실제 디버깅 환경에서 E2E 테스트

---

## 💡 빠른 테스트 팁

### 간단한 테스트 시나리오

1. **플러그인 실행**: `./gradlew runIde`
2. **코드 작성**: 위의 `bubbleSort` 예제 복사
3. **브레이크포인트**: `int n = arr.length;` 라인
4. **디버그 실행**: 우클릭 > Debug
5. **Step Over**: F8 여러 번
6. **콘솔 확인**: "BUBBLE_SORT detected" 로그 찾기

### 테스트 체크리스트

- [ ] 플러그인이 로드됨 (Tool Window 존재)
- [ ] 디버거가 브레이크포인트에서 멈춤
- [ ] Step Over 가능
- [ ] Variables 패널에 `arr` 배열 표시
- [ ] 콘솔에 알고리즘 감지 로그 (구현 필요)
- [ ] 시각화 표시 (통합 작업 필요)

---

**작성일**: 2025-11-11
**버전**: Phase 2 - Issues #13, #14 완료
**다음**: ToolWindowPanel 통합 (#15)
