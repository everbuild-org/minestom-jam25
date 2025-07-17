package org.everbuild.jam25.block.impl.launcher

import kotlin.time.Duration.Companion.seconds
import net.kyori.adventure.key.Key
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.utils.Direction
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.jam25.block.api.*
import org.everbuild.jam25.block.impl.pipe.PipeBlock
import org.everbuild.jam25.block.impl.pipe.PipeBlock.asId
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.impl.MissileLauncherItem
import org.everbuild.jam25.item.impl.VacuumBlockItem
import org.everbuild.jam25.listener.dropItemOnFloor
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.world.launcher.Launcher
import org.everbuild.jam25.world.vacuum.Vacuum

object MissileLauncherBlock : CustomBlock {
    val entities = hashMapOf<Instance, HashMap<Long, MissileLauncherEntity>>()

    override fun key(): Key = Key.key("jam", "launcher")

    override fun placeBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        instance.setBlock(
            position,
            Block.BARRIER
                .withTypeTag()
                .withTag(PipeBlock.faceCanConnectTag, BlockFace.entries.joinToString("|") { it.toString() })
        )
        entities.getOrPut(instance) { hashMapOf() }.getOrPut(position.asId()) {
            MissileLauncherEntity().also {
                it.setInstance(instance, position)
            }
        }

        player.getTeam()?.let {
            it.game.advanceable.add(Launcher(position, it))
        }

        update(instance, position)
    }

    override fun breakBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        instance.setBlock(position, Block.AIR)
        entities[instance]?.remove(position.asId())?.remove()
        dropItemOnFloor(Pos.fromPoint(position), MissileLauncherItem.createItem(), instance)

        player.getTeam()?.game?.advanceable?.removeIf {
            if (it is Launcher && it.pos == position) {
                it.drop(instance)
                true
            } else false
        }
    }

    override fun update(instance: Instance, position: BlockVec) {

    }

    fun trySpawn(
        instance: Instance,
        position: BlockVec,
        target: BlockVec,
        missile: CustomBlock,
        team: GameTeam,
        then: () -> Unit
    ) {
        val entity = entities[instance]?.get(position.asId()) ?: return then()
        entity.run(then)
        1.6.seconds later {
            missile.placeBlock(instance, target, PlacementActor.ByTeam(team))
        }
    }
}