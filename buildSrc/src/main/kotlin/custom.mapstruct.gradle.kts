dependencies {
    val implementation by configurations
    val annotationProcessor by configurations
    
    implementation("org.mapstruct:mapstruct:_")
    annotationProcessor("org.mapstruct:mapstruct-processor:_")
}