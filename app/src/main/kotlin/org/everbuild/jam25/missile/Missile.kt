package org.everbuild.jam25.missile

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance

open class Missile(val entity: Entity) {
    fun setInstance(instance: Instance, pos: BlockVec) {
        entity.setInstance(instance, pos)
    }
}