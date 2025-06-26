package org.everbuild.asorda.resources.data.blocks

import org.everbuild.asorda.resources.data.api.block.EntityResourcePackBlock
import org.everbuild.asorda.resources.data.api.maybeAsordaKey

object ExampleMachineResource : EntityResourcePackBlock("example_machine".maybeAsordaKey()) {
    val lit = booleanProperty("running")

    init {
        models {
            on(lit eq true, model = "minecraft:block/furnace_on")
            on(lit eq false, default = true, model = "minecraft:block/furnace")
        }
    }
}