package org.everbuild.jam25.block.impl.pipe

import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.instance.InstanceUnregisterEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.tag.Tag
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.block.api.CustomBlock
import org.everbuild.jam25.util.MinestomNBT

object PipeBlock : CustomBlock {
    val entities = hashMapOf<Instance, HashMap<BlockVec, Set<Entity>>>()
    val state = Tag.NBT("state")

    init {
        listen<InstanceUnregisterEvent> {
            entities.remove(it.instance)
        }
    }

    override fun key(): Key = Key.key("jam", "pipe")

    override fun placeBlock(
        instance: Instance,
        position: BlockVec,
        player: Player
    ) {
        instance.setBlock(position, Block.BARRIER
            .withTypeTag()
            .withTag(state, MinestomNBT.encodeToCompoundTag(BlockState.EMPTY))
        )

        update(instance, position)
    }

    override fun breakBlock(
        instance: Instance,
        position: BlockVec,
        player: Player
    ) {
        instance.setBlock(position, Block.AIR)
        entities[instance]?.remove(position)?.forEach { it.remove() }
    }

    override fun update(
        instance: Instance,
        position: BlockVec
    ) {
        val connectingTo = BlockFace.entries
            .filter { dir -> shouldConnect(
                instance.getBlock(position.relative(dir)),
                dir.oppositeFace
            )}
            .map { dir -> dir to BlockStateType.CONNECTING }
            .toMutableSet()

        if (connectingTo.isEmpty()) {
            connectingTo.add(BlockFace.NORTH to BlockStateType.END)
            connectingTo.add(BlockFace.SOUTH to BlockStateType.END)
        }

        if (connectingTo.size == 1) {
            connectingTo.add(connectingTo.first().first.oppositeFace to BlockStateType.END)
        }

        val oldState = MinestomNBT.decodeFromCompoundTag<BlockState>(
            instance.getBlock(position).getTag(state) as? CompoundBinaryTag ?: return
        )
        val state = BlockState(connectingTo)

        if (oldState == state) {
            return
        }

        val entities = entities.getOrPut(instance) { hashMapOf() }.getOrPut(position) { hashSetOf() }
        entities.forEach { it.remove() }

        state.connectedTo.forEach { (face, type) ->

        }
    }

    fun shouldConnect(block: Block, face: BlockFace): Boolean {
        return BlockController.getBlock(block) == PipeBlock
    }

    enum class BlockStateType {
        CONNECTING,
        END
    }

    @Serializable
    data class BlockState(val connectedTo: Set<Pair<BlockFace, BlockStateType>>) {

        companion object {
            val EMPTY = BlockState(emptySet())
        }
    }
}