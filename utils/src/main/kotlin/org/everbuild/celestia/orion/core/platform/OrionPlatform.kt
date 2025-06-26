package org.everbuild.celestia.orion.core.platform

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.luckperms.LuckPermsData
import java.net.URI

interface OrionPlatform {
    fun registerJoinEvent(handler: (PlatformIndependentJoinEvent) -> Unit)
    fun registerLeaveEvent(handler: (OrionPlayer) -> Unit)
    fun getAdventureAudience(player: OrionPlayer): Audience
    fun getLuckPermsData(player: OrionPlayer): LuckPermsData
    fun isOnline(player: OrionPlayer): Boolean
    fun isProxy(): Boolean
    fun getLogicalMaxPlayerCount(): Long
    fun isReady(): Boolean
    fun getPlayers(): List<OrionPlayer>
    fun executeServerCommand(command: String)
    fun sendMessageAs(player: OrionPlayer, component: Component)
    fun executeCommandAs(player: OrionPlayer, command: String)
    fun sendTo(player: OrionPlayer, to: String)
    fun teleportationFactory(): TeleportationFactory
    fun teleport(player: OrionPlayer, teleportation: Teleportation)
    fun texture(player: OrionPlayer): URI?
}