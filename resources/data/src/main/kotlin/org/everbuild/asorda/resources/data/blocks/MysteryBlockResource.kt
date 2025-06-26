package org.everbuild.asorda.resources.data.blocks

import org.everbuild.asorda.resources.data.api.block.NativeResourcePackBlock
import org.everbuild.asorda.resources.data.api.maybeAsordaKey

object MysteryBlockResource : NativeResourcePackBlock("mystery_block".maybeAsordaKey()) {
    val lit = booleanProperty("lit")

    init {
        models {
            on(lit eq true) {
                model("minecraft:block/gold_block")
            }

            on(lit eq false, default = true) {
                model("minecraft:block/gold_ore")
            }
        }
    }
}