plugins {
    id("custom.java")
    id("custom.library")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(Spring.boot.data.redis)
}