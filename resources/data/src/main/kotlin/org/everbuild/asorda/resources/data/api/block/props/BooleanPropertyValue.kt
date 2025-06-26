package org.everbuild.asorda.resources.data.api.block.props

import net.kyori.adventure.nbt.CompoundBinaryTag

class BooleanPropertyValue(override val name: String) : PropertyValue<Boolean> {
    override fun serialize(value: Boolean) = value.toString()
    override fun deserialize(serial: String?) = serial.toBoolean()
    override fun serializeDefault(): String = "false"
    override fun getDefaultValue() = false
    override fun getAllValues() = setOf(false, true)
    override fun deserializeAndWriteInto(tag: CompoundBinaryTag, value: String): CompoundBinaryTag {
        return writeInto(tag, deserialize(value))
    }

    override fun readFrom(tag: CompoundBinaryTag): Boolean {
        return tag.getBoolean(name)
    }

    override fun readFromAndSerialize(tag: CompoundBinaryTag): String {
        return tag.getBoolean(name).toString()
    }

    override fun setNext(tag: CompoundBinaryTag): CompoundBinaryTag {
        val currentValue = readFrom(tag)
        return writeInto(tag, !currentValue)
    }

    override fun suggestions(): List<String> {
        return listOf("true", "false")
    }

    override fun writeInto(tag: CompoundBinaryTag, value: Boolean?): CompoundBinaryTag {
        return tag.putBoolean(name, value ?: getDefaultValue())
    }
}
