enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "tasker"

include("tasker-core")
include("tasker-paper")
include("tasker-sponge")
include("tasker-fabric")
include("tasker-minestom")
