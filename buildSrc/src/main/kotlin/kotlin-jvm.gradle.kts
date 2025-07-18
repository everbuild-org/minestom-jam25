package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin in JVM projects.
    kotlin("jvm")
}

kotlin {
    // Use a specific Java version to make it easier to work in different environments.
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    maven("https://mvn.everbuild.org/public") {
        content{
            excludeModule("dev.lu15","luckperms-minestom")
            excludeModule("dev.lu15","luckperms-common")
        }
    }
    maven("https://repo.hypera.dev/snapshots")
    maven("https://repo.lucko.me/")
    maven("https://reposilite.atlasengine.ca/public")
    maven("https://repo.hypera.dev/snapshots")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.mineinabyss.com/releases")
    maven("https://reposilite.atlasengine.ca/public")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.viaversion.com")
    maven("https://repo.lucko.me/")
    maven("https://maven.solo-studios.ca/releases")
}

tasks.withType<Test>().configureEach {
    // Configure all test Gradle tasks to use JUnitPlatform.
    useJUnitPlatform()

    // Log information about all test results, not only the failed ones.
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}
