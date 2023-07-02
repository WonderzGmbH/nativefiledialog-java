plugins {
    kotlin("jvm") version "1.8.22"
    id("maven-publish")
    id("signing")
}

group = "tv.wunderbox"
version = "1.0.2"

repositories {
    mavenCentral()
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}

dependencies {
    implementation("net.java.dev.jna:jna:5.13.0")
}

// See:
// https://www.jetbrains.com/help/space/publish-artifacts-to-maven-central.html
// on how to publish to maven repos.
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "nativefiledialog"
            version = project.version.toString()

            from(components["java"])

            pom {
                packaging = "jar"
                name.set("Native File Dialog Java")
                url.set("https://github.com/WonderzGmbH/nativefiledialog-java")
                description.set("A wrapper around the small btzy/nativefiledialog-extended. As a fallback it uses AWT file picker.")

                licenses {
                    license {
                        name.set("zlib License")
                        url.set("https://github.com/WonderzGmbH/nativefiledialog-java/blob/master/LICENSE")
                    }
                }

                scm {
                    connection.set("scm:https://github.com/WonderzGmbH/nativefiledialog-java.git")
                    developerConnection.set("scm:git@github.com:WonderzGmbH/nativefiledialog-java.git")
                    url.set("https://github.com/WonderzGmbH/nativefiledialog-java")
                }

                developers {
                    developer {
                        id.set("AChep")
                        name.set("Artem Chepurnyi")
                        email.set("artemchep@gmail.com")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = project.properties["ossrhUsername"].toString()
                password = project.properties["ossrhPassword"].toString()
            }
        }
    }
}


signing {
    sign(publishing.publications)
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
