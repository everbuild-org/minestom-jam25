package org.everbuild.asorda.resources.data.api.lockfile

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.kyori.adventure.key.Key

abstract class BlockStateRegistry : LockableRegistry<String, LockedBlockState>() {
    /**
     * Updates existing blockstates with new default property values
     * @param blockKeyPrefix The prefix to match (like "test")
     * @param newDefaultProperties Map of new properties and their default values
     * @return Number of entries updated
     */
    fun upgradeBlockProperties(blockKeyPrefix: String, newDefaultProperties: Map<String, String>): Int {
        var updatedCount = 0

        // Find all matching entries that need updating
        entries.forEach { (key, blockState) ->
            // Check if this is the block we want to update
            if (key.startsWith("$blockKeyPrefix[")) {
                // Get current properties
                val currentProps = HashMap(blockState.properties)

                // Add any missing default properties
                var updated = false
                newDefaultProperties.forEach { (propName, defaultValue) ->
                    if (!currentProps.containsKey(propName)) {
                        currentProps[propName] = defaultValue
                        updated = true
                    }
                }

                // Update the entry if needed
                if (updated) {
                    entries[key] = LockedBlockState(blockState.key, currentProps)
                    updatedCount++
                }
            }
        }

        return updatedCount
    }

    /**
     * Helper method to get a specific block state or create a mapping for it
     */
    fun retrieveOrLock(customBlockState: String): LockedBlockState {
        return retrieveOrLock(customBlockState) { this@BlockStateRegistry.nextFree()!! }
    }
}

@Serializable
class BasicBlockStateRegistry : BlockStateRegistry() {
    override val entries: MutableMap<String, LockedBlockState> = mutableMapOf()

    @Transient
    override val allPossibleEntries: Set<LockedBlockState> = aggregate(
        propertyPermutations(
            PropertyDescriptor.string("instrument", *instruments),
            PropertyDescriptor.int("note", 0..24),
            PropertyDescriptor.bool("powered")
        ).toLocked(Key.key("minecraft:note_block")),
        propertyPermutations(
            PropertyDescriptor.bool("up"),
            PropertyDescriptor.bool("down"),
            PropertyDescriptor.bool("north"),
            PropertyDescriptor.bool("east"),
            PropertyDescriptor.bool("south"),
            PropertyDescriptor.bool("west"),
        ).toLocked(Key.key("minecraft:red_mushroom_block")),
        propertyPermutations(
            PropertyDescriptor.bool("up"),
            PropertyDescriptor.bool("down"),
            PropertyDescriptor.bool("north"),
            PropertyDescriptor.bool("east"),
            PropertyDescriptor.bool("south"),
            PropertyDescriptor.bool("west"),
        ).toLocked(Key.key("minecraft:brown_mushroom_block")),
        propertyPermutations(
            PropertyDescriptor.bool("up"),
            PropertyDescriptor.bool("down"),
            PropertyDescriptor.bool("north"),
            PropertyDescriptor.bool("east"),
            PropertyDescriptor.bool("south"),
            PropertyDescriptor.bool("west"),
        ).toLocked(Key.key("minecraft:mushroom_stem")),
    )

    @Transient
    override val forcedBaselineEntries: Map<String, LockedBlockState> = mutableMapOf()
}

@Serializable
class GroundBlockStateRegistry : BlockStateRegistry() {
    override val entries: MutableMap<String, LockedBlockState> = mutableMapOf()

    @Transient
    override val allPossibleEntries: Set<LockedBlockState> = aggregate(
        propertyPermutations(
            PropertyDescriptor.bool("attached"),
            PropertyDescriptor.bool("north"),
            PropertyDescriptor.bool("east"),
            PropertyDescriptor.bool("south"),
            PropertyDescriptor.bool("west"),
            PropertyDescriptor.bool("powered"),
            PropertyDescriptor.bool("disarmed"),
        ).toLocked(Key.key("minecraft:tripwire")),
    )

    @Transient
    override val forcedBaselineEntries: Map<String, LockedBlockState> = mutableMapOf()
}

@Serializable
data class LockedBlockState(
    val key: String,
    val properties: Map<String, String>
)
