package org.everbuild.jam25.block.impl.crafting

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.ItemStack
import org.everbuild.jam25.item.impl.DigitalComponentItem
import org.everbuild.jam25.item.impl.DigitalCrafterBlockItem
import org.everbuild.jam25.item.impl.PipeCrafterBlockItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.PipeBlockItem
import org.everbuild.jam25.item.impl.SiliconItem
import org.everbuild.jam25.world.placeable.ItemConsumer

object DigitalComponentCrafterBlock : CrafterBlock(
    BlockFace.SOUTH,
    BlockFace.NORTH
) {
    override fun recipeIngredients() = listOf(
        ItemConsumer.ItemOrOil.Item(SiliconItem.createItem().withAmount(3)),
        ItemConsumer.ItemOrOil.Item(MetalScrapsItem.createItem()),
    )
    override fun recipeOutput() = ItemConsumer.ItemOrOil.Item(DigitalComponentItem.createItem())

    override fun key(): Key = Key.key("jam", "digital_component_crafter")
    override fun createItem(): ItemStack = DigitalCrafterBlockItem.createItem()
    override fun getModelId(): String? = "pipe_assembler.geo.bbmodel"

    override fun verticalHologramOffset() = 1.5
}