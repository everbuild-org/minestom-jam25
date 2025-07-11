package org.everbuild.jam25.state

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

interface EntryGameState {
    fun addPlayer(player: Player)
    fun getInstance(): Instance
    fun getSpawn(): Pos
}