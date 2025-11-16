dependencies {
    api(libs.jspecify)
    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.junit.platform)
}

tasks.test {
    useJUnitPlatform()
}
