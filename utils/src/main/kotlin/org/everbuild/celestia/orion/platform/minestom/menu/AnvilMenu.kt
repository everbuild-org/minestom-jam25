package org.everbuild.celestia.orion.platform.minestom.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.player.PlayerPacketEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryProperty
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.client.play.ClientNameItemPacket
import net.minestom.server.network.packet.server.play.WindowPropertyPacket
import org.everbuild.asorda.resources.data.items.GlobalIcons
import org.everbuild.asorda.resources.data.items.SystemIcons
import org.everbuild.celestia.orion.core.menu.adaptive.AdaptiveUIType
import org.everbuild.celestia.orion.core.menu.adaptive.dsl.AdaptiveUiDSL
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.adventure.undecorate
import org.everbuild.celestia.orion.platform.minestom.api.utils.set
import org.everbuild.celestia.orion.platform.minestom.util.listen

data class ValidatorResult(val isValid: Boolean, val message: Component) {
    companion object {
        fun valid(): ValidatorResult = ValidatorResult(true, Component.empty())
        fun invalid(message: Component): ValidatorResult = ValidatorResult(false, message)
    }
}

class AnvilMenu(private val player: Player) {
    private var inventory: Inventory = Inventory(InventoryType.ANVIL, Component.text("Anvil")).also { resetItems(it) }
    private var resultHandler: AnvilMenuResultProcessor.(String) -> Unit = { _: String -> }
    private var currentText: String? = ""
    private var validator: ((String) -> ValidatorResult)? = null

    var title: Component = Component.empty()
        set(value) {
            field = value
            resetTitle()
        }

    var text: Component = Component.empty()
        set(value) {
            field = value
            resetItems(inventory)
        }

    private var menuType: AdaptiveUIType = AdaptiveUiDSL.dsl(0) {
        layer {
            space(-58)
            gui("text_input_anvil")
        }

        titleOffset(76)
    }

    fun menuType(block: AdaptiveUiDSL.() -> Unit) {
        this.menuType = AdaptiveUiDSL.dsl(0, block)
    }

    fun then(handler: AnvilMenuResultProcessor.(String) -> Unit) {
        resultHandler = handler
    }

    fun validate(validator: (String) -> ValidatorResult) {
        this.validator = validator
    }

    private fun resetTitle() {
        inventory.title = menuType.render(title)
    }

    private fun validatorResult(): ValidatorResult {
        return validator?.let { it(currentText ?: "") } ?: ValidatorResult.valid()
    }

    private fun resetItems(inventory: Inventory) {
        val pack = SystemIcons.oversizeFill
        inventory[0] = ItemStack.of(Material.fromKey(pack.getParentMaterial()) ?: Material.AIR)
            .withItemModel(pack.getCustomModel())
            .withCustomName(Component.empty())

        inventory[2] = ItemStack.of(Material.POLISHED_BLACKSTONE_BUTTON)
            .let {
                if (currentText?.isBlank() == true) {
                    it.withItemModel(SystemIcons.oversizeFill.getCustomModel())
                        .withCustomName(
                            if (currentText?.isBlank() == true) text else Component.text(
                                currentText ?: ""
                            )
                        )

                } else if (!validatorResult().isValid) {
                    it.withItemModel(GlobalIcons.iconXRed.getCustomModel())
                        .withCustomName(
                            "§r§8[§4×§8] §7".component().append(
                                validatorResult().message.color(NamedTextColor.RED)
                                    .append("§r §8(${currentText ?: ""})".component())
                            ).undecorate(TextDecoration.ITALIC)
                        )
                } else {
                    it.withItemModel(GlobalIcons.iconOkGreen.getCustomModel())
                        .withCustomName(
                            if (currentText?.isBlank() == true) text else Component.text(
                                currentText ?: ""
                            )
                        )
                }
            }

        player.sendPacket(
            WindowPropertyPacket(
                inventory.windowId.toInt(),
                InventoryProperty.ANVIL_REPAIR_COST.property,
                0
            )
        )
    }

    internal fun open() {
        anvilGuiListeners(player)
        player.openInventory(inventory)
    }

    inner class AnvilMenuResultProcessor {
        fun closeAnvil() {
            player.closeInventory()
        }

        fun continueAsking(text: Component) {
            this@AnvilMenu.text = text
            resetItems(inventory)
        }
    }

    private fun anvilGuiListeners(player: Player) {
        val name = "anvil_gui_${player.username}"
        val node = EventNode.all(name)
        Mc.globalEvent.addChild(node)
        node.listen<PlayerPacketEvent, _> {
            if (it.entity != player) return@listen

            if (!player.isOnline) {
                Mc.globalEvent.removeChild(node)
                return@listen
            }

            when (val packet = it.packet) {
                is ClientNameItemPacket -> {
                    @Suppress("USELESS_ELVIS")
                    currentText = packet.itemName ?: ""
                    resetItems(inventory)
                }
            }
        }

        node.listen<InventoryCloseEvent, _> {
            if (it.player != player) return@listen
            Mc.globalEvent.removeChild(node)
        }

        node.listen<InventoryPreClickEvent, _> {
            if (it.player != player) return@listen
            if (it.inventory != inventory) return@listen
            it.isCancelled = true

            if (it.slot == 2) {
                if (currentText?.isBlank() == true || !validatorResult().isValid) {
                    resetItems(inventory)
                    return@listen
                }
                resultHandler(AnvilMenuResultProcessor(), currentText ?: "")
            }
        }
    }
}

fun openAnvilGui(player: Player, initializer: AnvilMenu.() -> Unit) {
    val anvilMenu = AnvilMenu(player)
    anvilMenu.initializer()
    anvilMenu.open()
}