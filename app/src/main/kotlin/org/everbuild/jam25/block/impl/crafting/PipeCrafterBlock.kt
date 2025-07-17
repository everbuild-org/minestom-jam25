package org.everbuild.jam25.block.impl.crafting

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.ItemStack
import org.everbuild.jam25.item.impl.PipeCrafterBlockItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.PipeBlockItem
import org.everbuild.jam25.world.placeable.ItemConsumer

object PipeCrafterBlock : CrafterBlock(
    BlockFace.SOUTH,
    BlockFace.NORTH
) {
    override fun recipeIngredients() = listOf(ItemConsumer.ItemOrOil.Item(MetalScrapsItem.createItem().withAmount(2)))
    override fun recipeOutput() = ItemConsumer.ItemOrOil.Item(PipeBlockItem.createItem().withAmount(2))

    override fun key(): Key = Key.key("jam", "pipe_crafter")
    override fun createItem(): ItemStack = PipeCrafterBlockItem.createItem()
    override fun getModelId(): String? = "pipe_assembler.geo.bbmodel"

    override fun verticalHologramOffset() = 1.5
}