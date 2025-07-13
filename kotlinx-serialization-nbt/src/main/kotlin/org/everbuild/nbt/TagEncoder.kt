package org.everbuild.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.nbt.ByteArrayBinaryTag
import net.kyori.adventure.nbt.ByteBinaryTag
import net.kyori.adventure.nbt.DoubleBinaryTag
import net.kyori.adventure.nbt.FloatBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.LongBinaryTag
import net.kyori.adventure.nbt.ShortBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag

@OptIn(ExperimentalSerializationApi::class)
class TagEncoder(
    override val serializersModule: SerializersModule,
    private val tag: MutableCompoundTagBuilder = MutableCompoundTagBuilder(),
    private val isRoot: Boolean = true
) : AbstractEncoder() {
    private var currentKey: String? = null

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        currentKey = descriptor.getElementName(index)
        return true
    }

    override fun encodeInt(value: Int) {
        tag[currentKey!!] = IntBinaryTag.intBinaryTag(value)
    }

    override fun encodeString(value: String) {
        tag[currentKey!!] = StringBinaryTag.stringBinaryTag(value)
    }

    override fun encodeBoolean(value: Boolean) {
        tag[currentKey!!] = if (value) { ByteBinaryTag.ONE } else { ByteBinaryTag.ZERO }
    }

    override fun encodeByte(value: Byte) {
        tag[currentKey!!] = ByteBinaryTag.byteBinaryTag(value)
    }

    override fun encodeShort(value: Short) {
        tag[currentKey!!] = ShortBinaryTag.shortBinaryTag(value)
    }

    override fun encodeLong(value: Long) {
        tag[currentKey!!] = LongBinaryTag.longBinaryTag(value)
    }

    override fun encodeFloat(value: Float) {
        tag[currentKey!!] = FloatBinaryTag.floatBinaryTag(value)
    }

    override fun encodeDouble(value: Double) {
        tag[currentKey!!] = DoubleBinaryTag.doubleBinaryTag(value)
    }

    override fun encodeChar(value: Char) {
        tag[currentKey!!] = IntBinaryTag.intBinaryTag(value.code)
    }

    override fun encodeNull() {
        // Nothing encoded
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeString(enumDescriptor.getElementName(index))
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (isRoot) return TagEncoder(serializersModule, tag, false)
        val mutableCompoundTag = MutableCompoundTagBuilder()
        tag[currentKey!!] = mutableCompoundTag
        return TagEncoder(serializersModule, mutableCompoundTag, false)
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        val descriptor = serializer.descriptor

        // **Crucially, check if the serializer is for a ByteArray**
        // We compare the descriptor directly as recommended for built-in serializers
        if (descriptor == ByteArraySerializer().descriptor) {
            // Cast the value to ByteArray and use the specific putByteArray method
            val byteArray = value as ByteArray
            tag[currentKey!!] = ByteArrayBinaryTag.byteArrayBinaryTag(*byteArray)
        } else {
            // For other serializable types, let the default encoder logic handle it
            // This will typically call beginStructure, encodeElement, etc.
            super.encodeSerializableValue(serializer, value)
        }
    }

    fun build() = tag()
}