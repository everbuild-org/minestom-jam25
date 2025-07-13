package org.everbuild.nbt

import net.kyori.adventure.nbt.BinaryTag
import net.kyori.adventure.nbt.CompoundBinaryTag

class MutableCompoundTagBuilder {
    val tags = mutableMapOf<String, BinaryTag>()
    val children = mutableMapOf<String, MutableCompoundTagBuilder>()

    operator fun set(key: String, value: BinaryTag) {
        tags[key] = value
    }

    operator fun set(key: String, value: MutableCompoundTagBuilder) {
        children[key] = value
    }

    operator fun invoke(): CompoundBinaryTag {
        var tag = CompoundBinaryTag.builder()
            .put(tags)

        for ((key, value) in children) {
            tag = tag.put(key, value())
        }

        return tag.build()
    }
}