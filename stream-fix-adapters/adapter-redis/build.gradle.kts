plugins {
    id("custom.java")
    id("custom.library")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}