package org.everbuild.jam25.block.impl.crafting

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.ItemStack
import org.everbuild.jam25.item.impl.CableComponentItem
import org.everbuild.jam25.item.impl.DigitalComponentItem
import org.everbuild.jam25.item.impl.PipeCrafterBlockItem
import org.everbuild.jam25.item.impl.Missile1Item
import org.everbuild.jam25.item.impl.MissileCrafterBlockItem
import org.everbuild.jam25.world.placeable.ItemConsumer

object MissileCrafterBlock : CrafterBlock(
    BlockFace.SOUTH,
    BlockFace.NORTH
) {
    override fun recipeIngredients() = listOf(
        ItemConsumer.ItemOrOil.Item(DigitalComponentItem.createNewStack(1)),
        ItemConsumer.ItemOrOil.Item(CableComponentItem.createNewStack(1)),
    )
    override fun recipeOutput() = ItemConsumer.ItemOrOil.Item(Missile1Item.createItem().withAmount(1))

    override fun key(): Key = Key.key("jam", "missile_crafter")
    override fun createItem(): ItemStack = MissileCrafterBlockItem.createItem()
    override fun getModelId(): String? = "basic_missile_assembler.bbmodel"

    override fun verticalHologramOffset() = 1.0
}