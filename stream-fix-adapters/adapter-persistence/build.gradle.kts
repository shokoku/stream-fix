plugins {
    id("custom.java-conventions")
    id("custom.spring-conventions")
    id("custom.library-conventions")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-domain"))

    implementation(Spring.boot.data.jpa)

    implementation("org.flywaydb:flyway-core:_")
    implementation("org.flywaydb:flyway-mysql:_")
    implementation("com.querydsl:querydsl-jpa:_:jakarta")
    implementation("com.querydsl:querydsl-core:_")

    annotationProcessor("com.querydsl:querydsl-apt:_:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api:_")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api:_")

    runtimeOnly("com.mysql:mysql-connector-j:_")
}
tasks.getByName<Jar>("bootJar") {
    enabled = false
}
