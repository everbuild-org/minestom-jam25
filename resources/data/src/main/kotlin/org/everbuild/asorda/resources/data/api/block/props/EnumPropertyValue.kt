package org.everbuild.asorda.resources.data.api.block.props

import io.michaelrocks.bimap.BiMap
import net.kyori.adventure.nbt.CompoundBinaryTag

class EnumPropertyValue<E : Enum<E>>(
    override val name: String,
    private val default: E,
    private val mapping: BiMap<E, String>,
    private val elements: Array<E>
) : PropertyValue<E> {
    override fun deserialize(serial: String?): E {
        return mapping.inverse[serial] ?: default
    }

    override fun serializeDefault(): String {
        return mapping[default]!!
    }

    override fun getDefaultValue(): E = default

    override fun getAllValues(): Collection<E> {
        return mapping.keys
    }

    override fun deserializeAndWriteInto(tag: CompoundBinaryTag, value: String): CompoundBinaryTag {
        return writeInto(tag, deserialize(value))
    }

    override fun readFrom(tag: CompoundBinaryTag): E {
        return deserialize(tag.getString(name))
    }

    override fun readFromAndSerialize(tag: CompoundBinaryTag): String {
        return tag.getString(name)
    }

    override fun setNext(tag: CompoundBinaryTag): CompoundBinaryTag {
        val currentValue = readFrom(tag)
        val nextOrdinal = (currentValue.ordinal + 1) % mapping.size
        return writeInto(tag, elements[nextOrdinal])
    }

    override fun suggestions(): List<String> {
        return mapping.keys.map { it.name }
    }

    override fun writeInto(tag: CompoundBinaryTag, value: E?): CompoundBinaryTag {
        return tag.putString(name, serialize(value ?: getDefaultValue())!!)
    }

    override fun serialize(value: E): String? {
        return mapping[value]
    }
}