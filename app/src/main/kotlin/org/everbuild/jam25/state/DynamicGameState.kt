package org.everbuild.jam25.state

import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

interface DynamicGameState {
    fun addPlayer(player: Player)
    fun getInstance(): Instance
}