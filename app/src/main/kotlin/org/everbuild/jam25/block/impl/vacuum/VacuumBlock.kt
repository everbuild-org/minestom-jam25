package org.everbuild.jam25.block.impl.vacuum

import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.tag.Tag
import net.minestom.server.utils.Direction
import org.everbuild.jam25.block.api.*
import org.everbuild.jam25.block.impl.pipe.PipeBlock
import org.everbuild.jam25.block.impl.pipe.PipeBlock.asId
import org.everbuild.jam25.item.impl.VacuumBlockItem
import org.everbuild.jam25.listener.dropItemOnFloor
import org.everbuild.jam25.world.vacuum.Vacuum

object VacuumBlock : CustomBlock {
    const val INVENTORY_SIZE = 10

    val entities = hashMapOf<Instance, HashMap<Long, VacuumEntity>>()
    val state = Tag.NBT("state")
    val isBottomBlock = Tag.Boolean("isBottomBlock")

    override fun key(): Key = Key.key("jam", "vacuum")

    override fun placeBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        val blockState = BlockState(
            if (player is PlacementActor.ByPlayer) BlockFace.fromYaw(player.player.position.yaw()).toDirection()
            else Direction.SOUTH
        )
        instance.setBlock(
            position,
            Block.BARRIER
                .withTypeTag()
                .withTag(state, blockState.toNBT())
                .withTag(PipeBlock.faceCanConnectTag, BlockFace.fromDirection(blockState.facing.opposite()).name)
                .withInventory(BlockInventory(INVENTORY_SIZE))
                .withTag(isBottomBlock, true)
        )
        instance.setBlock(
            position.add(0, 1, 0),
            Block.BARRIER
                .withTypeTag()
                .withTag(isBottomBlock, false)
        )
        entities.getOrPut(instance) { hashMapOf() }.getOrPut(position.asId()) {
            VacuumEntity(blockState.facing).also {
                it.setInstance(instance, position)
            }
        }

        player.getTeam()?.game?.advanceable?.add(Vacuum(position))

        update(instance, position)
    }

    override fun breakBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        val block = instance.getBlock(position)
        val isBottomBlock = block.getTag(isBottomBlock) ?: return
        if (!isBottomBlock) {
            breakBlock(instance, position.add(0, -1, 0), player)
            return
        }
        instance.setBlock(position, Block.AIR)
        instance.setBlock(position.add(0, 1, 0), Block.AIR)
        entities[instance]?.remove(position.asId())?.remove()
        dropItemOnFloor(Pos.fromPoint(position), VacuumBlockItem.createItem(), instance)
        block.getInventory()?.items?.forEach { itemStack ->
            dropItemOnFloor(Pos.fromPoint(position), itemStack, instance)
        }

        player.getTeam()?.game?.advanceable?.removeIf {
            if (it is Vacuum && it.position == position) {
                it.drop(instance)
                true
            } else false
        }
    }

    override fun update(instance: Instance, position: BlockVec) {

    }

    data class BlockState(val facing: Direction) {
        fun toNBT(): CompoundBinaryTag {
            return CompoundBinaryTag.builder()
                .putString("facing", facing.name)
                .build()
        }

        companion object {
            fun fromNBT(nbt: CompoundBinaryTag): BlockState {
                return BlockState(
                    Direction.valueOf(nbt.getString("facing"))
                )
            }
        }
    }
}