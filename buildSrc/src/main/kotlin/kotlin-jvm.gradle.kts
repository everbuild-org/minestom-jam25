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
