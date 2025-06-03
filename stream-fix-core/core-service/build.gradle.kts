dependencies{
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-port"))
    runtimeOnly(project(":stream-fix-adapters:adapter-http"))

}