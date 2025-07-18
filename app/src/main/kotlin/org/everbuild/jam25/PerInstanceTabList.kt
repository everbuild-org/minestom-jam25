package org.everbuild.jam25

import java.util.WeakHashMap
import kotlin.time.Duration.Companion.milliseconds
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.EventNode
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.celestia.orion.platform.minestom.util.listen


object PerInstanceTabList {
    fun eventNode() = EventNode.all("instance-view-hook")
        .listen<PlayerSpawnEvent, _> { event ->
            500.milliseconds later {
                update()
            }
        }
        .listen<RemoveEntityFromInstanceEvent, _> { event ->
            500.milliseconds later {
                update()
            }
        }

    fun update() {
        Mc.connection.onlinePlayers.forEach { player1 ->
            Mc.connection.onlinePlayers.forEach { player2 ->
                if (player1 == player2) return@forEach player1.sendPacket(getAddPlayerPacket(player1))
                if (player1.instance != player2.instance) {
                    player1.sendPacket(getRemovePlayerPacket(player2))
                    return@forEach
                }
                player1.sendPacket(getAddPlayerPacket(player2))
            }
        }
    }

    private fun getAddPlayerPacket(player: Player): ServerPacket = getPlayerPacket(player, true)
    private fun getRemovePlayerPacket(player: Player): ServerPacket = PlayerInfoRemovePacket(player.uuid)

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