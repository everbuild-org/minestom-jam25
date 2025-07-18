package org.everbuild.jam25.block.impl.storageTank

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
import org.everbuild.jam25.item.impl.StorageTankBlockItem
import org.everbuild.jam25.item.impl.VacuumBlockItem
import org.everbuild.jam25.listener.dropItemOnFloor
import org.everbuild.jam25.world.storage.Storage
import org.everbuild.jam25.world.vacuum.Vacuum

object StorageTankBlock : CustomBlock {
    val entities = hashMapOf<Instance, HashMap<Long, StorageTankEntity>>()
    val state = Tag.NBT("state")
    val isBottomBlock = Tag.Boolean("isBottomBlock")

    override fun key(): Key = Key.key("jam", "storage_tank")

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
                .withTag(isBottomBlock, true)
        )
        instance.setBlock(
            position.add(0, 1, 0),
            Block.BARRIER
                .withTypeTag()
                .withTag(isBottomBlock, false)
        )
        entities.getOrPut(instance) { hashMapOf() }.getOrPut(position.asId()) {
            StorageTankEntity(blockState.facing).also {
                it.setInstance(instance, position)
            }
        }

        player.getTeam()?.game?.advanceable?.add(Storage(position, player.getTeam() ?: return))

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
        dropItemOnFloor(Pos.fromPoint(position), StorageTankBlockItem.createItem(), instance)

        player.getTeam()?.game?.advanceable?.removeIf {
            if (it is Storage && it.position == position) {
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