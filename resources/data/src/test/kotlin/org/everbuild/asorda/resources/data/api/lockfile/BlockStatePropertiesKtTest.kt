package org.everbuild.asorda.resources.data.api.lockfile

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BlockStatePropertiesKtTest {
    @Test
    fun testPropertyPermutations() {
        val a = PropertyDescriptor.bool("a")
        val b = PropertyDescriptor.bool("b")

        val set = propertyPermutations(a, b)
        assertEquals(4, set.size)
        assertTrue(set.contains(setOf(SetProperty("a", "true"), SetProperty("b", "true"))))
        assertTrue(set.contains(setOf(SetProperty("a", "false"), SetProperty("b", "true"))))
        assertTrue(set.contains(setOf(SetProperty("a", "true"), SetProperty("b", "false"))))
        assertTrue(set.contains(setOf(SetProperty("a", "false"), SetProperty("b", "false"))))
    }

    @Test
    fun testLargerPropertyPermutations() {
        val a = PropertyDescriptor.int("a", 0..15)
        val b = PropertyDescriptor.int("b", 0..15)
        val c = PropertyDescriptor.int("c", 0..15)
        val set = propertyPermutations(a, b, c)
        assertEquals(4096, set.size)
    }
}