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
    maven { url = uri("https://maven.restlet.com") }
    maven { url = uri("https://jitpack.io") }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.5")
        mavenBom("com.google.guava:guava-bom:33.1.0-jre")
    }
    dependencies {
        dependency("org.apache.commons:commons-lang3:3.14.0")
        dependency("org.apache.commons:commons-collections4:4.4")
        dependency("org.mapstruct:mapstruct:1.5.5.Final")
        dependency("org.mapstruct:mapstruct-processor:1.5.5.Final")
        dependencySet("io.jsonwebtoken:0.12.5") {
            entry("jjwt-api")
            entry("jjwt-impl")
            entry("jjwt-jackson")
        }
    }
}

dependencies {
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-collections4")
    implementation("org.mapstruct:mapstruct")

    annotationProcessor("org.mapstruct:mapstruct-processor")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
