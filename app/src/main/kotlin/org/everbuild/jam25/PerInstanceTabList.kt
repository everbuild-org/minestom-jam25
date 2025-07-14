package org.everbuild.jam25

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.EventNode
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket
import net.minestom.server.tag.Tag
import net.minestom.server.utils.PacketSendingUtils
import org.everbuild.celestia.orion.platform.minestom.util.listen


object PerInstanceTabList {
    private val firstSpawnTag: Tag<Boolean> = Tag.Boolean("InstanceViewHook:FirstSpawn").defaultValue(true)

    fun eventNode() = EventNode.all("instance-view-hook")
        .listen<PlayerSpawnEvent, _> { event ->
            val player = event.player
            val instance = event.spawnInstance
            PacketSendingUtils.sendGroupedPacket(
                instance.players,
                getAddPlayerPacket(player)
            ) { player1 -> player1 !== player }
            if (player.getTag(firstSpawnTag) == true) { // On First Spawn
                for (onlinePlayer in MinecraftServer.getConnectionManager().onlinePlayers) {
                    if (onlinePlayer == player || onlinePlayer.getInstance() === instance) continue
                    PacketSendingUtils.sendPacket(player, getRemovePlayerPacket(onlinePlayer))
                    PacketSendingUtils.sendPacket(onlinePlayer, getRemovePlayerPacket(player))
                }
                player.setTag(firstSpawnTag, false)
            } else { // On Switch Instance
                for (instancePlayer in instance.players) {
                    if (instancePlayer == player) continue
                    PacketSendingUtils.sendPacket(player, getAddPlayerPacket(instancePlayer))
                }
            }
        }
        .listen<RemoveEntityFromInstanceEvent, _> { event ->
            if (event.entity !is Player) return@listen
            val player = event.entity as Player
            val instance: Instance = event.instance
            PacketSendingUtils.sendGroupedPacket(
                instance.players,
                getRemovePlayerPacket(player)
            ) { player1 -> player1 !== player }
            for (instancePlayer in instance.players) {
                if (instancePlayer === player) continue
                PacketSendingUtils.sendPacket(player, getRemovePlayerPacket(instancePlayer))
            }

        }

    private fun getAddPlayerPacket(player: Player): PlayerInfoUpdatePacket = getPlayerPacket(player, true)
    private fun getRemovePlayerPacket(player: Player): PlayerInfoRemovePacket = PlayerInfoRemovePacket(listOf(player.uuid))

    private fun getPlayerPacket(player: Player, shown: Boolean): PlayerInfoUpdatePacket {
        val skin: PlayerSkin? = player.skin

        val prop =
            if (skin != null) listOf(
                PlayerInfoUpdatePacket.Property(
                    "textures",
                    skin.textures(),
                    skin.signature()
                )
            ) else listOf()

        return PlayerInfoUpdatePacket(
            PlayerInfoUpdatePacket.Action.ADD_PLAYER,
            PlayerInfoUpdatePacket.Entry(
                player.uuid,
                player.username,
                prop,
                shown,
                player.latency,
                player.gameMode,
                player.displayName,
                null,
                0
            )
        )
    }
}