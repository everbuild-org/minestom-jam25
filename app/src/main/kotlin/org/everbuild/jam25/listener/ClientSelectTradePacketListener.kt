package org.everbuild.jam25.listener

import net.minestom.server.entity.Player
import net.minestom.server.network.packet.client.play.ClientSelectTradePacket
import org.everbuild.jam25.shop.ShopGUI

object ClientSelectTradePacketListener {
    fun listener(packet: ClientSelectTradePacket, player: Player) {
        val inv = player.openInventory ?: return
        val shop = inv as? ShopGUI ?: return
        shop.select(packet.selectedSlot, player)
    }
}