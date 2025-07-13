package org.everbuild.nbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import net.kyori.adventure.nbt.CompoundBinaryTag

open class Nbt(serializerInit: SerializersModuleBuilder.() -> Unit = {}) : SerialFormat {
    override val serializersModule = SerializersModule {
        serializerInit(this)
    }

    fun <T> encodeToCompoundTag(
        serializer: SerializationStrategy<T>,
        value: T
    ): CompoundBinaryTag {
        val encoder = TagEncoder(serializersModule)
        serializer.serialize(encoder, value)
        return encoder.build()
    }

    fun <T> decodeFromCompoundTag(
        deserializer: DeserializationStrategy<T>,
        tag: CompoundBinaryTag
    ): T {
        val decoder = TagDecoder(serializersModule, tag)
        return deserializer.deserialize(decoder)
    }

    inline fun <reified T> decodeFromCompoundTag(tag: CompoundBinaryTag): T =
        decodeFromCompoundTag(serializersModule.serializer<T>(), tag)

    inline fun <reified T> encodeToCompoundTag(value: T): CompoundBinaryTag =
        encodeToCompoundTag(serializersModule.serializer<T>(), value)

    companion object : Nbt()
}