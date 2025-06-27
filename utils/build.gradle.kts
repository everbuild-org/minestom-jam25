plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.database)
    implementation(libs.bundles.utils)
    implementation(libs.bundles.luckperms)
    implementation(libs.minestom)
    implementation(project(":resources:data"))
    testImplementation(kotlin("test"))
}

tasks.classes {
    dependsOn(":resources:buildResources")

    doFirst {
        rootProject.file("resources.lock.json").copyTo(layout.projectDirectory.file("src/main/resources/resources.lock.json").asFile, overwrite = true)
        rootProject.file("run/resources.json").copyTo(layout.projectDirectory.file("src/main/resources/resources.json").asFile, overwrite = true)
        rootProject.file("run/resources.zip").copyTo(layout.projectDirectory.file("src/main/resources/resources.zip").asFile, overwrite = true)
    }
}
