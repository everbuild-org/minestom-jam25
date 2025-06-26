package org.everbuild.celestia.orion.platform.minestom.menu

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.util.RGBLike
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.EnchantmentList
import net.minestom.server.item.enchant.Enchantment
import net.minestom.server.registry.RegistryKey
import org.everbuild.asorda.resources.data.api.item.AbstractItemResource
import org.everbuild.celestia.orion.core.OrionCore
import net.minestom.server.registry.DynamicRegistry
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.menu.MenuDSL
import org.everbuild.celestia.orion.core.translation.TranslationContext
import org.everbuild.celestia.orion.platform.minestom.api.utils.dropItem
import org.everbuild.celestia.orion.platform.minestom.api.utils.get
import org.everbuild.celestia.orion.platform.minestom.api.utils.logger
import org.everbuild.celestia.orion.platform.minestom.api.utils.set
import org.everbuild.celestia.orion.platform.minestom.initializer.SkinCache
import org.everbuild.celestia.orion.platform.minestom.initializer.asHeadProfile
import org.everbuild.celestia.orion.platform.minestom.util.c
import org.everbuild.celestia.orion.platform.minestom.util.t
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import kotlin.time.Duration.Companion.milliseconds

class MenuItem(var itemStack: ItemStack, private val menu: Menu) {
    private var clickFunction: Function<MenuItem, Boolean>? = null
    private var fineGrainedClickListener: BiConsumer<ReInventoryClickEvent, MenuItem>? = null
    private var onInventoryStateChange: Consumer<MenuItem>? = null
    private var onItemStateChange: Consumer<MenuItem>? = null
    private var onInventoryStateButNotItemStateChange: BiConsumer<MenuItem, MenuItem?>? = null
    private var postInit: Consumer<MenuItem>? = null
    private val slots: MutableList<Int> = ArrayList()
    private var lazyHeadProfile: Deferred<PlayerSkin?>? = null
    private var open = false

    fun clickListener(clickFunction: Function<MenuItem, Boolean>?): MenuItem {
        this.clickFunction = clickFunction
        return this
    }

    fun fineGrainedClickListener(fineGrainedClickListener: BiConsumer<ReInventoryClickEvent, MenuItem>?): MenuItem {
        this.fineGrainedClickListener = fineGrainedClickListener
        return this
    }

    fun onInventoryStateChange(onInventoryStateChange: Consumer<MenuItem>?): MenuItem {
        this.onInventoryStateChange = onInventoryStateChange
        return this
    }

    fun open(open: Boolean): MenuItem {
        this.open = open
        return this
    }

    fun onItemStateChange(onItemStateChange: Consumer<MenuItem>?): MenuItem {
        this.onItemStateChange = onItemStateChange
        return this
    }

    fun onInventoryStateButNotItemStateChange(onInventoryStateButNotItemStateChange: BiConsumer<MenuItem, MenuItem?>?): MenuItem {
        this.onInventoryStateButNotItemStateChange = onInventoryStateButNotItemStateChange
        return this
    }

    fun postInit(postInit: Consumer<MenuItem>?): MenuItem {
        this.postInit = postInit
        return this
    }

    private fun slot(slot: List<Int>): MenuItem {
        slots.addAll(slot)
        return this
    }

    fun lazyHeadProfile(lazyHeadProfile: Deferred<PlayerSkin?>?): MenuItem {
        this.lazyHeadProfile = lazyHeadProfile
        return this
    }

    fun modify(consumer: Consumer<Builder?>) {
        val builder = Builder(this)
        consumer.accept(builder)
        builder.buildInto(this)
        for (slot in slots) {
            if (slot >= menu.inventory.size) {
                logger.warn("Slot " + slot + " is out of bounds! (Inventory size: " + menu.inventory.size + ")")
                continue
            }
            menu.inventory[slot] = itemStack
        }
    }

