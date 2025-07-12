package org.everbuild.jam25

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.core.util.sendMiniMessageActionBar
import org.everbuild.celestia.orion.platform.minestom.api.Mc


open class DynamicGroup(val block: (Player) -> Boolean) {
    fun sendMiniMessage(minimessage: String) {
        forEach { it.sendMiniMessage(minimessage) }
    }

    fun sendMiniMessageActionBar(minimessage: String) {
        forEach { it.sendMiniMessageActionBar(minimessage) }
    }

    fun forEach(inlineBlock: (Player) -> Unit) {
        Mc.connection.onlinePlayers.filter(block).forEach { inlineBlock(it) }
    }

    fun setInstance(instance: InstanceContainer, pos: Pos) {
        forEach { it.setInstance(instance, pos) }
    }
}