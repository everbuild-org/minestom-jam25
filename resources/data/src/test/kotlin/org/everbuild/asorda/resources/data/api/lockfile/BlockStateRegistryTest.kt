package org.everbuild.asorda.resources.data.api.lockfile

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class BlockStateRegistryTest {
    @Test
    fun testDoesNotReallocateBase() {
        val registry = BasicBlockStateRegistry()
        assertNotEquals(
            registry.retrieveOrLock("asorda:test1"),
            registry.retrieveOrLock("asorda:test2")
        )
    }

    @Test
    fun testDoesNotReallocateProps() {
        val registry = BasicBlockStateRegistry()
        assertNotEquals(
            registry.retrieveOrLock("asorda:test1[x=1]"),
            registry.retrieveOrLock("asorda:test1[x=2]")
        )
    }

    @Test
    fun testDoesReallocateSameBase() {
        val registry = BasicBlockStateRegistry()
        assertEquals(
            registry.retrieveOrLock("asorda:test1"),
            registry.retrieveOrLock("asorda:test1")
        )
    }

    @Test
    fun testDoesReallocateSameProps() {
        val registry = BasicBlockStateRegistry()
        assertEquals(
            registry.retrieveOrLock("asorda:test1[x=1]"),
            registry.retrieveOrLock("asorda:test1[x=1]")
        )
    }
}