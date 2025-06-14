plugins {
    id("custom.java-conventions")
    id("custom.library-conventions")
}

dependencies {
    implementation(project(":stream-fix-core:core-domain"))
    implementation(project(":stream-fix-core:core-port"))
}
