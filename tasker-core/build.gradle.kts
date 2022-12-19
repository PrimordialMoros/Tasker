dependencies {
    testImplementation(libs.bundles.junit)
}

tasks.test {
    useJUnitPlatform()
}
