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
