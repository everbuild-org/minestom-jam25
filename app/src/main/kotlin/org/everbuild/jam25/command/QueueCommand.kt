package org.everbuild.jam25.command

import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.lobby.LobbyGroup

object QueueCommand : Kommand("queue") {
    val groups get() = Jam.gameStates.lobbyState.groups

    init {
        permission = "jam.queue"
        default { _, _ ->
            val group = groups.filter { g -> g.hasSpace() }.maxByOrNull { g -> g.players.size }
            if (group != null) {
                group.addPlayer(player)
            } else {
                val newGroup = LobbyGroup()
                newGroup.addPlayer(player)
                groups.add(newGroup)
            }
        }
    }
}