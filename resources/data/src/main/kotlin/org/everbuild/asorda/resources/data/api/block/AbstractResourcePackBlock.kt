package org.everbuild.asorda.resources.data.api.block

import io.michaelrocks.bimap.HashBiMap
import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.BuildableResource
import org.everbuild.asorda.resources.data.api.block.props.BooleanPropertyValue
import org.everbuild.asorda.resources.data.api.block.props.EnumPropertyValue
import org.everbuild.asorda.resources.data.api.block.props.IntPropertyValue
import org.everbuild.asorda.resources.data.api.block.props.PropertyValue
import org.everbuild.asorda.resources.data.api.lockfile.LockedBlockState
import org.everbuild.asorda.resources.data.api.lockfile.LockfileService
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.blockstate.BlockState
import team.unnamed.creative.blockstate.MultiVariant
import team.unnamed.creative.blockstate.Variant

interface AbstractResourcePackBlock {
    val key: Key
    val properties: HashMap<String, PropertyValue<*>>

    fun booleanProperty(name: String): PropertyValue<Boolean> {
        return BooleanPropertyValue(name).also { properties[name] = it }
    }

    fun intProperty(name: String, range: IntRange): PropertyValue<Int> {
        return IntPropertyValue(name, range).also { properties[name] = it }
    }
}

inline fun <reified E : Enum<E>> AbstractResourcePackBlock.enumProperty(name: String, default: E): PropertyValue<E> {
    val map = HashBiMap<E, String>()
    map.putAll(enumValues<E>().associateWith { it.name.lowercase() })
    return EnumPropertyValue(name, default, map, enumValues<E>()).also { properties[name] = it }
}

fun generatePermutations(properties: Collection<PropertyValue<*>>): Set<HashMap<String, String>> {
    val result = mutableSetOf<HashMap<String, String>>()
    if (properties.isEmpty()) return result.apply { add(hashMapOf()) }

    fun generateCombination(
        currentCombination: HashMap<String, String>,
        remainingProperties: List<PropertyValue<*>>
    ) {
        if (remainingProperties.isEmpty()) {
            result.add(HashMap(currentCombination))
            return
        }
        val currentProperty = remainingProperties.first()
        currentProperty.getAllValues().forEach { value ->
            currentCombination[currentProperty.name] = value.toString()
            generateCombination(currentCombination, remainingProperties.drop(1))
            currentCombination.remove(currentProperty.name)
        }
    }

    generateCombination(hashMapOf(), properties.toList())
    return result
}