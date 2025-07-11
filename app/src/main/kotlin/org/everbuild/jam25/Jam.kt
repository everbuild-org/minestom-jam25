package org.everbuild.jam25

import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.jam25.state.GameStateController

class Jam : OrionServer() {
    val gameStates = GameStateController()

    init {
        Mc.globalEvent
            .addChild(gameStates.eventNode())
            .addChild(PingResponder.eventNode())
    }
}


fun main() {
    Jam().bind()
}