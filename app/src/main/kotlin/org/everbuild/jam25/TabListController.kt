package org.everbuild.jam25

import net.minestom.server.entity.Player
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.api.Mc

object TabListController {
    private val hiddenPlayers = mutableSetOf<Player>()
    private val spectatorPlayers = mutableSetOf<Player>()

    fun hidePlayer(player: Player, hidden: Boolean = true) {
        if (hidden) {
            hiddenPlayers.add(player)
        } else {
            hiddenPlayers.remove(player)
        }
        updateTabListForAll()
    }

    fun showPlayer(player: Player) {
        hiddenPlayers.remove(player)
        spectatorPlayers.remove(player)
        updateTabListForAll()
    }

    fun setSpectator(player: Player, isSpectator: Boolean) {
        if (isSpectator) {
            spectatorPlayers.add(player)
            hiddenPlayers.add(player)
        } else {
            spectatorPlayers.remove(player)
            hiddenPlayers.remove(player)
        }
        updateTabListForAll()
    }

    fun isSpectator(player: Player): Boolean = spectatorPlayers.contains(player)

    fun schedule() {
        Mc.scheduler.buildTask(::updateTabListForAll)
            .repeat(1.seconds.toJavaDuration())
            .schedule()
    }

    private fun updateTabListForAll() {
        for (player in Mc.connection.onlinePlayers) {
            player.sendPlayerListHeaderAndFooter(
                "\n\n<gradient:#FFAA00:#FF5555><bold>${Jam.NAME}</bold></gradient>\n\n".minimessage(),
                "\n<gradient:#FFAA00:#FF5555>✧ Asorda Jam Entry ✧</gradient> \n\n<gray>Wi1helm, _CreepyX_,\n<gray>     p3sto, TheNico24, justalittlewolf     \n<gray>Bloeckchengrafik\n".minimessage()
            )
        }
    }

    fun isHidden(player: Player): Boolean = hiddenPlayers.contains(player)
}