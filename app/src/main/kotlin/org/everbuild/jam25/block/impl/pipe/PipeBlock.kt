package org.everbuild.jam25.block.impl.pipe

import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag
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

object PipeBlock : CustomBlock {
    val entities = hashMapOf<Instance, HashMap<BlockVec, MutableSet<Entity>>>()
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
        player: Player?
    ) {
        instance.setBlock(position, Block.BARRIER
            .withTypeTag()
            .withTag(state, BlockState.EMPTY.toNBT())
        )

        update(instance, position)
    }

    override fun breakBlock(
        instance: Instance,
        position: BlockVec,
        player: Player?
    ) {
        instance.setBlock(position, Block.AIR)
        entities[instance]?.remove(position)?.forEach { it.remove() }
    }

    override fun update(
        instance: Instance,
        position: BlockVec
    ) {
        println("Updating $position")
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

        val oldState = BlockState.fromNBT(instance.getBlock(position).getTag(state) as? CompoundBinaryTag ?: return)
        val state = BlockState(connectingTo.toList())

        instance.setBlock(position, Block.BARRIER.withTypeTag().withTag(this.state, state.toNBT()))

        if (oldState == state) {
            return
        }

        val entities = entities.getOrPut(instance) { hashMapOf() }.getOrPut(position) { hashSetOf() }
        entities.forEach { it.remove() }

        state.connectedTo.forEach { (face, type) ->
            if (type == BlockStateType.END) {
                entities.add(PipePartEntity("plate", face))
            }
            entities.add(PipePartEntity("conn", face))
        }

        if (state.connectedTo.size != 2 || state.connectedTo.all { v -> state.connectedTo.any { it.first == v.first.oppositeFace }}) {
            entities.add(PipePartEntity("middle", null))
        }

        entities.forEach { it.setInstance(instance, position) }
        entities.forEach {
            println("Placed entity ${it.entityType} at ${it.position}")
        }

//        BlockController.updateAround(instance, position)
    }

    fun shouldConnect(block: Block, face: BlockFace): Boolean {
        return BlockController.getBlock(block) == PipeBlock
    }

    enum class BlockStateType {
        CONNECTING,
        END
    }

    data class BlockState(val connectedTo: List<Pair<BlockFace, BlockStateType>>) {

        fun toNBT(): CompoundBinaryTag {
            return CompoundBinaryTag.builder()
                .put("connectedTo", ListBinaryTag.listBinaryTag(BinaryTagTypes.COMPOUND, connectedTo.map {
                    CompoundBinaryTag.builder()
                        .putString("face", it.first.name)
                        .putString("type", it.second.name)
                        .build()
                }))
                .build()
        }

        companion object {
            val EMPTY = BlockState(emptyList())

            fun fromNBT(nbt: CompoundBinaryTag): BlockState {
                return BlockState(nbt.getList("connectedTo").map {
                    val nbtPair = it as CompoundBinaryTag
                    Pair(
                        BlockFace.valueOf(nbtPair.getString("face")),
                        BlockStateType.valueOf(nbtPair.getString("type"))
                    )
                })
            }
        }
    }
}