the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom("com.querydsl:querydsl-bom:5.1.0")
    }

    dependencies {
        dependency("com.querydsl:querydsl-core:5.1.0")
        dependency("com.querydsl:querydsl-jpa:5.1.0")
        dependency("com.querydsl:querydsl-apt:5.1.0")
    }
}

dependencies {
    val implementation by configurations
    val annotationProcessor by configurations

    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    implementation("com.querydsl:querydsl-core:5.1.0")

    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
}