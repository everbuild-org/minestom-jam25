package org.everbuild.celestia.orion.platform.minestom.platform

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerDisconnectEvent
import org.everbuild.celestia.orion.core.chat.ChatMessage
import org.everbuild.celestia.orion.core.chat.Textures
import org.everbuild.celestia.orion.core.platform.OrionPlatform
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.luckperms.LuckPermsData
import org.everbuild.celestia.orion.core.platform.PlatformIndependentJoinEvent
import org.everbuild.celestia.orion.core.platform.Teleportation
import org.everbuild.celestia.orion.core.platform.TeleportationFactory
import org.everbuild.celestia.orion.platform.minestom.globalServer
import org.everbuild.celestia.orion.platform.minestom.luckperms.MinestomLuckPermsData
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.celestia.orion.platform.minestom.util.minestom
import org.everbuild.celestia.orion.platform.minestom.util.orion
import org.everbuild.celestia.orion.platform.minestom.util.toPos
import java.net.URI
import java.util.*
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent

class MinestomOrionPlatform : OrionPlatform {
    override fun registerJoinEvent(handler: (PlatformIndependentJoinEvent) -> Unit) {
        listen<AsyncPlayerPreLoginEvent> {
            handler(
                PlatformIndependentJoinEvent(
                    playerName = it.gameProfile.name,
                    playerUUID = it.gameProfile.uuid,
                    playerLocale = "en"
                )
            )
        }
    }

    override fun registerLeaveEvent(handler: (OrionPlayer) -> Unit) {
        listen<PlayerDisconnectEvent> {
            handler(it.player.orion)
        }
    }

    override fun getAdventureAudience(player: OrionPlayer): Audience = player.minestom
    override fun getLuckPermsData(player: OrionPlayer): LuckPermsData = MinestomLuckPermsData(player.minestom)
    override fun isOnline(player: OrionPlayer): Boolean =
        MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(UUID.fromString(player.uuid)) != null

    override fun isProxy(): Boolean = false
    override fun getLogicalMaxPlayerCount(): Long = globalServer.maxPlayers.toLong()
    override fun isReady(): Boolean = globalServer.online
    override fun getPlayers(): List<OrionPlayer> = MinecraftServer.getConnectionManager().onlinePlayers.map { it.orion }

    override fun executeServerCommand(command: String) {
        MinecraftServer.getCommandManager().executeServerCommand(command)
    }

    override fun sendMessageAs(player: OrionPlayer, component: Component) {
        globalServer.chat.send(ChatMessage.text(player, component, "-"))
    }

    override fun executeCommandAs(player: OrionPlayer, command: String) {
        // not supported
    }

    override fun sendTo(player: OrionPlayer, to: String) {
        // not supported
    }

    override fun teleportationFactory(): TeleportationFactory = MinestomTeleportationFactory()

    override fun teleport(player: OrionPlayer, teleportation: Teleportation) {
        player.minestom.teleport(teleportation.toLocation().toPos())
    }

    override fun texture(player: OrionPlayer): URI? {
        val texs = Textures.de(player.minestom.skin?.textures() ?: return null) ?: return null
        val tex = texs.textures.getOrDefault("SKIN", null) ?: return null
        return URI(tex.url)
    }
}