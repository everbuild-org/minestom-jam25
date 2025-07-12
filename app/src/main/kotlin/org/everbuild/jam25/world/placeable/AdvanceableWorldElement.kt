package org.everbuild.jam25.world.placeable

import net.minestom.server.instance.Instance

interface AdvanceableWorldElement {
    fun advance(instance: Instance)
}