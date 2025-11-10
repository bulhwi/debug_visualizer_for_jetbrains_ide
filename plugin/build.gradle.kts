plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.github.algorithmvisualizer"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

// IntelliJ Platform 설정
intellij {
    version.set("2023.2.5")
    type.set("IC") // IntelliJ IDEA Community Edition

    plugins.set(listOf(
        "com.intellij.java",
        "org.jetbrains.kotlin"
    ))
}

tasks {
    // React UI 빌드 및 복사
    val buildReactUI by registering(Exec::class) {
        workingDir = file("../visualizer-ui")
        commandLine("npm", "run", "build")
    }

    val copyReactBuild by registering(Copy::class) {
        dependsOn(buildReactUI)
        from("../visualizer-ui/dist")
        into("src/main/resources/web")
    }

    // processResources가 React UI를 포함하도록 설정
    processResources {
        dependsOn(copyReactBuild)
    }

    // JVM 타겟 버전 설정
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    runIde {
        jvmArgs = listOf("-Xmx2048m", "-Xms512m")
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("241.*")

        pluginDescription.set("""
            <![CDATA[
            <h1>Algorithm Debug Visualizer</h1>
            <p>JetBrains IDE를 위한 강력한 알고리즘 디버깅 시각화 도구입니다.</p>

            <h2>주요 기능</h2>
            <ul>
                <li>배열, 리스트를 막대 그래프로 시각화</li>
                <li>트리 구조를 계층적 다이어그램으로 시각화</li>
                <li>그래프 알고리즘의 실행 과정을 시각적으로 표현</li>
                <li>DP 테이블 시각화</li>
                <li>실시간 디버거 통합</li>
            </ul>

            <h2>지원 언어</h2>
            <ul>
                <li>Java</li>
                <li>Kotlin</li>
                <li>Python (예정)</li>
                <li>JavaScript/TypeScript (예정)</li>
            </ul>
            ]]>
        """.trimIndent())

        changeNotes.set("""
            <![CDATA[
            <h3>0.1.0-SNAPSHOT</h3>
            <ul>
                <li>초기 프로젝트 설정</li>
                <li>플러그인 기본 구조 구축</li>
            </ul>
            ]]>
        """.trimIndent())
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    test {
        useJUnitPlatform()

        // 테스트 결과 리포트 설정
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = false
            showExceptions = true
            showCauses = true
            showStackTraces = true

            // 각 테스트 실행 후 결과 출력
            afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
                if (desc.parent == null) {
                    println("\n테스트 결과: ${result.resultType}")
                    println("테스트 실행: ${result.testCount}개")
                    println("성공: ${result.successfulTestCount}개")
                    println("실패: ${result.failedTestCount}개")
                    println("건너뜀: ${result.skippedTestCount}개")
                    println("소요 시간: ${result.endTime - result.startTime}ms\n")
                }
            }))
        }

        // 테스트 실패 시 계속 실행
        ignoreFailures = false

        // 최대 병렬 실행
        maxParallelForks = Runtime.getRuntime().availableProcessors().div(2).coerceAtLeast(1)
    }
}
