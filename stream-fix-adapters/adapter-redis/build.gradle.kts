plugins {
    id("custom.java-conventions")
    id("custom.spring-conventions")
    id("custom.library-conventions")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-domain"))

    implementation(Spring.boot.data.redis)
}

tasks.getByName<Jar>("bootJar") {
    enabled = false
}
