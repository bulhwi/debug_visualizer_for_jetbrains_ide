#!/bin/bash

# Phase 1 이슈 생성 스크립트

cd "$(dirname "$0")/.." || exit

# Issue 1
gh issue create \
  --repo bulhwi/debug_visualizer_for_jetbrains_ide \
  --title "[Phase 1] 플러그인 프로젝트 초기화 및 기본 구조 설정" \
  --label "phase-1,infrastructure,priority-high" \
  --body-file - <<'EOF'
## 작업 설명
IntelliJ Platform SDK를 사용하여 플러그인 프로젝트를 초기화하고 기본 구조를 설정합니다.

## 체크리스트
- [ ] Gradle 기반 플러그인 프로젝트 생성
- [ ] plugin.xml 설정 (플러그인 메타데이터)
- [ ] 기본 디렉토리 구조 생성 (src/main/kotlin, src/main/resources)
- [ ] build.gradle.kts 설정 (IntelliJ Platform Gradle Plugin)
- [ ] JDK 17+ 설정
- [ ] 기본 플러그인 클래스 생성 (AlgorithmVisualizerPlugin)
- [ ] README에 빌드 및 실행 방법 추가

## 수용 기준
- `./gradlew build`가 성공적으로 실행됨
- `./gradlew runIde`로 테스트 IDE 인스턴스가 실행됨
- 플러그인이 IDE의 플러그인 목록에 나타남

## 예상 소요 시간
2-3일

## 참고 자료
- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- [IntelliJ Platform SDK Docs](https://plugins.jetbrains.com/docs/intellij/)
- docs/architecture.md
EOF

echo "✅ Issue 1 created"

# Issue 2
gh issue create \
  --repo bulhwi/debug_visualizer_for_jetbrains_ide \
  --title "[Phase 1] 도구 윈도우 (Tool Window) 구현" \
  --label "phase-1,ui,priority-high" \
  --body-file - <<'EOF'
## 작업 설명
시각화를 표시할 도구 윈도우를 구현합니다.

## 체크리스트
- [ ] VisualizerToolWindowFactory 클래스 생성
- [ ] plugin.xml에 도구 윈도우 등록
- [ ] 기본 UI 패널 생성 (Swing/JavaFX)
- [ ] 도구 윈도우 위치 설정 (하단 또는 우측)
- [ ] 도구 윈도우 아이콘 추가
- [ ] 표현식 입력 필드 UI 구현
- [ ] "Evaluate" 버튼 구현

## 수용 기준
- IDE 실행 시 "Algorithm Visualizer" 도구 윈도우가 보임
- 도구 윈도우를 열고 닫을 수 있음
- 도구 윈도우 크기 조절이 가능함
- 표현식 입력 필드와 버튼이 UI에 표시됨

## 예상 소요 시간
2-3일

## 참고 자료
- [Tool Windows - IntelliJ Platform](https://plugins.jetbrains.com/docs/intellij/tool-windows.html)
- docs/PRD.md - UI 섹션
EOF

echo "✅ Issue 2 created"

echo "✨ 총 11개 이슈 생성 완료!"
