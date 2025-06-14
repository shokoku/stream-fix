plugins {
    java
    idea
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform {
        if (EnvUtils.isAct()) {
            excludeTags("integration")
        }
    }

    testLogging {
        events("passed", "skipped", "failed")
    }

    maxHeapSize = "2g"
}

dependencies {
    // junit
    testImplementation(Testing.junit.jupiter)
    testRuntimeOnly(Testing.junit.jupiter.engine)

    // mockito
    testImplementation(Testing.mockito.core)
    testImplementation(Testing.mockito.junitJupiter)

    project.afterEvaluate {
        if(project.pluginManager.hasPlugin("custom.spring-conventions")) {
            testImplementation(Spring.boot.test)
        }
    }
}