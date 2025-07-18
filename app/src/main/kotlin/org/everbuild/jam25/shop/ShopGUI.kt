package org.everbuild.jam25.shop

import kotlin.time.Duration.Companion.milliseconds
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.item.ItemStack
import net.minestom.server.network.packet.server.play.TradeListPacket
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.listener.dropItem

class ShopGUI(name: String, val entries: List<ShopEntry>) : Inventory(InventoryType.MERCHANT, name.minimessage()) {
    val rateLimit = Cooldown(50.milliseconds)
    var selected: ShopEntry? = null
    init {
        eventNode()
            .listen { event: InventoryPreClickEvent ->
                check()
                if (event.slot != 2) return@listen
                event.isCancelled = true
                if (!rateLimit.get()) return@listen
                val entry = selected ?: return@listen
                val result = getItemStack(2)
                if (result.isAir) return@listen
                val cursor = event.player.inventory.cursorItem
                when (entry) {
                    is ShopEntry.Item -> {
                        if (!result.isSimilar(event.player.inventory.cursorItem) && !cursor.isAir) return@listen
                        if (cursor.amount() + result.amount() > result.maxStackSize()) return@listen
                        val newCursor = result.withAmount((if (cursor.isAir) 0 else cursor.amount()) + result.amount())
                        event.player.inventory.cursorItem = newCursor
                    }
                    is ShopEntry.Upgrade -> {
                        entry.callback()
                    }
                }
                setItemStack(0, getItemStack(0).withAmount(getItemStack(0).amount() - entry.left.amount()))
                if (entry.right != null) {
                    setItemStack(1, getItemStack(1).withAmount(getItemStack(1).amount() - (entry.right?.amount() ?: 0)))
                }
                setItemStack(2, ItemStack.AIR)
                check()
            }
            .listen { event: InventoryCloseEvent ->
                val itemsToDrop = listOf(getItemStack(0), getItemStack(1))
                itemsToDrop.map { event.player.inventory.addItemStack(it, TransactionOption.ALL) }.map { it ->
                    dropItem(event.player.position, it, event.player.instance!!)
                }
            }
    }
    override fun addViewer(player: Player): Boolean {
        return super.addViewer(player).also {
            player.sendPacket(TradeListPacket(
                windowId.toInt(),
                entries.map { entry ->
                    TradeListPacket.Trade(
                        entry.left,
                        entry.result,
                        entry.right,
                        false,
                        0,
                        1000,
                        0,
                        0,
                        0f,
                        0
                    )
                }.toList(),
                0,
                0,
                false,
                false
            ))
        }
    }

    fun select(id: Int, player: Player) {
        if (entries.size <= id) return
        val entry = entries[id]
        selected = entry

        val itemsToDrop = listOf(getItemStack(0), getItemStack(1))
        itemsToDrop.map {
            player.inventory.addItemStack(it, TransactionOption.ALL)
        }.map { it ->
            if (it.isAir) return@map
            dropItem(player.position, it, player.instance!!)
        }

        val leftItem = player.inventory.itemStacks.indexOfFirst { it.isSimilar(entry.left) }
        if (leftItem == -1) {
            setItemStack(0, ItemStack.AIR)
        } else {
            setItemStack(0, player.inventory.itemStacks[leftItem])
            player.inventory.setItemStack(leftItem, ItemStack.AIR)
        }

        if (entry.right != null) {
            val rightItem = player.inventory.itemStacks.indexOfFirst { it.isSimilar(entry.right!!) }
            if (rightItem == -1) {
                setItemStack(1, ItemStack.AIR)
            } else {
                setItemStack(1, player.inventory.itemStacks[rightItem])
                player.inventory.setItemStack(rightItem, ItemStack.AIR)
            }
        } else {
            setItemStack(1, ItemStack.AIR)
        }

        50.milliseconds later {
            update()
            player.inventory.update()
        }

        check()
    }

    fun check() {
        val entry = selected ?: run {
            setItemStack(2, ItemStack.AIR)
            return
        }
        val leftMatchesNow = getItemStack(0).isSimilar(entry.left) && getItemStack(0).amount() >= entry.left.amount()
        val rightMatchesNow = getItemStack(1).isSimilar(entry.right ?: ItemStack.AIR) && getItemStack(1).amount() >= (entry.right?.amount() ?: 0)
        if (leftMatchesNow && rightMatchesNow) {
            setItemStack(2, entry.result)
        } else {
            setItemStack(2, ItemStack.AIR)
        }
        update()
    }

    sealed interface ShopEntry {
        val left: ItemStack
        val right: ItemStack?
        val result: ItemStack

        data class Item(override val left: ItemStack, override val right: ItemStack?, override val result: ItemStack) : ShopEntry
        data class Upgrade(override val left: ItemStack, override val right: ItemStack?, override val result: ItemStack, val callback: () -> Unit) : ShopEntry
    }
}