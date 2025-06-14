// 전체 모듈 커버리지 리포트 통합 태스크
tasks.register<JacocoReport>("jacocoAggregateReport") {
    group = "verification"
    description = "Generate aggregate Jacoco coverage report for all modules"
    
    dependsOn(subprojects.map { it.tasks.withType<Test>() })
    mustRunAfter(subprojects.map { it.tasks.withType<Test>() })
    
    executionData.setFrom(
        subprojects.mapNotNull { subproject ->
            val execFile = subproject.layout.buildDirectory.file("jacoco/test.exec").orNull?.asFile
            if (execFile?.exists() == true) execFile else null
        }
    )
    
    sourceDirectories.setFrom(
        subprojects.flatMap { subproject ->
            listOf(
                subproject.layout.projectDirectory.dir("src/main/java"),
                subproject.layout.projectDirectory.dir("src/main/kotlin")
            ).filter { it.asFile.exists() }
        }
    )
    
    classDirectories.setFrom(
        subprojects.flatMap { subproject ->
            listOf(
                subproject.layout.buildDirectory.dir("classes/java/main"),
                subproject.layout.buildDirectory.dir("classes/kotlin/main")
            ).map { classDir ->
                subproject.fileTree(classDir) {
                    exclude(
                        // 헥사고날 아키텍처에서 제외할 패턴들
                        "**/config/**",
                        "**/dto/**", 
                        "**/entity/**",
                        "**/exception/**",
                        "**/Application*",
                        "**/*Application*",
                        "**/*Config*",
                        "**/Q*", // QueryDSL 생성 클래스
                        "**/adapter/**/*Adapter*", // 어댑터 인터페이스는 제외하고 구현체만
                        "**/port/**", // 포트 인터페이스는 제외
                    )
                }
            }
        }
    )
    
    reports {
        html.required.set(EnvUtils.isLocal())
        xml.required.set(EnvUtils.isCI())
        csv.required.set(false)
        
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/aggregate"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/aggregate/jacocoTestReport.xml"))
    }
    
    doFirst {
        logger.lifecycle("Generating aggregate Jacoco report for ${subprojects.size} modules")
    }
    
    doLast {
        if (EnvUtils.isLocal()) {
            logger.lifecycle("Aggregate Jacoco HTML report generated at: ${reports.html.outputLocation.get().asFile}")
        }
    }
}

// 전체 커버리지 검증
tasks.register<JacocoCoverageVerification>("jacocoAggregateVerification") {
    group = "verification"
    description = "Verify aggregate Jacoco coverage for all modules"
    
    dependsOn(tasks.named("jacocoAggregateReport"))
    
    executionData.setFrom(
        subprojects.mapNotNull { subproject ->
            val execFile = subproject.layout.buildDirectory.file("jacoco/test.exec").orNull?.asFile
            if (execFile?.exists() == true) execFile else null
        }
    )
    
    sourceDirectories.setFrom(
        subprojects.flatMap { subproject ->
            listOf(
                subproject.layout.projectDirectory.dir("src/main/java"),
                subproject.layout.projectDirectory.dir("src/main/kotlin")
            ).filter { it.asFile.exists() }
        }
    )
    
    classDirectories.setFrom(
        subprojects.flatMap { subproject ->
            listOf(
                subproject.layout.buildDirectory.dir("classes/java/main"),
                subproject.layout.buildDirectory.dir("classes/kotlin/main")
            ).map { classDir ->
                subproject.fileTree(classDir) {
                    exclude(
                        "**/config/**",
                        "**/dto/**", 
                        "**/entity/**",
                        "**/exception/**",
                        "**/Application*",
                        "**/*Application*",
                        "**/*Config*",
                        "**/Q*",
                        "**/adapter/**/*Adapter*",
                        "**/port/**",
                    )
                }
            }
        }
    )
    
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal() // 전체 70% 커버리지
            }
        }
        
        rule {
            element = "BUNDLE"
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.60".toBigDecimal() // 브랜치 60% 커버리지
            }
        }
        
        // 개별 패키지별 검증 (헥사고날 레이어별)
        rule {
            element = "PACKAGE"
            includes = listOf("**/domain/**")
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal() // 도메인 레이어는 더 높은 커버리지
            }
        }
        
        rule {
            element = "PACKAGE"
            includes = listOf("**/usecase/**", "**/service/**")
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal() // 유스케이스/서비스 레이어
            }
        }
    }
    
    doFirst {
        logger.lifecycle("Verifying aggregate Jacoco coverage thresholds")
    }
}

// 편의 태스크: 모든 테스트 실행 후 통합 리포트 생성
tasks.register("testWithCoverage") {
    group = "verification"
    description = "Run all tests and generate aggregate coverage report"
    
    dependsOn(subprojects.map { it.tasks.withType<Test>() })
    finalizedBy(tasks.named("jacocoAggregateReport"))
    
    doLast {
        logger.lifecycle("All tests completed. Aggregate coverage report generated.")
    }
}