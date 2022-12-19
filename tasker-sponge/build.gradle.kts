repositories {
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    api(project(":tasker-core"))
    compileOnly(libs.sponge.api)
}
