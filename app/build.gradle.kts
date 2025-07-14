plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.gradleup.shadow") version "9.0.0-rc1"

    application
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":kotlinx-serialization-nbt"))
    implementation(project(":resources:data"))
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.database)
    implementation(libs.bundles.utils)
    implementation(libs.bundles.luckperms)
    implementation(libs.minestom)
    implementation("dev.hollowcube:schem:2.0")
    implementation("org.joml:joml:1.10.8")
    implementation("net.worldseed.multipart:WorldSeedEntityEngine:11.3.1-dev2")
}

application {
    mainClass = "org.everbuild.jam25.JamKt"
}

tasks.named<JavaExec>("run") {
    workingDir = file("../run")
}

val zipMap = tasks.register<Zip>("zipMap") {
    from("../map")
    destinationDirectory.set(layout.buildDirectory.dir("resources/main"))
    archiveFileName.set("map.zip")
}

val zipLobby = tasks.register<Zip>("zipLobby") {
    from("../lobby")
    destinationDirectory.set(layout.buildDirectory.dir("resources/main"))
    archiveFileName.set("lobby.zip")
}

val includeSchematics = tasks.register<Copy>("includeSchematics") {
    from("../schematics")
    into(layout.buildDirectory.dir("resources/main"))
}

tasks.processResources {
    dependsOn(zipMap, zipLobby)
    dependsOn(includeSchematics)
}