dependencies {
    val testImplementation by configurations
    val testRuntimeOnly by configurations
    
    // JUnit 5 (Jupiter)
    testImplementation("org.springframework.boot:spring-boot-starter-test:_")
    
    // Spring Boot Test
    testImplementation("org.springframework.boot:spring-boot-test:_")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:_")
    
    // Spring Security Test
    testImplementation("org.springframework.security:spring-security-test:_")
    
    // H2 Database (In-Memory Database for Test)
    testRuntimeOnly("com.h2database:h2:_")
    
    // TestContainers (Database Integration Test)
    testImplementation("org.testcontainers:junit-jupiter:_")
    testImplementation("org.testcontainers:mysql:_")
    testRuntimeOnly("com.mysql:mysql-connector-j:_")
    
    // Mockito
    testImplementation("org.mockito:mockito-core:_")
    testImplementation("org.mockito:mockito-junit-jupiter:_")
    
    // AssertJ
    testImplementation("org.assertj:assertj-core:_")
    
    // Embedded Redis (for Redis Integration Test)
    testImplementation("it.ozimov:embedded-redis:_")
    
    // WireMock (for External API Test)
    testImplementation("org.wiremock:wiremock-standalone:_")
    
    // Fixture Monkey (Test Data Builder)
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter:_")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jackson:_")
}

tasks.withType<Test> {
    useJUnitPlatform()
    
    // 테스트 결과 출력 설정
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    
    // 테스트 메모리 설정
    jvmArgs("-XX:+UseG1GC", "-Xmx1g")
    
    // 테스트 병렬 실행
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
}
