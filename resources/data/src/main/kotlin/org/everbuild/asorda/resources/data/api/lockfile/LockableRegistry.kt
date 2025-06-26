package org.everbuild.asorda.resources.data.api.lockfile

import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key

@Serializable
abstract class LockableRegistry<K, V> {
    abstract val entries: MutableMap<K, V>

    abstract val allPossibleEntries: Set<V>
    abstract val forcedBaselineEntries: Map<K, V>

    /**
     * Gets a mapping if it exists
     */
    fun getMapping(key: K): V? = entries[key]

    /**
     * Sets a mapping if it doesn't exist yet or is not locked
     * @return true if the mapping was set, false if it was locked
     */
    fun setMapping(key: K, value: V): Boolean {
        // Don't override forced baseline entries
        if (forcedBaselineEntries.containsKey(key)) {
            return false
        }

        // Only set if not already mapped (lock behavior)
        if (!entries.containsKey(key)) {
            entries[key] = value
            return true
        }

        return false
    }

    /**
     * Retrieves an existing mapping or creates and locks a new one
     * @param key The key to look up
     * @param defaultValueProvider Function that provides a default value if none exists
     * @return The existing or newly created mapping
     */
    fun retrieveOrLock(key: K, defaultValueProvider: () -> V): V {
        return entries[key] ?: run {
            val value = defaultValueProvider()
            setMapping(key, value)
            value
        }
    }

    /**
     * Returns the next available entry that is not currently in use
     * @return An unused entry or null if all entries are used
     */
    fun nextFree(): V? {
        val usedValues = entries.values.toSet()
        return allPossibleEntries.find { !usedValues.contains(it) }
    }

    /**
     * Gets all current entries
     */
    fun getAllEntries(): Map<K, V> = entries.toMap()
}
