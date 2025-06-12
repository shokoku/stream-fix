plugins {
    id("custom.java")
    id("custom.library")
    id("custom.spring")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-domain"))
    implementation("org.springframework.boot:spring-boot-starter-webflux:_")
}