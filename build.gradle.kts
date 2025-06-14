plugins {
    jacoco
    id("custom.jacoco-aggregate-conventions")
}

subprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}