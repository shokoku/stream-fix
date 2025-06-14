plugins {
    id("custom.java-conventions")
    id("custom.spring-conventions")
}

dependencies {
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-core:core-service"))
    implementation(project(":stream-fix-commons"))
    implementation(project(":stream-fix-adapters:adapter-http"))
    implementation(project(":stream-fix-adapters:adapter-persistence"))
    implementation(project(":stream-fix-adapters:adapter-redis"))

    implementation(Spring.boot.web)
    implementation(Spring.boot.security)

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.apache.commons:commons-lang3:_")
    implementation("org.apache.commons:commons-collections4:_")
}

val appMainClassName = "com.shokoku.streamfix.StreamFixApplication"
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set(appMainClassName)
    archiveClassifier.set("boot")
}
