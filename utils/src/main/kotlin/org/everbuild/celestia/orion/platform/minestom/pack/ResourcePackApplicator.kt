package org.everbuild.celestia.orion.platform.minestom.pack

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent
import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.util.listen
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.BindException
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.Flow
import java.util.concurrent.SubmissionPublisher

private fun withInternalServer(): String {
    val server = HttpServer.create(InetSocketAddress(3013), 0)
    server.createContext("/pack.zip", object : HttpHandler {
        @Throws(IOException::class)
        override fun handle(exchange: HttpExchange) {
            javaClass.getResourceAsStream("/resources.zip").use { inputStream ->
                if (inputStream == null) {
                    val response = "File not found"
                    exchange.sendResponseHeaders(404, response.toByteArray().size.toLong())
                    exchange.responseBody.use { os ->
                        os.write(response.toByteArray())
                    }
                    return
                }
                val fileBytes = inputStream.readAllBytes()

                println("Load resource pack")

                exchange.responseHeaders.add("Content-Type", "application/zip")
                exchange.responseHeaders.add("Content-Disposition", "attachment; filename=\"pack.zip\"")
                exchange.sendResponseHeaders(200, fileBytes.size.toLong())
                exchange.responseBody.use { os ->
                    os.write(fileBytes)
                }
            }
        }
    })

    server.executor = null
    server.start()

    return ":${server.address.port}/pack.zip"
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

fun withResourcePacksIfInDev() {
    if (SharedPropertyConfig.bcpEnabled) return
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

        val info = ResourcePackInfo.resourcePackInfo()
            .id(UUID.randomUUID())
            .uri(URI.create("http://$addr$portAndPath"))
            .hash(hash)
            .build()

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