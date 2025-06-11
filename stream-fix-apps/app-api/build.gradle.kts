plugins {
    id("custom.java")
    id("custom.boot")
    id("custom.asciidoctor")
    id("custom.restdocs")
}

dependencies {
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-service"))
    implementation(project(":stream-fix-commons"))
    implementation(project(":stream-fix-adapters:adapter-http"))
    implementation(project(":stream-fix-adapters:adapter-persistence"))
    implementation(project(":stream-fix-adapters:adapter-redis"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.flywaydb:flyway-core")
}

val appMainClassName = "com.shokoku.streamfix.StreamFixApplication"
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set(appMainClassName)
    archiveClassifier.set("boot")
}