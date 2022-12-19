pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "tasker"
include("tasker-core")
include("tasker-bukkit")
include("tasker-sponge")
include("tasker-fabric")
include("tasker-minestom")
