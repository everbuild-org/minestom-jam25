package org.everbuild.jam25.block.impl.shieldgenerator

import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Vec
import net.minestom.server.event.instance.InstanceUnregisterEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.tag.Tag
import net.minestom.server.utils.Direction
import org.everbuild.celestia.orion.platform.minestom.api.utils.rotateAroundYDegrees
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.Jam
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.block.api.CustomBlock
import org.everbuild.jam25.block.api.PlacementActor
import org.everbuild.jam25.block.impl.pipe.PipeBlock
import org.everbuild.jam25.block.impl.pipe.PipeBlock.asId
import org.everbuild.jam25.item.impl.BioScrapsItem
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.world.shield.generator.ShieldGeneratorConsumer

object ShieldGeneratorBlock : CustomBlock {
    val entities = hashMapOf<Instance, HashMap<Long, ShieldGeneratorEntity>>()
    val state = Tag.NBT("state")

    init {
        listen<InstanceUnregisterEvent> {
            entities.remove(it.instance)
        }
    }

    override fun placeBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        val team = player.getTeam()!!
        placeShieldGeneratorBlocks(instance, position, BlockState.DEFAULT, team)
        entities.getOrPut(instance) { hashMapOf() }.getOrPut(position.asId()) {
            ShieldGeneratorEntity(BlockState.DEFAULT.running, team.poi.shieldGenerator.direction).also {
                it.setInstance(instance, position)
            }
        }
    }

    override fun breakBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        entities[instance]?.remove(position.asId())?.remove()
        forEachGeneratorPosition(position) {
            instance.setBlock(it, Block.AIR)
        }
    }

    override fun update(instance: Instance, position: BlockVec) {

    }

    fun updateState(instance: Instance, position: BlockVec, running: Boolean) {
        val oldState = BlockState.fromNBT(instance.getBlock(position).getTag(state) as? CompoundBinaryTag ?: return)
        val updatedState = oldState.copy(running = running)
        val team = Jam.gameStates.getInGamePhase(instance)!!.teamAt(position)
        placeShieldGeneratorBlocks(instance, position, updatedState, team)
        entities[instance]?.remove(position.asId())?.remove()
        entities[instance]?.put(position.asId(), ShieldGeneratorEntity(updatedState.running, team.poi.shieldGenerator.direction).also {
            it.setInstance(instance, position)
        })
    }

    private fun placeShieldGeneratorBlocks(instance: Instance, position: BlockVec, blockState: BlockState, team: GameTeam) {
        forEachGeneratorPosition(position) {
            instance.setBlock(
                it,
                Block.BARRIER
                    .withTag(BlockController.unbreakable, true)
                    .withTag(BlockController.refillable, BioScrapsItem.id.asString())
                    .withTag(BlockController.shieldGenerator, true)
            )
        }
        instance.setBlock(
            position, Block.BARRIER
                .withTypeTag()
                .withTag(state, blockState.toNBT())
                .withTag(BlockController.shieldGenerator, true)
        )
        instance.setBlock(
            position.add(team.poi.shieldGenerator.inputDirection().vec()),
            Block.BARRIER
                .withTag(PipeBlock.faceCanConnectTag, BlockFace.fromDirection(team.poi.shieldGenerator.inputDirection()).name)
                .withTag(BlockController.unbreakable, true)
                .withTag(BlockController.refillable, BioScrapsItem.id.asString())
                .withTag(BlockController.shieldGenerator, true)
        )

        val consumer = ShieldGeneratorConsumer(team.poi.shieldGenerator, instance, position.add(BlockVec(team.poi.shieldGenerator.inputDirection().vec())))
        team.game.advanceable.add(consumer)
    }

    private fun forEachGeneratorPosition(center: BlockVec, consumer: (subBlockPosition: BlockVec) -> Unit) {
        for (generatorPosition in generatorPositions(center)) {
            consumer(generatorPosition)
        }
    }

    fun generatorPositions(center: BlockVec) = buildList {
        for (x in ((center.blockX() - 1)..(center.blockX() + 1))) {
            for (y in (center.blockY())..(center.blockY() + 2)) {
                for (z in (center.blockZ() - 1)..(center.blockZ() + 1)) {
                    add(BlockVec(x, y, z))
                }
            }
        }
    }

    override fun key(): Key = Key.key("jam", "shield_generator")

    data class BlockState(val running: Boolean) {
        fun toNBT(): CompoundBinaryTag {
            return CompoundBinaryTag.builder()
                .putBoolean("running", running)
                .build()
        }

        companion object {
            val DEFAULT = BlockState(true)

            fun fromNBT(nbt: CompoundBinaryTag): BlockState {
                return BlockState(nbt.getBoolean("running"))
            }
        }
    }
}