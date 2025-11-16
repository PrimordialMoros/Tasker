repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    api(projects.taskerCore)
    compileOnly(libs.sponge.api)
}
