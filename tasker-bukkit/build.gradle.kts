repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":tasker-core"))
    compileOnly(libs.paper)
}
