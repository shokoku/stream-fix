plugins {
    id("custom.java")
    id("custom.library")
}

dependencies{
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-commons"))
    implementation(project(":stream-fix-core:core-domain"))
    runtimeOnly(project(":stream-fix-adapters:adapter-http"))
    runtimeOnly(project(":stream-fix-adapters:adapter-persistence"))
    runtimeOnly(project(":stream-fix-adapters:adapter-redis"))
    implementation("org.springframework.data:spring-data-commons:_")
    implementation("org.springframework:spring-context:_")

    implementation("io.jsonwebtoken:jjwt-api:_")
    implementation("io.jsonwebtoken:jjwt-impl:_")
    implementation("io.jsonwebtoken:jjwt-jackson:_")
}