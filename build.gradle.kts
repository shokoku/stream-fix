import com.linecorp.support.project.multi.recipe.configureByLabels
import org.gradle.kotlin.dsl.the

plugins {
    id("java")
    id("io.spring.dependency-management") version Versions.SPRING_DEPENDENCY_MANAGEMENT_PLUGIN apply false
    id("org.springframework.boot") version Versions.SPRING_BOOT apply false
    id("io.freefair.lombok") version Versions.LOMBOK_PLUGIN apply false
    id("com.coditory.integration-test") version Versions.INTEGRATION_TEST_PLUGIN apply false
    id("com.epages.restdocs-api-spec") version Versions.RESTDOCS_API_SPEC apply false
    id("org.asciidoctor.jvm.convert") version Versions.ASCIIDOCTOR_PLUGIN apply false
    id("com.linecorp.build-recipe-plugin") version Versions.LINE_RECIPE_PLUGIN
    id("com.diffplug.spotless") version "7.0.4"

    kotlin("jvm") version Versions.KOTLIN apply false
    kotlin("kapt") version Versions.KOTLIN apply false
    kotlin("plugin.spring") version Versions.KOTLIN apply false
    kotlin("plugin.jpa") version Versions.KOTLIN apply false
}

allprojects {
    group = "com.shokoku"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.restlet.com") }
        maven { url = uri("https://jitpack.io") }
    }
}


subprojects {
    apply(plugin = "idea")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

configureByLabels("java") {
    apply(plugin = "org.gradle.java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "com.coditory.integration-test")
    apply(plugin = "com.diffplug.spotless")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    spotless {
        java {
            googleJavaFormat()
            importOrder("", "javax", "java", "\\#")
            removeUnusedImports()
            targetExclude(
                "build/**/*.java",
                "**/build/**/*.java",
                "generated/**/*.java",
                "**/generated/**/*.java",
                "**/gradle/wrapper/*",
                "**/buildSrc/build/**",
                "**/stream-fix-frontend/**"
            )
        }
    }


    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.SPRING_BOOT}")
            mavenBom("com.google.guava:guava-bom:${Versions.GUAVA}")
        }

        dependencies {
            dependency("org.apache.commons:commons-lang3:${Versions.APACHE_COMMONS_LANG}")
            dependency("org.apache.commons:commons-collections4:${Versions.APACHE_COMMONS_COLLECTIONS}")
            dependency("com.navercorp.fixturemonkey:fixture-monkey-starter:${Versions.FIXTURE_MONKEY}")
            dependency("org.mapstruct:mapstruct:${Versions.MAPSTRUCT}")
            dependency("org.mapstruct:mapstruct-processor:${Versions.MAPSTRUCT}")
            dependency("com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON_CORE}")

            dependency("org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}")
            dependency("org.junit.jupiter:junit-jupiter-params:${Versions.JUNIT}")
            dependency("org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}")
            dependency("org.assertj:assertj-core:${Versions.ASSERTJ_CORE}")
            dependency("org.mockito:mockito-junit-jupiter:${Versions.MOCKITO_CORE}")

            dependency("com.epages:restdocs-api-spec:${Versions.RESTDOCS_API_SPEC}")
            dependency("com.epages:restdocs-api-spec-mockmvc:${Versions.RESTDOCS_API_SPEC}")
            dependency("com.epages:restdocs-api-spec-restassured:${Versions.RESTDOCS_API_SPEC}")

            dependencySet("io.jsonwebtoken:${Versions.JWT}") {
                entry("jjwt-api")
                entry("jjwt-impl")
                entry("jjwt-jackson")
            }
        }
    }

    dependencies {
        val implementation by configurations
        val annotationProcessor by configurations

        val testImplementation by configurations
        val testRuntimeOnly by configurations

        val integrationImplementation by configurations
        val integrationRuntimeOnly by configurations

        implementation("com.google.guava:guava")

        implementation("org.apache.commons:commons-lang3")
        implementation("org.apache.commons:commons-collections4")
        implementation("org.mapstruct:mapstruct")

        annotationProcessor("org.mapstruct:mapstruct-processor")

        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.assertj:assertj-core")
        testImplementation("org.junit.jupiter:junit-jupiter-params")
        testImplementation("org.mockito:mockito-core")
        testImplementation("org.mockito:mockito-junit-jupiter")
        testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter")
        testImplementation("org.springframework.security:spring-security-test")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

        integrationImplementation("org.junit.jupiter:junit-jupiter-api")
        integrationImplementation("org.junit.jupiter:junit-jupiter-params")
        integrationImplementation("org.assertj:assertj-core")
        integrationRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }
}


configureByLabels("boot") {
    apply(plugin = "org.springframework.boot")

    tasks.getByName<Jar>("jar") {
        enabled = false
    }

    tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = true
        archiveClassifier.set("boot")
    }
}

configureByLabels("library") {
    apply(plugin = "java-library")

    tasks.getByName<Jar>("jar") {
        enabled = true
    }
}

configureByLabels("asciidoctor") {
    apply(plugin = "org.asciidoctor.jvm.convert")

    tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor") {
        sourceDir(file("src/docs"))
        outputs.dir(file("build/docs"))
        attributes(
            mapOf(
                "snippets" to file("build/generated-snippets")
            )
        )
    }
}

configureByLabels("restdocs") {
    apply(plugin = "com.epages.restdocs-api-spec")
}

configureByLabels("querydsl") {
    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("com.querydsl:querydsl-bom:${Versions.QUERYDSL}")
        }

        dependencies {
            dependency("com.querydsl:querydsl-core:${Versions.QUERYDSL}")
            dependency("com.querydsl:querydsl-jpa:${Versions.QUERYDSL}")
            dependency("com.querydsl:querydsl-apt:${Versions.QUERYDSL}")
        }
    }

    dependencies {
        val implementation by configurations
        val annotationProcessor by configurations

        implementation("com.querydsl:querydsl-jpa:${Versions.QUERYDSL}:jakarta")
        implementation("com.querydsl:querydsl-core:${Versions.QUERYDSL}")

        annotationProcessor("com.querydsl:querydsl-apt:${Versions.QUERYDSL}:jakarta")
        annotationProcessor("jakarta.persistence:jakarta.persistence-api")
        annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    }
}