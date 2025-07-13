package org.everbuild.jam25.block.impl

import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.block.api.CustomBlock

object PipeBlock : CustomBlock {
    override fun key(): Key = Key.key("jam", "pipe")

    override fun placeBlock(
        instance: Instance,
        position: BlockVec,
        player: Player
    ) {
        instance.setBlock(position, Block.BARRIER.withTypeTag())
    }

    override fun breakBlock(
        instance: Instance,
        position: BlockVec,
        player: Player
    ) {
        instance.setBlock(position, Block.AIR)
    }

    override fun update(
        instance: Instance,
        position: BlockVec
    ) {
    }
}