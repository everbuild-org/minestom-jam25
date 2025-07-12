plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.gradleup.shadow") version "9.0.0-rc1"

    application
}

dependencies {
    implementation(project(":utils"))
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.adventure)
    implementation(libs.bundles.database)
    implementation(libs.bundles.utils)
    implementation(libs.bundles.luckperms)
    implementation(libs.minestom)
    implementation("dev.hollowcube:schem:2.0")
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

val includeSchematics = tasks.register<Copy>("includeSchematics") {
    from("../schematics")
    into(layout.buildDirectory.dir("resources/main"))
}

tasks.processResources {
    dependsOn(zipMap)
    dependsOn(includeSchematics)
}