package org.everbuild.celestia.orion.platform.minestom.api.world

object LegacyBlockNameTransfer {
    private val fromLegacyMap = mapOf(
        "minecraft:grass" to "minecraft:short_grass",
    )

    fun transferLegacyBlockName(legacyBlockName: String): String {
        return fromLegacyMap[legacyBlockName] ?: legacyBlockName
    }
}