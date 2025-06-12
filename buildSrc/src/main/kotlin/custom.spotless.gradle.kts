import com.diffplug.gradle.spotless.SpotlessExtension

apply(plugin = "com.diffplug.spotless")

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
