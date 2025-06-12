plugins {
    id("custom.java")
    id("custom.library")
    id("custom.spring")
    id("custom.data")
    id("custom.querydsl")
}

dependencies {
    implementation(project(":stream-fix-core:core-port"))
    implementation(project(":stream-fix-core:core-domain"))

    // Database specific dependencies
    implementation("org.flywaydb:flyway-core:_")
    implementation("org.flywaydb:flyway-mysql:_")
    runtimeOnly("com.mysql:mysql-connector-j:_")
}