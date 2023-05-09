import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
}

group = "tv.wunderbox"
version = "0.1.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.java.dev.jna:jna:5.10.0")
    implementation("io.arrow-kt:arrow-core:1.0.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
