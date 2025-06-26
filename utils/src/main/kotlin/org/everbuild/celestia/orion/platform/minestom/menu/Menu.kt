@file:Suppress("MemberVisibilityCanBePrivate")

package org.everbuild.celestia.orion.platform.minestom.menu

import com.google.common.collect.Lists
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.everbuild.celestia.orion.core.menu.MenuDSL
import org.everbuild.celestia.orion.core.menu.adaptive.AdaptiveUIType
import org.everbuild.celestia.orion.core.menu.adaptive.dsl.AdaptiveUiDSL
import org.everbuild.celestia.orion.core.translation.TranslationContext
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.set
import org.everbuild.celestia.orion.platform.minestom.util.t

abstract class Menu(
    val player: Player,
    nameI18NKey: String,
    val rows: Int,
    replacements: (TranslationContext) -> Unit = {}
) {
    private var doFiller: Boolean = false
    lateinit var inventory: Inventory
    protected val title = player.t(nameI18NKey).also(replacements).toComponent()
    protected var items: Array<MenuItem?> = arrayOfNulls(rows * 9)
    protected var itemBuilders: MutableList<MenuItem.Builder> = Lists.newArrayList()
    protected var dropOpenItems = true
    private var menuType: AdaptiveUIType = AdaptiveUiDSL.dsl(rows) {
        guiSciFiLayer()
        centerTitle()
    }

    init {
        setItems()
    }

    open fun open() {
        if (!preflightCheck()) return

        inventory = Inventory(
            when (rows) {
                1 -> InventoryType.CHEST_1_ROW
                2 -> InventoryType.CHEST_2_ROW
                3 -> InventoryType.CHEST_3_ROW
                4 -> InventoryType.CHEST_4_ROW
                5 -> InventoryType.CHEST_5_ROW
                6 -> InventoryType.CHEST_6_ROW
                else -> InventoryType.CHEST_6_ROW
            },
            menuType.render(title)
        )
        var menuIsClosed = false
        val listener = EventListener.builder(InventoryPreClickEvent::class.java)
            .handler { event ->
                if (event.slot == -999) {
                    onClickOutside(event.slot)
                    return@handler
                }
                onClick(event)
            }
            .expireWhen { !inventory.isViewer(player) }
            .build()

        val closeListener = EventListener.builder(InventoryCloseEvent::class.java)
            .handler { event ->
                if (event.inventory != inventory) return@handler
                onClose()
                menuIsClosed = true
            }
            .expireWhen { menuIsClosed }
            .build()

        Mc.globalEvent.addListener(listener)
        Mc.globalEvent.addListener(closeListener)

        for (itemBuilder in itemBuilders) {
            val item = itemBuilder.build()
            for (slot in itemBuilder.slots()) {
                items[slot] = item
                inventory[slot] = item.itemStack
            }
        }
        player.openInventory(inventory)

        for (item in items) {
            item?.onPostInit()
        }

        afterOpen()
    }

    open fun close() {
        player.closeInventory()
    }

    @MenuDSL
    protected fun item(vararg slot: Int): MenuItem.Builder {
        var itemBuilder = MenuItem.Builder(Material.AIR, this)
        for (j in slot) itemBuilder = itemBuilder.slot(j)
        itemBuilders.add(itemBuilder)
        return itemBuilder
    }

    @MenuDSL
    protected fun itemRange(start: Int, end: Int): MenuItem.Builder {
        var itemBuilder = MenuItem.Builder(Material.AIR, this)
        for (i in start until end) itemBuilder = itemBuilder.slot(i)
        itemBuilders.add(itemBuilder)
        return itemBuilder
    }

    @MenuDSL
    protected fun filler() {
        doFiller = true
    }

    @MenuDSL
    protected fun filler(start: Int, end: Int) {
        itemBuilders.add(0, itemRange(start, end).material(fillerMaterial).name(" "))
    }

    @MenuDSL
    protected fun type(block: AdaptiveUiDSL.() -> Unit) {
        this.menuType = AdaptiveUiDSL.dsl(rows, block)
    }

    open fun onClick(event: InventoryPreClickEvent) {
        if (event.inventory != inventory) return
        event.isCancelled = true
        if (event.slot == -999) {
            return
        }
        val item = items[event.slot]
        item?.click(ReInventoryClickEvent(event.slot, event.click, event))
        onStateChange(item, event.slot)
    }

    open fun onStateChange(item: MenuItem?, slot: Int) {
        for (eventItem in items) {
            if (eventItem == null) continue
            eventItem.onInventoryStateChange(slot, item)
        }
    }

    fun updateItem(slot: Int, lambda: (MenuItem.Builder) -> Unit) {
        val item = items[slot]?.let { item -> MenuItem.Builder(item) } ?: item(slot)
        lambda(item)
        items[slot] = item.build()
        inventory[slot] = items[slot]?.itemStack ?: ItemStack.AIR
        items[slot]?.onPostInit()
    }

    open fun onClickOutside(slot: Int) {
        onStateChange(null, slot)
    }

    open fun click(event: InventoryPreClickEvent) {
        onClick(event)
    }

    private fun internalOnClose() {
        if (!dropOpenItems) return
        var slot = -1
        for (item in items) {
            slot++
            if (item == null) continue
            item.dropIfApplicable(slot)
        }
    }

    open fun onClose() {
        internalOnClose()
    }

    open fun afterOpen() {}

    protected fun setItems() {}
    protected val fillerMaterial: Material
        get() = Material.GRAY_STAINED_GLASS_PANE

    protected fun preflightCheck(): Boolean = true
}