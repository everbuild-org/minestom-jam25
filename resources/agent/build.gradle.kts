plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    compileOnly(project(":resources"))
}

application {
    mainClass.set("org.everbuild.asorda.resources.agent.ResourceAgent")
}

tasks.jar {
    manifest {
        attributes["Premain-Class"] = "org.everbuild.asorda.resources.agent.ResourceAgent"
    }
}
