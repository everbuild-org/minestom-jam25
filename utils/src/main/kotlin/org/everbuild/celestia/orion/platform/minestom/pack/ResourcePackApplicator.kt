package org.everbuild.celestia.orion.platform.minestom.pack

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.origin
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.BindException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID
import java.util.concurrent.Flow
import java.util.concurrent.SubmissionPublisher
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.logger
import org.everbuild.celestia.orion.platform.minestom.util.listen

private fun withInternalServer(): String {

    embeddedServer(Netty, port=3013, host="0.0.0.0") {
        routing {
            get("/pack.zip") {
                logger.info("Received resource pack request from ${call.request.origin.remoteHost}")
                call.respondBytes(
                    bytes = javaClass.getResourceAsStream("/resources.zip")!!.readAllBytes(),
                    contentType = io.ktor.http.ContentType.Application.Zip,
                    status = io.ktor.http.HttpStatusCode.OK
                )
            }
        }
    }.start(wait = false)

    return ":3013/pack.zip"
}

private fun withExternalServer(publisher: SubmissionPublisher<String>): String {
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:3013/updates"))
        .header("Accept", "text/event-stream")
        .build()

    client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
        .thenAccept { response ->
            BufferedReader(InputStreamReader(response.body())).use { reader ->
                val data = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    when {
                        line!!.startsWith("data:") -> {
                            data.append(line.substringAfter("data:").trim())
                        }
                        line.isBlank() -> {
                            val event = data.toString()
                            data.setLength(0)
                            publisher.submit(event)
                        }
                    }
                }

                publisher.close()
            }
        }.exceptionally { e ->
            publisher.closeExceptionally(e)
            null
        }

    return ":3013/pack.zip"
}

fun withResourcePacksInDev() {
    val publisher = SubmissionPublisher<String>()
    val portAndPath = try {
        withInternalServer()
    } catch (_: BindException) {
        withExternalServer(publisher)
    }

    var hash = "";

    fun recomputeHash() {
        hash = ResourcePackInfo.resourcePackInfo()
            .id(UUID.randomUUID())
            .uri(URI.create("http://localhost$portAndPath"))
            .computeHashAndBuild()
            .join().hash()
        println("Loaded resource pack hash: $hash")
    }

    fun sendPack(player: Player) {
        var addr = player.playerConnection.serverAddress ?: "127.0.0.1"
        addr = addr.split(":")[0]

        recomputeHash()

        val info = ResourcePackInfo.resourcePackInfo()
            .id(UUID.randomUUID())
            .uri(URI.create("http://$addr$portAndPath"))
            .hash(hash)
            .build()

        println("Sending resource pack to ${player.username} (${player.uuid}) (${"http://$addr$portAndPath"})")

        player.sendResourcePacks(
            ResourcePackRequest.resourcePackRequest()
                .packs(info)
                .required(true)
                .replace(true)
                .prompt("This is the asorda dev pack".component())
        )
    }

    fun resendPack() {
        Mc.connection.onlinePlayers.forEach { sendPack(it) }
    }

    recomputeHash()
    publisher.subscribe(object : Flow.Subscriber<String> {
        override fun onSubscribe(subscription: Flow.Subscription) {
            subscription.request(Long.MAX_VALUE)
        }
        override fun onError(throwable: Throwable) {
            throwable.printStackTrace()
        }
        override fun onComplete() {
            println("Resource pack: done!")
        }
        override fun onNext(item: String?) {
            recomputeHash()
            resendPack()
        }

    })

    listen<PlayerSpawnEvent> { event ->
        if (event.isFirstSpawn)
            sendPack(event.player)
    }
}

object ResourcePackApplicator

fun withResourcePack(url: String) {
    ResourcePackInfo.resourcePackInfo()
        .id(UUID.randomUUID())
        .uri(URI.create(url))
        .computeHashAndBuild().thenAccept { info ->
            ResourcePackApplicator.logger.info("Loaded resource pack hash: ${info.hash()}")

            listen<PlayerSpawnEvent> { event ->
                if (!event.isFirstSpawn) return@listen

                event.player.sendResourcePacks(
                    ResourcePackRequest.resourcePackRequest()
                        .packs(info)
                        .required(true)
                        .replace(true)
                        .prompt("<yellow>This game requires a resource pack. Click 'Accept' to continue".minimessage())
                )
            }
        }
}