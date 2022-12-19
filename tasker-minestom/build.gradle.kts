repositories {
    maven("https://jitpack.io/")
}

dependencies {
    api(project(":tasker-core"))
    compileOnly(libs.minestom.api)
}
