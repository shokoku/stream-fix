plugins {
    id("custom.java")
    id("custom.library")
    id("custom.spring")
    id("custom.batch")
    id("custom.spotless")
}

dependencies {
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-domain"))
    implementation(project(":stream-fix-commons"))
    implementation(project(":stream-fix-adapters:adapter-persistence"))
    implementation(project(":stream-fix-adapters:adapter-redis"))

    runtimeOnly(project(":stream-fix-core:core-service"))
}
