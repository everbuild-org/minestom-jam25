package org.everbuild.jam25

import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.ping.Status
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.state.GameStateController

class Jam : OrionServer() {
    val gameStates = GameStateController()

    init {
        listen<AsyncPlayerConfigurationEvent> {
            gameStates.addPlayer(it)
        }

        listen<ServerListPingEvent> { event ->
            event.status = Status.builder()
                .description("Asorda Jam Entry 2025".minimessage())
                .build()
        }
    }
}


fun main() {
    Jam().bind()
}