package org.everbuild.jam25.world.shield

import net.minestom.server.instance.Instance
import org.joml.Vector3f

class ShieldRenderer(val instance: Instance, vertices: List<List<Vector3f>>) : AutoCloseable {
    val children = mutableListOf<AutoCloseable>()

    init {
        for (fs in vertices) {
            children.add(QuadRenderer(instance, fs))
            children.add(QuadRenderer(instance, fs.reversed()))
        }
    }

    override fun close() {
        children.forEach { it.close() }
    }
}