sourceSets {
    main {
        resources {
            srcDir("resources")
        }
    }
}

plugins {
    `java-library`

    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(libs.bundles.creative)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.kotlinReflect)
    implementation(libs.bimap)
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.4.2")
}

