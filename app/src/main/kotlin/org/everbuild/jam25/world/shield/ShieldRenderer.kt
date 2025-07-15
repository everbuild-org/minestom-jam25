package org.everbuild.jam25.world.shield

import net.minestom.server.instance.Instance
import org.joml.Vector3f

class ShieldRenderer(val instance: Instance, val vertices: List<List<Vector3f>>) : AutoCloseable {
    val children = mutableListOf<AutoCloseable>()
    init { up() }

    override fun close() {
        down()
    }

    fun up() {
        for (fs in vertices) {
            children.add(QuadRenderer(instance, fs))
            children.add(QuadRenderer(instance, fs.reversed()))
        }
    }

    fun down() {
        children.forEach { it.close() }
        children.clear()
    }

    fun set(active: Boolean) = if (active) up() else down()
}