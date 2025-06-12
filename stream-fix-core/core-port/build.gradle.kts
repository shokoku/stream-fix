plugins {
    id("custom.java")
    id("custom.library")
}

dependencies {
    implementation(project(":stream-fix-core:core-domain"))
}