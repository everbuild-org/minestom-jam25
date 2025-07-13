package org.everbuild.nbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.nbt.CompoundBinaryTag

@OptIn(ExperimentalSerializationApi::class)
class TagDecoder(
    override val serializersModule: SerializersModule,
    private val tag: CompoundBinaryTag,
    private val root: Boolean = true
) : AbstractDecoder() {
    private var index = 0
    private var currentKey: String? = null

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (index >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        currentKey = descriptor.getElementName(index)
        index++
        return index - 1
    }

    override fun decodeInt(): Int = tag.getInt(currentKey!!)
    override fun decodeString(): String = tag.getString(currentKey!!)
    override fun decodeBoolean(): Boolean = tag.getBoolean(currentKey!!)
    override fun decodeByte(): Byte = tag.getByte(currentKey!!)
    override fun decodeShort(): Short = tag.getShort(currentKey!!)
    override fun decodeLong(): Long = tag.getLong(currentKey!!)
    override fun decodeFloat(): Float = tag.getFloat(currentKey!!)
    override fun decodeDouble(): Double = tag.getDouble(currentKey!!)
    override fun decodeChar(): Char = tag.getInt(currentKey!!).toChar()
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(tag.getString(currentKey!!))
            .also { if (it == CompositeDecoder.UNKNOWN_NAME) throw SerializationException(
                "Enum '${
                    tag.getString(
                        currentKey!!
                    )
                }' is not a valid enum member of '${enumDescriptor.serialName}'"
            )
            }
    }

    override fun decodeNotNullMark(): Boolean = tag.keySet().contains(currentKey)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (root) return TagDecoder(serializersModule, tag, false)
        return TagDecoder(serializersModule, tag.getCompound(currentKey!!), false)
    }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        val descriptor = deserializer.descriptor

        if (descriptor == ByteArraySerializer().descriptor) {
            @Suppress("UNCHECKED_CAST")
            return tag.getByteArray(currentKey!!) as T
        }

        return super.decodeSerializableValue(deserializer)
    }

}