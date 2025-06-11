plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.5")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.5")
    implementation("io.freefair.gradle:lombok-plugin:8.6")
    implementation("org.gradle.test-retry:org.gradle.test-retry.gradle.plugin:1.5.8")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    implementation("com.epages:restdocs-api-spec-gradle-plugin:0.19.2")
    implementation("org.asciidoctor:asciidoctor-gradle-jvm:4.0.2")
}