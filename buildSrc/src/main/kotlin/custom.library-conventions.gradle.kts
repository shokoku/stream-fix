apply(plugin = "java-library")

tasks.getByName<Jar>("jar") {
    enabled = true
}
