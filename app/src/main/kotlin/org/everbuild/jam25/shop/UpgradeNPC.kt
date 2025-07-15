package org.everbuild.jam25.shop

import java.util.concurrent.CompletableFuture
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance

class UpgradeNPC(val pos: Pos) : ShopNPC("Upgrades", SKIN_ENGINEER) {
    override fun setInstance(instance: Instance): CompletableFuture<Void?>? {
        return super.setInstance(instance, pos)
    }

    override fun openMenu(player: Player) {
        player.openInventory(ShopGUI("Upgrades", listOf(

        )))
    }
}