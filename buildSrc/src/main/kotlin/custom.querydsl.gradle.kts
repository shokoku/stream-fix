dependencies {
    val implementation by configurations
    val annotationProcessor by configurations

    implementation("com.querydsl:querydsl-jpa:_:jakarta")
    implementation("com.querydsl:querydsl-core:_")

    annotationProcessor("com.querydsl:querydsl-apt:_:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api:_")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api:_")
}
