package org.everbuild.celestia.orion.platform.minestom.api.world.entity

import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.DoubleBinaryTag
import net.kyori.adventure.nbt.FloatBinaryTag
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Chunk

interface EntityDissector {
    fun dissect(chunk: Chunk, entity: CompoundBinaryTag)

    fun requireCoordinates(chunkDataEntity: CompoundBinaryTag): Pos {
        val position = chunkDataEntity.getList("Pos", BinaryTagTypes.DOUBLE)
        val rot = requireRotation(chunkDataEntity)

        return Pos(
            (position[0] as DoubleBinaryTag).value(),
            (position[1] as DoubleBinaryTag).value(),
            (position[2] as DoubleBinaryTag).value(),
            rot.first,
            rot.second
        )
    }

    fun requireRotation(chunkDataEntity: CompoundBinaryTag): Pair<Float, Float> {
        val rotation = chunkDataEntity.getList("Rotation", BinaryTagTypes.FLOAT)
        return (rotation[0] as FloatBinaryTag).value() to (rotation[1] as FloatBinaryTag).value()
    }
}