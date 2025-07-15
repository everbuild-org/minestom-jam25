package org.everbuild.jam25.command

import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.lobby.LobbyGroup
import kotlin.time.Duration.Companion.seconds

object QuickStartCommand : Kommand("quickstart") {

    init {
        permission = "jam.quickstart"
        default { _, _ ->
            val group = QueueCommand.groups.filter { g -> g.hasSpace() }.maxByOrNull { g -> g.players.size }
            if (group != null) {
                group.addPlayer(player)
            } else {
                val newGroup = LobbyGroup()
                newGroup.addPlayer(player)
                QueueCommand.groups.add(newGroup)
                player.sendMiniMessage("<green>Quickstart activated")
                for (i in 1..3) i.seconds later {
                    Jam.gameStates.getLobby(player)?.audience?.sendMiniMessage("${Jam.PREFIX} <green>Quickstart in ${4 - i}</green>")
                }
                3.seconds later {
                    Jam.gameStates.tryQuickStart(player)
                }
            }
        }
    }
}