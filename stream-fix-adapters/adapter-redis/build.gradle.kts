plugins {
    id("custom.java")
    id("custom.library")
    id("custom.spring")
    id("custom.redis")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-domain"))
}