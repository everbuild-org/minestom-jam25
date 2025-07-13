import org.gradle.jvm.tasks.Jar

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("org.everbuild.asorda.resources.ResourceGeneratorMainKt")
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation(project(":resources:data"))

    implementation(libs.classgraph)
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.creative)
    implementation(libs.minestom)
    implementation(libs.bundles.utils)
    implementation("org.zeroturnaround:zt-zip:1.15")
}

tasks.register("buildResources", JavaExec::class) {
    group = "averium"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    mainClass.set("org.everbuild.asorda.resources.ResourceGeneratorMainKt")
    args = listOf("-b", "main")
    jvmArgs = listOf(
        "-Dio.ktor.development=true",
        "-Dio.ktor.deployment.environment=dev",
        "-Dasorda.rpgen=true",
    )
}

tasks.register("releaseResources", JavaExec::class) {
    group = "averium"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    mainClass.set("org.everbuild.asorda.resources.ResourceGeneratorMainKt")
    args = listOf(
        "-b", "main",
        "-u",
        "--s3server", project.properties["s3.host"].toString(),
        "--s3bucket", project.properties["s3.bucket"].toString(),
        "--s3key", project.properties["s3.accessKey"].toString(),
        "--s3secret", project.properties["s3.secretKey"].toString(),
    )
    jvmArgs = listOf(
        "-Dio.ktor.development=true",
        "-Dio.ktor.deployment.environment=dev",
        "-Dasorda.rpgen=true",
    )
}

tasks.register("serveResources", JavaExec::class) {
    group = "averium"
    dependsOn("classes")
    workingDir = rootProject.projectDir
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.everbuild.asorda.resources.ResourceGeneratorMainKt")
    args = listOf("-s", "-b", "main")
    jvmArgs = listOf(
        "-Dio.ktor.development=true",
        "-Dio.ktor.deployment.environment=dev",
        "-Dasorda.rpgen=true",
    )
}
