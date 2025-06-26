package org.everbuild.celestia.orion.platform.minestom.api.world.entity.impl

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.animal.SheepMeta
import net.minestom.server.instance.Chunk
import org.everbuild.celestia.orion.platform.minestom.api.world.entity.EntityDissector

object SheepDissector : EntityDissector {
    override fun dissect(chunk: Chunk, entity: CompoundBinaryTag) {
        val pos = requireCoordinates(entity)
        val instance = chunk.instance

        val sheep = Entity(EntityType.SHEEP)
        val sheepMeta = sheep.entityMeta as SheepMeta
        sheepMeta.isSheared = entity.getByte("Sheared") == 1.toByte()
        sheepMeta.isBaby = entity.getInt("Age") < 0

        sheep.setInstance(instance, pos)
    }
}