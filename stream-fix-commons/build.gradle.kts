plugins {
    id("custom.java")
    id("custom.library")
    id("custom.spring")
    id("custom.utils")
    id("custom.spotless")
}

dependencies {
    implementation(project(":stream-fix-core:core-domain"))
}
