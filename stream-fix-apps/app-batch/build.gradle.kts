dependencies{
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-domain"))
    implementation(project(":stream-fix-commons"))
    implementation(project(":stream-fix-adapters:adapter-http"))
    implementation(project(":stream-fix-adapters:adapter-persistence"))
    implementation(project(":stream-fix-adapters:adapter-redis"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-batch")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework:spring-tx")

    runtimeOnly(project(":stream-fix-core:core-service"))

}