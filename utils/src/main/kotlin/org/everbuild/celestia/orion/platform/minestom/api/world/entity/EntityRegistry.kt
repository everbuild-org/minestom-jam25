package org.everbuild.celestia.orion.platform.minestom.api.world.entity

import org.everbuild.celestia.orion.platform.minestom.api.world.entity.impl.ArmorStandDissector
import org.everbuild.celestia.orion.platform.minestom.api.world.entity.impl.SheepDissector

object EntityRegistry {
    private val dissectors = mutableMapOf(
        "minecraft:sheep" to SheepDissector,
        "minecraft:armor_stand" to ArmorStandDissector
    )

    fun dissectorForType(id: String): EntityDissector? {
        return dissectors[id]
    }

    fun registerDissector(id: String, dissector: EntityDissector) {
        dissectors[id] = dissector
    }
}