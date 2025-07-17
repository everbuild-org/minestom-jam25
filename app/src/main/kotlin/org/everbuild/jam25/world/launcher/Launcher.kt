package org.everbuild.jam25.world.launcher

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement

class Launcher(val pos: BlockVec) : AdvanceableWorldElement {
    override fun advance(instance: Instance) {

    }

    fun drop(instance: Instance) {

    }

    override fun getBlockPosition(): BlockVec = pos
}