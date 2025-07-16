package org.everbuild.jam25.block.impl.pipe.path

import kotlin.time.Duration.Companion.seconds
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.ItemDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.jam25.state.ingame.InGameState
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer

class ResourcePath(
    val resource: ItemConsumer.ItemOrOil,
    val path: MutableList<BlockVec>,
    val instance: Instance,
    val game: InGameState,
    val target: BlockVec,
    val thenRun: (ResourcePath) -> Unit = {}
) : AdvanceableWorldElement {
    val blocksPerSecond = 3
    val cooldown = Cooldown(1.seconds / blocksPerSecond)
    val last3Blocks = mutableListOf<BlockVec>()
    var currentBlock = path.removeFirst()
    val entity = Entity(EntityType.ITEM_DISPLAY).also {
        it.setNoGravity(true)
        it.editEntityMeta(ItemDisplayMeta::class.java) { meta ->
            meta.itemStack = resource.display()
            meta.scale = Vec(0.25, 0.25, 0.25)
            meta.displayContext = ItemDisplayMeta.DisplayContext.FIXED
            meta.transformationInterpolationDuration = (20 / blocksPerSecond) + 2
        }
    }

    init {
        game.advanceable.add(this)
        entity.setInstance(instance, currentBlock)
        game.networkController.requests.add(this)
    }

    fun travelToNext() {
        if (path.isEmpty()) {
            game.advanceable.remove(this)
            game.networkController.requests.remove(this)
            entity.remove()
            return thenRun(this)
        }

        last3Blocks.add(currentBlock)
        if (last3Blocks.size > 3) last3Blocks.removeAt(0)

        val next = path.removeFirst()
        currentBlock = next
        entity.editEntityMeta(ItemDisplayMeta::class.java) { meta ->
            meta.transformationInterpolationStartDelta = -1
            meta.translation = Vec(next.x() + 0.5, next.y() + 0.5, next.z() + 0.5).sub(entity.position)
        }
    }

    fun isAround(block: BlockVec) = last3Blocks.contains(block)
            || block == currentBlock
            || path.subList(0, path.size.coerceAtMost(last3Blocks.size)).contains(block)

    override fun advance(instance: Instance) {
        if (!cooldown.get()) return
        travelToNext()
    }

    override fun getBlockPosition(): BlockVec {
        return currentBlock
    }
}