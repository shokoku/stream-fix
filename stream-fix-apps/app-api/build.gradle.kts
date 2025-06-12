plugins {
    id("custom.java")
    id("custom.boot")
    id("custom.spring")
    id("custom.web")
    id("custom.security")
    id("custom.jwt")
    id("custom.mapstruct")
    id("custom.utils")
    id("custom.test")
    id("custom.spotless")
}

dependencies {
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-service"))
    implementation(project(":stream-fix-commons"))
    implementation(project(":stream-fix-adapters:adapter-http"))
    implementation(project(":stream-fix-adapters:adapter-persistence"))
    implementation(project(":stream-fix-adapters:adapter-redis"))

    // API 전용 의존성
    implementation("org.springframework.boot:spring-boot-starter-actuator:_")
    implementation("org.springframework.boot:spring-boot-starter-aop:_")
    implementation("org.springframework.security:spring-security-oauth2-client:_")
}

val appMainClassName = "com.shokoku.streamfix.StreamFixApplication"
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set(appMainClassName)
    archiveClassifier.set("boot")
}
