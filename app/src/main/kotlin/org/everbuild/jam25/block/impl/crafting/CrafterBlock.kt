package org.everbuild.jam25.block.impl.crafting

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import net.minestom.server.utils.Direction
import org.everbuild.jam25.block.api.CustomBlock
import org.everbuild.jam25.block.api.PlacementActor
import org.everbuild.jam25.block.impl.pipe.PipeBlock
import org.everbuild.jam25.block.impl.pipe.PipeBlock.asId
import org.everbuild.jam25.listener.dropItemOnFloor
import org.everbuild.jam25.world.crafting.Crafter
import org.everbuild.jam25.world.placeable.ItemConsumer

abstract class CrafterBlock(
    val inputSide: BlockFace,
    val outputSide: BlockFace
) : CustomBlock {
    val entities = hashMapOf<Instance, HashMap<Long, CrafterEntity>>()
    val state = Tag.NBT("state")

    override fun placeBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        val blockState = BlockState(
            if (player is PlacementActor.ByPlayer) BlockFace.fromYaw(player.player.position.yaw()).toDirection()
            else Direction.SOUTH
        )
        val rotatedInputSide = inputSide.rotated(BlockFace.fromDirection(blockState.facing))
        val rotatedOutputSide = outputSide.rotated(BlockFace.fromDirection(blockState.facing))
        instance.setBlock(
            position,
            Block.BARRIER
                .withTypeTag()
                .withTag(state, blockState.toNBT())
                .withTag(PipeBlock.faceCanConnectTag, "${rotatedInputSide.name}|${rotatedOutputSide.name}")
        )
        entities.getOrPut(instance) { hashMapOf() }.getOrPut(position.asId()) {
            CrafterEntity(getModelId(), blockState.facing).also {
                it.setInstance(instance, position)
                it.anim.playRepeat("idle")
            }
        }

        player.getTeam()?.game?.let { game ->
            game.advanceable.add(
                Crafter(
                    position,
                    recipeIngredients(),
                    recipeOutput(),
                    game,
                    inputSide,
                    verticalHologramOffset()
                )
            )
        }

        update(instance, position)
    }

    private fun BlockFace.rotated(south: BlockFace) = when(this) {
        BlockFace.SOUTH -> south
        BlockFace.NORTH -> south.oppositeFace
        BlockFace.WEST -> south.rotateRight()
        BlockFace.EAST -> south.rotateRight().rotateRight().rotateRight()
        else -> this
    }

    private fun BlockFace.rotateRight() = when(this) {
        BlockFace.NORTH -> BlockFace.EAST
        BlockFace.EAST -> BlockFace.SOUTH
        BlockFace.SOUTH -> BlockFace.WEST
        BlockFace.WEST -> BlockFace.NORTH
        else -> this
    }

    override fun breakBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        instance.setBlock(
            position,
            Block.AIR
        )

        entities[instance]?.remove(position.asId())?.remove()
        dropItemOnFloor(Pos.fromPoint(position), createItem(), instance)

        player.getTeam()?.game?.advanceable?.removeIf {
            if (it is Crafter && it.position == position) {
                it.drop(instance)
                true
            } else false
        }
    }

    override fun update(instance: Instance, position: BlockVec) {
    }

    fun craft(instance: Instance, position: BlockVec, then: () -> Unit) {
        val crafter = entities[instance]?.get(position.asId()) ?: return
        crafter.craft(then)
    }

    abstract fun getModelId(): String?
    abstract fun createItem(): ItemStack
    abstract fun recipeIngredients(): List<ItemConsumer.ItemOrOil>
    abstract fun recipeOutput(): ItemConsumer.ItemOrOil
    open fun verticalHologramOffset(): Double = 2.0

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