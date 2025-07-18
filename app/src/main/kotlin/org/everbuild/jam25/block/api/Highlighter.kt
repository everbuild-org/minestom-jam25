package org.everbuild.jam25.block.api

import kotlin.time.Duration
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.BlockDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.jam25.state.ingame.GameTeam

class Highlighter(
    spawnInstance: Instance,
    vec: BlockVec,
    color: Int,
    block: Block,
    team: GameTeam
) : Entity(EntityType.BLOCK_DISPLAY) {
    init {
        setNoGravity(true)
        editEntityMeta(BlockDisplayMeta::class.java) { meta ->
            meta.setBlockState(block)
            meta.glowColorOverride = color
            meta.isHasGlowingEffect = true
            meta.scale = Vec(0.99, 0.99,0.99)
            meta.translation = Vec(0.005, 0.005, 0.005)
        }
        setInstance(spawnInstance, vec.asVec())

        isAutoViewable = false
        for (player in team.players) {
            addViewer(player)
        }
    }

    fun removeAfter(duration: Duration) = duration later {
        remove()
    }
}