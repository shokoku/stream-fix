import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.kotlin.dsl.configure

plugins {
    java
    idea
    id("io.freefair.lombok")
}

apply(plugin = "com.diffplug.spotless")


repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

dependencies {
    implementation("org.jetbrains:annotations:_")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

configure<SpotlessExtension> {
    java {
        target("**/*.java")

        googleJavaFormat("1.24.0")
        importOrder()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        target("**/*.kt")
        ktlint("1.4.1")
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint("1.4.1")
    }
}

tasks.named("compileJava") {
    dependsOn("spotlessApply")
}
