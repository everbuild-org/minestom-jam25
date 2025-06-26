package org.everbuild.asorda.resources.data.api.block.props

import net.kyori.adventure.nbt.CompoundBinaryTag

class IntPropertyValue(override val name: String, private val range: IntRange) : PropertyValue<Int> {
    override fun serialize(value: Int) = value.toString()
    override fun deserialize(serial: String?) = serial?.toIntOrNull() ?: getDefaultValue()
    override fun serializeDefault(): String = getDefaultValue().toString()
    override fun getDefaultValue() = range.first
    override fun getAllValues() = range.toSet()
    override fun deserializeAndWriteInto(tag: CompoundBinaryTag, value: String): CompoundBinaryTag {
        return writeInto(tag, deserialize(value))
    }

    override fun readFrom(tag: CompoundBinaryTag): Int {
        return tag.getInt(name)
    }

    override fun readFromAndSerialize(tag: CompoundBinaryTag): String {
        return tag.getInt(name).toString()
    }

    override fun setNext(tag: CompoundBinaryTag): CompoundBinaryTag {
        val currentValue = readFrom(tag)
        var nextValue = currentValue + 1
        if (nextValue > range.last) {
            nextValue = range.first
        }
        return writeInto(tag, nextValue)
    }

    override fun suggestions(): List<String> {
        if (range.last - range.first < 10) {
            return range.map { it.toString() }
        }
        return listOf(range.first.toString(), range.last.toString())
    }

    override fun writeInto(tag: CompoundBinaryTag, value: Int?): CompoundBinaryTag {
        return tag.putInt(name, value ?: getDefaultValue())
    }
}
