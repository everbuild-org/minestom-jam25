package org.everbuild.jam25.block.api

import net.kyori.adventure.key.Keyed
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack

interface CustomBlock : Keyed {
    fun placeBlock(instance: Instance, position: BlockVec, player: PlacementActor)
    fun breakBlock(instance: Instance, position: BlockVec, player: PlacementActor)
    fun update(instance: Instance, position: BlockVec)

    fun Block.withTypeTag(): Block {
        return this.withTag(BlockController.typeTag, this@CustomBlock.key().asString())
    }
}