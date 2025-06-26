import org.gradle.api.tasks.testing.logging.TestExceptionFormat

sourceSets {
    main {
        resources {
            srcDir("resources")
        }
    }
}

plugins {
    `java-library`

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.benchmark)
}

dependencies {
    implementation(libs.creative.api)
    implementation(libs.adventure.nbt)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.reflect)
    implementation(libs.bimap)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.kotlin)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}
