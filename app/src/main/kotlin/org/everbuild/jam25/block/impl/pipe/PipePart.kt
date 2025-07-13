package org.everbuild.jam25.block.impl.pipe

import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.worldseed.multipart.GenericModelImpl

class PipePart : GenericModelImpl() {
    override fun getId(): String = "pipe.bbmodel"

    override fun init(instance: Instance?, position: Pos) {
        super.init(instance, position)
        println(super.parts)
    }
}