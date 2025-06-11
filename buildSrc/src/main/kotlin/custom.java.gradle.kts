import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

plugins {
    java
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
}

group = "com.shokoku"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:_")
        mavenBom("com.google.guava:guava-bom:_")
        mavenBom("com.querydsl:querydsl-bom:_")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:_")
    implementation("org.springframework.boot:spring-boot-starter-validation:_")
    implementation("org.springframework.boot:spring-boot-starter-security:_")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:_")
    implementation("org.springframework.boot:spring-boot-starter-aop:_")
    implementation("org.springframework.boot:spring-boot-starter-actuator:_")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:_")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:_")
    implementation("org.springframework.boot:spring-boot-starter-batch:_")
    
    implementation("org.apache.commons:commons-lang3:_")
    implementation("org.apache.commons:commons-collections4:_")
    implementation("org.mapstruct:mapstruct:_")
    implementation("org.flywaydb:flyway-core:_")
    implementation("org.flywaydb:flyway-mysql:_")
    implementation("io.jsonwebtoken:jjwt-api:_")
    implementation("io.jsonwebtoken:jjwt-impl:_")
    implementation("io.jsonwebtoken:jjwt-jackson:_")

    annotationProcessor("org.mapstruct:mapstruct-processor:_")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
