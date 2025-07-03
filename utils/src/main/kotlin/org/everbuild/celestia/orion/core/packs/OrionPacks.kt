package org.everbuild.celestia.orion.core.packs

import net.kyori.adventure.resource.ResourcePackInfo
import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import java.net.URI
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import org.everbuild.asorda.resources.data.api.meta.FontSizeEntry
import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.celestia.orion.core.menu.adaptive.token.SpaceLayerToken
import org.everbuild.celestia.orion.core.util.component

object OrionPacks {
    var resourcePack: ResourcePackInfo = ResourcePackInfo.resourcePackInfo()
        .id(UUID.nameUUIDFromBytes("orion-evdev-${SharedPropertyConfig.resourcePack}".toByteArray()))
        .uri(URI(SharedPropertyConfig.resourcePack))
        .computeHashAndBuild()
        .join()

    private val spaceCache = hashMapOf<Int, Component>()

    val data =
        Json.decodeFromString<ResourcePackMetadata>(OrionPacks::class.java.getResource("/resources.json")!!.readText())

    fun getCharacterCodepoint(name: String): String {
        return data.font.entries[name]?.codepoint ?: name
    }

    fun getCharacterSize(name: String): FontSizeEntry {
        return data.font.entries[name]?.size ?: FontSizeEntry(8, 8)
    }

    fun getCharacterWidth(char: Char): Int {
        val name = data.font.entries.filter { it.value.codepoint == char.toString() }.firstNotNullOfOrNull { it.key }

        if (name == null) {
            return GlyphSizes.findOrNull(char) ?: 8
        }

        return data.font.entries[name]?.size?.width ?: 8
    }

    fun refreshResourcePack() {
        resourcePack = ResourcePackInfo.resourcePackInfo()
            .id(UUID.nameUUIDFromBytes("orion-evdev-${SharedPropertyConfig.resourcePack}".toByteArray()))
            .uri(URI(SharedPropertyConfig.resourcePack))
            .computeHashAndBuild()
            .join()
    }

    fun getSpaceComponent(width: Int): Component {
        return spaceCache.getOrPut(width) { SpaceLayerToken.segmentWidth(width.absoluteValue, width.sign).component() }
    }

    fun getComponentWidth(label: Component): Int = 0
}

fun String.withEmojis(): String {
    var mutSelf = this
    for ((key, value) in OrionPacks.data.font.entries) {
        mutSelf = mutSelf.replace(":$key:", value.codepoint)
    }
    return mutSelf
}