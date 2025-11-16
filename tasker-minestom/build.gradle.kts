dependencies {
    api(projects.taskerCore)
    compileOnly(libs.minestom.api)
}

configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
