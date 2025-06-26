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
    implementation(libs.kotlinx.cli)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.sse)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.creative.api)
    implementation(libs.creative.minecraft)
    implementation(libs.aws.s3)
    implementation(libs.aws.httpauth)
    implementation(libs.aws.httpclientenginecrt)
    implementation(libs.slf4j.simple)
}

tasks.register("buildResources", JavaExec::class) {
    group = "averium"
    dependsOn("classes")
    dependsOn(":resources:agent:jar")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    mainClass.set("org.everbuild.asorda.resources.ResourceGeneratorMainKt")
    args = listOf("-b", "main")
    jvmArgs = listOf(
        "-Dio.ktor.development=true",
        "-Dio.ktor.deployment.environment=dev",
        "-Dasorda.rpgen=true",
        "-javaagent:${project(":resources:agent").tasks.named<Jar>("jar").get().archiveFile.get().asFile.absolutePath}"
    )
}

tasks.register("releaseResources", JavaExec::class) {
    group = "averium"
    dependsOn("classes")
    dependsOn(":resources:agent:jar")
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
        "-javaagent:${project(":resources:agent").tasks.named<Jar>("jar").get().archiveFile.get().asFile.absolutePath}"
    )
}

tasks.register("serveResources", JavaExec::class) {
    group = "averium"
    dependsOn("classes")
    dependsOn(":resources:agent:jar")
    workingDir = rootProject.projectDir
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.everbuild.asorda.resources.ResourceGeneratorMainKt")
    args = listOf("-s", "-b", "main")
    jvmArgs = listOf(
        "-Dio.ktor.development=true",
        "-Dio.ktor.deployment.environment=dev",
        "-Dasorda.rpgen=true",
        "-javaagent:${project(":resources:agent").tasks.named<Jar>("jar").get().archiveFile.get().asFile.absolutePath}"
    )
}
