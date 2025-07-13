package org.everbuild.jam25.block.impl.pipe

import net.minestom.server.instance.block.BlockFace
import net.worldseed.multipart.GenericModelImpl

class PipePart(val type: String, val face: BlockFace?) : GenericModelImpl() {
    override fun getId(): String {
        if (face == null) return "pipe_$type.geo.bbmodel".also { println(it) }
        return "pipe_${type}_${face.toDirection().name.lowercase()}.geo.bbmodel".also { println(it) }
    }
}