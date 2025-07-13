package org.everbuild.asorda.resources

import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.contentType
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import io.ktor.util.cio.ChannelWriteException
import io.ktor.utils.io.ClosedWriteChannelException
import kotlin.system.exitProcess
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.everbuild.asorda.resources.data.ResourceGenerator
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter
import kotlin.time.Duration.Companion.milliseconds
import org.everbuild.asorda.resources.ResourceGenerationService.Companion.resources
import org.everbuild.asorda.resources.ResourceGenerationService.Companion.resourcesDir
import org.everbuild.asorda.resources.ResourceGenerationService.Companion.zipDirectory
import org.everbuild.asorda.resources.data.addWseeModels

val service = ResourceGenerationService()

fun triggerRefresh() {
    service.requestGeneration()
}

fun main(args: Array<String>) {
    val parser = ArgParser("resourcegen")
    val branch by parser.option(ArgType.String, shortName = "b", description = "The branch to use")
    val upload by parser.option(ArgType.Boolean, shortName = "u", description = "Upload the generated file to s3")
        .default(false)
    val serve by parser.option(
        ArgType.Boolean,
        shortName = "s",
        description = "Start a local server to serve the generated file and broadcast changes"
    ).default(false)
    val s3Server by parser.option(ArgType.String, fullName = "s3server", description = "The s3 server to use")
    val s3Bucket by parser.option(ArgType.String, fullName = "s3bucket", description = "The s3 bucket to use")
    val s3key by parser.option(ArgType.String, fullName = "s3key", description = "The s3 access key to use")
    val s3secret by parser.option(ArgType.String, fullName = "s3secret", description = "The s3 secret to use")
    parser.parse(args)

    if (serve) {
        service.start()
        println("> Running on :3013")
        embeddedServer(Netty, 3013) {
            install(SSE)

            routing {
                get("/trigger") {
                    triggerRefresh()
                    call.respond("OK")
                }

                get("/pack.zip") {
                    call.response.header(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "pack.zip")
                            .toString()
                    )
                    call.respondFile(ResourceGenerationService.resources) {
                        contentType(ContentType.Application.Zip) {}
                    }
                }

                sse("/updates") {
                    val channel: Channel<String> = service.newUpdateChannel()
                    println("> Channel opened")
                    send("BEGIN")

                    while (true) {
                        val hash = channel.receive()
                        try {
                            print("Sending...")
                            System.out.flush()
                            send(hash)
                            println(" [OK]")
                        } catch (_: ClosedWriteChannelException) {
                            break
                        }
                    }

                    println(" [Channel closed]")
                    service.dropChannel(channel)
                }
            }
        }.start(wait = true)
    } else {
        runBlocking {
            val (pack, meta) = ResourceGenerator.regenerate { step -> println(step) }
            ResourceGenerationService.metadata.parentFile.mkdirs()
            MinecraftResourcePackWriter
                .builder()
                .prettyPrinting(true)
                .targetPackFormat(46)
                .build()
                .writeToDirectory(resourcesDir, pack)
            MinecraftResourcePackWriter.minecraft().writeToDirectory(resourcesDir, pack)
            addWseeModels(resourcesDir)
            zipDirectory(resourcesDir, resources)
            ResourceGenerationService.metadata.writeText(Json.encodeToString(meta))

            if (upload) {
                println("Uploading not supported")
            }

            exitProcess(0)
        }
    }
}
