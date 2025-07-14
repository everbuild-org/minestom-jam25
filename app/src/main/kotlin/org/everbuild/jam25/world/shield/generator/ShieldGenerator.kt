package org.everbuild.jam25.world.shield.generator

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.everbuild.jam25.block.api.PlacementActor
import org.everbuild.jam25.block.impl.shieldgenerator.ShieldGeneratorBlock

data class ShieldGenerator(
    val position: BlockVec
) {
    var running: Boolean = ShieldGeneratorBlock.BlockState.DEFAULT.running
        set(value) {
            field = value
            ShieldGeneratorBlock.updateState(instance ?: return, position, value)
        }
    private var instance: Instance? = null

    fun setInstance(instance: Instance) {
        ShieldGeneratorBlock.placeBlock(instance, position, PlacementActor.ByServer)
        this.instance = instance
    }
}