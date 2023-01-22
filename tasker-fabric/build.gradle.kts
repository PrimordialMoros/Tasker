plugins {
    alias(libs.plugins.fabric.loom)
}

version = "${libs.versions.minecraft.get()}-${rootProject.version}"

repositories {
    maven("https://maven.fabricmc.net/")
}

dependencies {
    api(project(":tasker-core"))
    minecraft(libs.fabric.minecraft)
    mappings(loom.officialMojangMappings())
    modCompileOnly(libs.fabric.api)
    modCompileOnly(libs.fabric.loader)
}
