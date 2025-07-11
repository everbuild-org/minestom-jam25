package org.everbuild.jam25.state

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Keyed
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

sealed interface GameState : Keyed {
    fun events(): EventNode<out Event>
    fun audience(): Audience
    fun players(): List<Player>
}