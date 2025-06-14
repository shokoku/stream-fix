plugins {
    id("custom.java-conventions")
    id("custom.spring-conventions")
    id("custom.library-conventions")
    id("custom.test-conventions")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-domain"))

    implementation(Spring.boot.data.jpa)

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    runtimeOnly("com.mysql:mysql-connector-j")

    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")

    implementation("com.querydsl:querydsl-jpa:_:jakarta")
    implementation("com.querydsl:querydsl-core:_")
    annotationProcessor("com.querydsl:querydsl-apt:_:jakarta")
}
tasks.getByName<Jar>("bootJar") {
    enabled = false
}
