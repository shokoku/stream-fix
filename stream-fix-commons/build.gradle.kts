plugins {
    id("custom.java")
    id("custom.library")
    id("custom.spring")
    id("custom.utils")
}

dependencies {
    implementation(project(":stream-fix-core:core-domain"))
}