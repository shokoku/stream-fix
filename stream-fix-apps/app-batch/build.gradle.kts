plugins {
    id("custom.java-conventions")
    id("custom.spring-conventions")
    id("custom.test-conventions")
}

dependencies {
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-domain"))
    implementation(project(":stream-fix-commons"))
    implementation(project(":stream-fix-adapters:adapter-persistence"))
    implementation(project(":stream-fix-adapters:adapter-redis"))

    implementation(Spring.boot.batch)

    runtimeOnly(project(":stream-fix-core:core-service"))
}
