dependencies{
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-commons"))
    runtimeOnly(project(":stream-fix-adapters:adapter-http"))
    runtimeOnly(project(":stream-fix-adapters:adapter-persistence"))
    runtimeOnly(project(":stream-fix-adapters:adapter-redis"))
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-context")

}