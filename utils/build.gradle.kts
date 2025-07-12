plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktor)
}

dependencies {
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.database)
    implementation(libs.bundles.utils)
    implementation(libs.bundles.luckperms)
    implementation(libs.minestom)
    implementation("org.xerial:sqlite-jdbc:3.50.1.0")
    implementation(project(":resources:data"))
    implementation("io.ktor:ktor-client-core-jvm:3.2.1")
    implementation(libs.bundles.ktor)
    implementation("io.ktor:ktor-client-apache:3.2.1")
    testImplementation(kotlin("test"))
}

tasks.processResources {
    dependsOn(":resources:buildResources")

    doFirst {
        rootProject.file("resources.lock.json").copyTo(layout.projectDirectory.file("src/main/resources/resources.lock.json").asFile, overwrite = true)
        rootProject.file("run/resources.json").copyTo(layout.projectDirectory.file("src/main/resources/resources.json").asFile, overwrite = true)
        rootProject.file("run/resources.zip").copyTo(layout.projectDirectory.file("src/main/resources/resources.zip").asFile, overwrite = true)
    }
}

tasks.classes {
    dependsOn("processResources")
}
