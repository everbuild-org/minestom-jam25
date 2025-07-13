
plugins {
    `java-library`
    `maven-publish`

    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    api(libs.bundles.adventure)

    implementation(kotlin("stdlib"))
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.commons.compress)
}

java {
    withJavadocJar()
    withSourcesJar()
}
