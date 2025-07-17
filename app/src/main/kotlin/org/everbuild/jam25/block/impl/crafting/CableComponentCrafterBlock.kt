package org.everbuild.jam25.block.impl.crafting

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.ItemStack
import org.everbuild.jam25.item.impl.CableComponentItem
import org.everbuild.jam25.item.impl.CableCrafterBlockItem
import org.everbuild.jam25.item.impl.DigitalComponentItem
import org.everbuild.jam25.item.impl.PipeCrafterBlockItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.PipeBlockItem
import org.everbuild.jam25.item.impl.SiliconItem
import org.everbuild.jam25.world.placeable.ItemConsumer

object CableComponentCrafterBlock : CrafterBlock(
    BlockFace.SOUTH,
    BlockFace.NORTH
) {
    override fun recipeIngredients() = listOf(
        ItemConsumer.ItemOrOil.Item(MetalScrapsItem.createNewStack(2)),
    )
    override fun recipeOutput() = ItemConsumer.ItemOrOil.Item(CableComponentItem.createItem())

    override fun key(): Key = Key.key("jam", "cable_component_crafter")
    override fun createItem(): ItemStack = CableCrafterBlockItem.createItem()
    override fun getModelId(): String? = "pipe_assembler.geo.bbmodel"

    override fun verticalHologramOffset() = 1.5
}