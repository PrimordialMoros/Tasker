repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(projects.taskerCore)
    compileOnly(libs.paper.api)
}
