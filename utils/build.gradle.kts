plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.database)
    implementation(libs.bundles.utils)
    implementation(libs.minestom)
    testImplementation(kotlin("test"))
}