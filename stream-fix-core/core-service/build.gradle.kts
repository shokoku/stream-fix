plugins {
    id("custom.java")
    id("custom.library")
    id("custom.spring")
    id("custom.data")
    id("custom.utils")
    id("custom.jwt")
}

dependencies {
    implementation(project(":stream-fix-core:core-domain"))
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-commons"))
    
    implementation("org.springframework.boot:spring-boot-starter-aop:_")
}