plugins {
    id("custom.java-conventions")
    id("custom.spring-conventions")
    id("custom.library-conventions")
    id("custom.test-conventions")
}

dependencies {
    implementation(project(":stream-fix-core:core-domain"))
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-usecase"))
    implementation(project(":stream-fix-commons"))

    implementation(Spring.boot.data.jpa)

    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("io.jsonwebtoken:jjwt-api:_")
    implementation("io.jsonwebtoken:jjwt-impl:_")
    implementation("io.jsonwebtoken:jjwt-jackson:_")
    implementation("org.apache.commons:commons-lang3:_")
    implementation("org.apache.commons:commons-collections4:_")
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}