    fun click(event: ReInventoryClickEvent) {
        if (clickFunction != null) {
            event.event.isCancelled = clickFunction!!.apply(this)
        }
        if (fineGrainedClickListener != null) {
            fineGrainedClickListener!!.accept(event, this)
        }
    }

    fun onInventoryStateChange(slot: Int, other: MenuItem?) {
        if (onInventoryStateChange != null) {
            onInventoryStateChange!!.accept(this)
        }
        if (onItemStateChange != null && slots.contains(slot)) {
            onItemStateChange!!.accept(this)
        }
        if (onInventoryStateButNotItemStateChange != null && !slots.contains(slot)) {
            onInventoryStateButNotItemStateChange!!.accept(this, other)
        }
    }

    override fun toString(): String {
        return itemStack.toString()
    }

    fun dropIfApplicable(slot: Int) {
        if (!open) return
        val item = menu.inventory[slot]
        if (item.material() == Material.AIR) return
        if (item.amount() == 0) return
        val remaining = menu.player.inventory.addItemStack(item, TransactionOption.ALL)
        if (remaining.isAir) return
        menu.player.instance.dropItem(remaining, menu.player.position)
    }

    fun onPostInit() {
        val startTime = System.currentTimeMillis()
        if (lazyHeadProfile != null)
            OrionCore.scope.launch {
                val skin = lazyHeadProfile?.await() ?: return@launch
                lazyHeadProfile = null
                if (startTime - System.currentTimeMillis() < 20 && menu.player.openInventory != menu.inventory)
                    delay(20.milliseconds)
                if (menu.player.openInventory != menu.inventory) return@launch
                slots.forEach { slot ->
                    menu.updateItem(slot) { item ->
                        item.modify { stack ->
                            stack.with(DataComponents.PROFILE, skin.asHeadProfile())
                        }
                    }
                }
            }

        if (postInit != null) {
            postInit!!.accept(this)
        }
    }

    class Builder {
        private var itemStack: ItemStack
        private val menu: Menu
        private val slot: MutableList<Int> = ArrayList()
        private var clickFunction: Function<MenuItem, Boolean>? = null
        private var onInventoryStateChange: Consumer<MenuItem>? = null
        private var onItemStateChange: Consumer<MenuItem>? = null
        private var onInventoryStateButNotItemStateChange: BiConsumer<MenuItem, MenuItem?>? = null
        private var fineGrainedClickListener: BiConsumer<ReInventoryClickEvent, MenuItem>? = null
        private var postInit: Consumer<MenuItem>? = null
        private var open = false
        private var lazyHeadProfile: Deferred<PlayerSkin?>? = null

        constructor(material: Material, menu: Menu) {
            itemStack = ItemStack.of(material)
            this.menu = menu
        }

        constructor(item: MenuItem) {
            itemStack = item.itemStack
            menu = item.menu
            clickFunction = item.clickFunction
            fineGrainedClickListener = item.fineGrainedClickListener
            onInventoryStateChange = item.onInventoryStateChange
            onItemStateChange = item.onItemStateChange
            onInventoryStateButNotItemStateChange = item.onInventoryStateButNotItemStateChange
            postInit = item.postInit
            open = item.open
            slot.addAll(item.slots)
            lazyHeadProfile = item.lazyHeadProfile
        }

        @MenuDSL
        fun skullName(playerHeadPlayerName: String): Builder {
            if (SkinCache.isOnFile(playerHeadPlayerName)) {
                itemStack = itemStack
                    .with(DataComponents.PROFILE, SkinCache.getSkin(playerHeadPlayerName).asHeadProfile())
            } else {
                lazyHeadProfile = SkinCache.lazy(playerHeadPlayerName)
            }
            return this
        }

        @MenuDSL
        fun skullOf(player: OrionPlayer): Builder {
            if (SkinCache.isOnFile(player.name)) {
                itemStack = itemStack
                    .with(DataComponents.PROFILE, SkinCache.getSkin(player.name).asHeadProfile())
            } else {
                lazyHeadProfile = SkinCache.lazy(player.name)
            }
            return this

        }

