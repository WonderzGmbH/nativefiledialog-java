import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.32"
    id("maven-publish")
    id("signing")
}

group = "tv.wunderbox"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
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
        }
    }
}

signing {
    sign(publishing.publications)
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
