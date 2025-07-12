package org.everbuild.celestia.orion.platform.minestom

import net.minestom.server.MinecraftServer
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.instance.InstanceManager
import org.everbuild.celestia.orion.core.OrionCore
import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import org.everbuild.celestia.orion.core.chat.BufferedChat
import org.everbuild.celestia.orion.core.util.OrionDependency
import org.everbuild.celestia.orion.platform.minestom.chat.ChatCommand
import org.everbuild.celestia.orion.platform.minestom.chat.MinestomPlatformChatHooks
import org.everbuild.celestia.orion.platform.minestom.chat.registerChatListeners
import org.everbuild.celestia.orion.platform.minestom.command.*
import org.everbuild.celestia.orion.platform.minestom.initializer.registerPlayerInitializer
import org.everbuild.celestia.orion.platform.minestom.luckperms.MinestomLuckPermsProvider
import org.everbuild.celestia.orion.platform.minestom.platform.MinestomOrionPlatform
import org.everbuild.celestia.orion.platform.minestom.scoreboard.MinestomScoreboardTablistController
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(OrionServer::class.java)

fun createOrionServer(): MinecraftServer {
    return MinecraftServer.init()
}

fun MinecraftServer.autoBind() {
    logger.info("Bound to 0.0.0.0:${MinestomOrionConfig.bind}")
    this.start("0.0.0.0", MinestomOrionConfig.bind)
}

val instanceManager: InstanceManager get() = MinecraftServer.getInstanceManager()
lateinit var globalServer: OrionServer

abstract class OrionServer(private vararg val deps: OrionDependency) : OrionCore<MinestomOrionPlatform>(
    MinestomOrionPlatform()
) {
    val server = createOrionServer()
    var maxPlayers = 128
    var online = false
    val chat = BufferedChat(MinestomPlatformChatHooks())

    init {
        MinestomLuckPermsProvider.load(this)
        MinestomScoreboardTablistController.start()
        globalServer = this
    }

    fun bind() {
        registerPlayerInitializer()
        load()

        GamemodeCommand.register()
        TeleportCommand.register()
        AdminCommand.register()
        ChatCommand.register()
        CoinCommand.register()
        BalCommand.register()
        PayCommand.register()

        if (SharedPropertyConfig.bcpEnabled) {
            BungeeCordProxy.enable()
        }

        registerChatListeners(chat)

        online = true
        server.autoBind()
    }
}

