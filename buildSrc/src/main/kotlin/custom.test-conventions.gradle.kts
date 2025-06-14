import gradle.kotlin.dsl.accessors._b6894e0511bd903116b8270f6e7db3de.jacocoTestReport

plugins {
    java
    idea
    jacoco
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
    
    // Jacoco 설정
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
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