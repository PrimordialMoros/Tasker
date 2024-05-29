plugins {
    `java-library`
    signing
    `maven-publish`
    alias(libs.plugins.checker)
}

allprojects {
    group = "me.moros"
    version = "1.2.0"

    apply(plugin = "java-library")
    apply(plugin = "org.checkerframework")

    repositories {
        mavenCentral()
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks {
        withType<JavaCompile> {
            options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
            options.encoding = "UTF-8"
        }
        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }
        named<Copy>("processResources") {
            from(rootProject.file("LICENSE")) {
                rename { "META-INF/${it}_${rootProject.name.uppercase()}" }
            }
        }
    }
}
subprojects {
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    java {
        if (!isSnapshot()) {
            withJavadocJar()
        }
        withSourcesJar()
    }

    tasks {
        withType<Sign>().configureEach {
            onlyIf { !isSnapshot() }
        }
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set("Task scheduling framework for the JVM.")
                url.set("https://github.com/PrimordialMoros/Tasker")
                licenses {
                    license {
                        name.set("The GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("moros")
                        name.set("Moros")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/PrimordialMoros/Tasker.git")
                    developerConnection.set("scm:git:ssh://git@github.com/PrimordialMoros/Tasker.git")
                    url.set("https://github.com/PrimordialMoros/Tasker")
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/PrimordialMoros/Tasker/issues")
                }
            }
        }
        repositories {
            val snapshotUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            val releaseUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            maven {
                name = "sonatype"
                credentials(PasswordCredentials::class)
                url = if (isSnapshot()) snapshotUrl else releaseUrl
            }
        }
    }
    signing {
        sign(publishing.publications["maven"])
    }
}

fun isSnapshot() = project.version.toString().endsWith("-SNAPSHOT")
