package org.everbuild.asorda.resources.data.api.block.props

import net.kyori.adventure.nbt.CompoundBinaryTag

interface PropertyValue<T> {
    val name: String
    fun deserialize(serial: String?): T
    fun serialize(value: T): String?
    fun serializeDefault(): String
    fun getDefaultValue(): T
    fun getAllValues(): Collection<T>

    fun writeInto(tag: CompoundBinaryTag, value: T?): CompoundBinaryTag
    fun deserializeAndWriteInto(tag: CompoundBinaryTag, value: String): CompoundBinaryTag
    fun readFrom(tag: CompoundBinaryTag): T
    fun readFromAndSerialize(tag: CompoundBinaryTag): String
    fun setNext(tag: CompoundBinaryTag): CompoundBinaryTag
    fun suggestions(): List<String>
}