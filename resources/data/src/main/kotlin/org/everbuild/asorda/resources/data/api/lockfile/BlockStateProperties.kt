package org.everbuild.asorda.resources.data.api.lockfile

import net.kyori.adventure.key.Key

data class PropertyDescriptor(val name: String, val values: Set<String>) {
    companion object {
        fun string(name: String, vararg values: String) = PropertyDescriptor(name, values.toSet())
        fun int(name: String, values: IntRange) = PropertyDescriptor(name, values.map { it.toString() }.toSet())
        fun int(name: String, vararg values: Int) = PropertyDescriptor(name, values.map { it.toString() }.toSet())
        fun bool(name: String) = PropertyDescriptor(name, setOf("true", "false"))
    }
}

data class SetProperty(val name: String, val value: String)

fun propertyPermutations(vararg properties: PropertyDescriptor): Set<Set<SetProperty>> {
    return properties.fold(setOf(setOf())) { acc, property ->
        acc.flatMap { combination ->
            property.values.map { value ->
                combination + SetProperty(property.name, value)
            }
        }.toSet()
    }
}

fun Set<Set<SetProperty>>.toLocked(key: Key): Set<LockedBlockState> {
    return this.map { properties -> LockedBlockState(key.asString(), properties.associate { it.name to it.value }) }.toSet()
}

fun <T> aggregate(vararg sets: Set<T>): Set<T> = sets.flatMap { it }.toSet()
