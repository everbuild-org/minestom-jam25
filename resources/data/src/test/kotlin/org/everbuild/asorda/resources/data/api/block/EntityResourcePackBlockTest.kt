package org.everbuild.asorda.resources.data.api.block

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EntityResourcePackBlockTest {
    @Test
    fun testNearestMatchingState() {
        val fullDefinition = mapOf("a" to "1", "b" to "2", "c" to "3")

        // Test empty options
        assertNull(EntityResourcePackBlock.getNearestMatchingState(fullDefinition, emptySet()))

        // Test exact match
        val optionsWithExact = setOf(
            mapOf("a" to "1", "b" to "2", "c" to "3")
        )
        assertEquals(fullDefinition, EntityResourcePackBlock.getNearestMatchingState(fullDefinition, optionsWithExact))

        // Test partial match
        val optionsPartial = setOf(
            mapOf("a" to "1", "b" to "2"),
            mapOf("a" to "1", "b" to "3"),
            mapOf("a" to "1", "c" to "2")
        )
        assertEquals(
            mapOf("a" to "1", "b" to "2"),
            EntityResourcePackBlock.getNearestMatchingState(fullDefinition, optionsPartial)
        )

        // Test no matching properties
        val optionsNoMatch = setOf(
            mapOf("a" to "4", "b" to "5"),
            mapOf("b" to "5", "c" to "6")
        )
        assertEquals(
            mapOf("a" to "4", "b" to "5"),
            EntityResourcePackBlock.getNearestMatchingState(fullDefinition, optionsNoMatch)
        )
    }
}