        @MenuDSL
        fun slot(slot: Int): Builder {
            this.slot.add(slot)
            return this
        }

        @MenuDSL
        fun slots(): List<Int> {
            return slot
        }

        @MenuDSL
        fun material(material: Material): Builder {
            itemStack = itemStack.withMaterial(material)
            return this
        }

        @MenuDSL
        fun material(material: AbstractItemResource): Builder {
            itemStack = itemStack
                .withMaterial(Material.fromKey(material.getParentMaterial()) ?: return this@Builder)
                .withItemModel(material.getCustomModel())
                .withoutExtraTooltip()
            return this
        }

        @MenuDSL
        fun amount(amount: Int): Builder {
            itemStack = itemStack.withAmount(amount)
            return this
        }

        private fun undecorate(component: Component): Component {
            return Component.text().style(Style.style().decoration(TextDecoration.ITALIC, false).build())
                .append(component).build()
        }

        @MenuDSL
        fun name(name: String): Builder {
            itemStack = itemStack.with(DataComponents.CUSTOM_NAME, undecorate(menu.player.c(name)))
            return this
        }

        @MenuDSL
        fun name(name: String, replacement: (TranslationContext) -> Unit): Builder {
            itemStack = itemStack.with(
                DataComponents.CUSTOM_NAME,
                undecorate(menu.player.t(name).also(replacement).toComponent())
            )
            return this
        }

        @MenuDSL
        fun fullLore(vararg lore: String): Builder {
            val loreList = ArrayList<Component>()
            for (line in lore) {
                loreList.add(undecorate(menu.player.c(line)))
            }
            itemStack = itemStack.with(DataComponents.LORE, loreList)
            return this
        }

        @MenuDSL
        fun fullLore(replacement: (TranslationContext) -> Unit, vararg lore: String): Builder {
            val loreList = ArrayList<Component>()
            for (line in lore) {
                menu.player.t(line).also(replacement).toComponent().also { loreList.add(undecorate(it)) }
            }
            itemStack = itemStack.with(DataComponents.LORE, loreList)
            return this
        }

        @MenuDSL
        fun lore(lore: String): Builder {
            val loreList = ArrayList<Component>()
            if (itemStack.has(DataComponents.LORE)) {
                loreList.addAll(itemStack.get(DataComponents.LORE)!!)
            }

            loreList.add(undecorate(menu.player.c(lore)))
            itemStack = itemStack.with(DataComponents.LORE, loreList)
            return this
        }

        @MenuDSL
        fun lore(lore: String, replacement: (TranslationContext) -> Unit): Builder {
            val loreList = ArrayList<Component>()
            if (itemStack.has(DataComponents.LORE)) {
                loreList.addAll(itemStack.get(DataComponents.LORE)!!)
            }

            loreList.add(undecorate(menu.player.t(lore).also(replacement).toComponent()))
            itemStack = itemStack.with(DataComponents.LORE, loreList)
            return this
        }

        @MenuDSL
        fun enchant(): Builder {
            itemStack = itemStack
                .with(
                    DataComponents.ENCHANTMENTS, EnchantmentList(
                        mapOf(
                            Enchantment.THORNS to 1
                        )
                    )
                )
            return this
        }

        @MenuDSL
        fun unbreakable(): Builder {
            itemStack = itemStack.with(DataComponents.UNBREAKABLE)
            return this
        }

        @MenuDSL
        fun enchant(enchantment: RegistryKey<Enchantment>, level: Int): Builder {
            val enchantments = itemStack.get(DataComponents.ENCHANTMENTS) ?: EnchantmentList.EMPTY
            itemStack = itemStack.with(DataComponents.ENCHANTMENTS, enchantments.with(enchantment, level))

            return this
        }

