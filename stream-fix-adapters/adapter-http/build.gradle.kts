plugins {
    id("custom.java")
    id("custom.library")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-domain"))
    implementation(project(":stream-fix-commons"))


    implementation(Spring.boot.web)

}