        @MenuDSL
        fun enchant(enchantment: RegistryKey<Enchantment>): Builder {
            enchant(enchantment, 1)
            return this
        }

        @MenuDSL
        fun customModelData(
            floats: List<Float>,
            flags: List<Boolean>,
            strings: List<String>,
            colors: List<RGBLike>
        ): Builder {
            itemStack = itemStack.withCustomModelData(floats, flags, strings, colors)
            return this
        }

        @MenuDSL
        fun itemModel(data: String): Builder {
            itemStack = itemStack.withItemModel(data)
            return this
        }

        @MenuDSL
        fun modify(consumer: (ItemStack) -> ItemStack): Builder {
            itemStack = consumer.invoke(itemStack)
            return this
        }

        @MenuDSL
        fun then(clickFunction: Consumer<MenuItem>): Builder {
            this.clickFunction = Function { item: MenuItem ->
                clickFunction.accept(item)
                true
            }
            return this
        }

        @MenuDSL
        fun then(clickFunction: Consumer<MenuItem>, cancel: Boolean): Builder {
            this.clickFunction = Function { item: MenuItem ->
                clickFunction.accept(item)
                cancel
            }
            return this
        }

        @MenuDSL
        fun whenInventoryStateChanges(onInventoryStateChange: Consumer<MenuItem>): Builder {
            this.onInventoryStateChange = onInventoryStateChange
            return this
        }

        @MenuDSL
        fun whenItemStateChanges(onItemStateChange: Consumer<MenuItem>): Builder {
            this.onItemStateChange = onItemStateChange
            return this
        }

        @MenuDSL
        fun whenOtherItemStateChanges(onInventoryStateButNotItemStateChange: BiConsumer<MenuItem, MenuItem?>): Builder {
            this.onInventoryStateButNotItemStateChange = onInventoryStateButNotItemStateChange
            return this
        }

        @MenuDSL
        fun postInit(postInit: Consumer<MenuItem>): Builder {
            this.postInit = postInit
            return this
        }

        @MenuDSL
        fun onClick(clickFunction: Consumer<MenuItem>): Builder {
            return then(clickFunction)
        }

        @MenuDSL
        fun thenWithResult(clickFunction: Function<MenuItem, Boolean>): Builder {
            this.clickFunction = clickFunction
            return this
        }

        @MenuDSL
        fun thenFine(clickFunction: Consumer<ReInventoryClickEvent>): Builder {
            fineGrainedClickListener =
                BiConsumer { event: ReInventoryClickEvent, _: MenuItem -> clickFunction.accept(event) }
            return this
        }

        @MenuDSL
        fun thenFineWithItem(clickFunction: BiConsumer<ReInventoryClickEvent, MenuItem>): Builder {
            fineGrainedClickListener = clickFunction
            return this
        }

        @MenuDSL
        fun open(): Builder {
            open = true
            return thenWithResult { false }
        }

        fun build(): MenuItem {
            return MenuItem(itemStack, menu)
                .lazyHeadProfile(lazyHeadProfile)
                .clickListener(clickFunction)
                .fineGrainedClickListener(fineGrainedClickListener)
                .onInventoryStateChange(onInventoryStateChange)
                .onItemStateChange(onItemStateChange)
                .onInventoryStateButNotItemStateChange(onInventoryStateButNotItemStateChange)
                .postInit(postInit)
                .open(open)
                .slot(slot)
        }

        fun buildInto(item: MenuItem) {
            item.itemStack = itemStack
            item.fineGrainedClickListener = fineGrainedClickListener
            item.clickFunction = clickFunction
            item.onInventoryStateChange = onInventoryStateChange
            item.onItemStateChange = onItemStateChange
            item.onInventoryStateButNotItemStateChange = onInventoryStateButNotItemStateChange
            item.postInit = postInit
            item.lazyHeadProfile = lazyHeadProfile
            item.slots.clear()
            item.slots.addAll(slot)
            item.open = open
        }
    }
}