package org.everbuild.jam25.item.api

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.component.DataComponents
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.everbuild.asorda.resources.data.api.item.AbstractItemResource
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.util.MinestomNBT

fun itemStackOf(material: Material, amount: Int = 1): ItemStack = ItemStack.of(material, amount)
fun itemStackOf(item: AbstractItem, amount: Int = 1): ItemStack = item.createNewStack(amount)
fun itemStackOf(material: AbstractItemResource, amount: Int = 1): ItemStack =
    itemStackOf(
        Material.fromKey(material.getParentMaterial())!!,
        amount
    ).withItemModel(material.getCustomModel())

fun ItemStack.withMaterial(material: AbstractItemResource): ItemStack =
    this.withMaterial(Material.fromKey(material.getParentMaterial())!!)
        .withItemModel(material.getCustomModel())

fun ItemStack.Builder.material(material: AbstractItemResource): ItemStack.Builder =
    this.material(Material.fromKey(material.getParentMaterial())!!)
        .itemModel(material.getCustomModel())

inline fun <reified T> ItemStack.with(component: T): ItemStack {
    val key = ItemLoader.keyOfComponent(T::class)
        ?: throw IllegalArgumentException("Unknown component type: ${T::class}")

    val tag = Tag.NBT(key.asString())
    val nbt = MinestomNBT.encodeToCompoundTag(component)

    return this.withTag(tag, nbt)
}

@OptIn(InternalSerializationApi::class)
fun ItemStack.withAnonymous(component: Any): ItemStack {
    val key = ItemLoader.keyOfComponent(component::class)
        ?: throw IllegalArgumentException("Unknown component type: ${component::class}")

    @Suppress("UNCHECKED_CAST") // always succeeds
    val serializer = component::class.serializer() as KSerializer<Any>
    val tag = Tag.NBT(key.asString())
    val nbt = MinestomNBT.encodeToCompoundTag(serializer, component)

    return this
        .withTag(tag, nbt)
}

inline fun <reified T> ItemStack.without(): ItemStack {
    val key = ItemLoader.keyOfComponent(T::class)
        ?: throw IllegalArgumentException("Unknown component type: ${T::class}")

    val tag = Tag.NBT(key.asString())

    return this.withTag(tag, null)
}

inline fun <reified T> ItemStack.get(): T? {
    val key = ItemLoader.keyOfComponent(T::class)
        ?: throw IllegalArgumentException("Unknown component type: ${T::class}")

    val tag = Tag.NBT(key.asString())
    val nbt = this.getTag(tag) ?: return null
    val compound = nbt as? CompoundBinaryTag ?: return null
    return MinestomNBT.decodeFromCompoundTag<T>(compound)
}

inline fun <reified T> ItemStack.has(): Boolean {
    val key = ItemLoader.keyOfComponent(T::class)
        ?: throw IllegalArgumentException("Unknown component type: ${T::class}")

    return this.hasTag(Tag.NBT(key.asString()))
}

private fun undecorate(component: Component): Component {
    return Component.text().style(Style.style().decoration(TextDecoration.ITALIC, false).build()).append(component)
        .build()
}

inline fun <reified T> ItemStack.edit(action: (T) -> T): ItemStack =
    this.with(action(this.get<T>() ?: throw IllegalStateException("Item has no component of type ${T::class}")))

inline fun <reified T> ItemStack.editIfPresent(action: (T) -> T): ItemStack =
    if (this.has<T>()) this.edit(action) else this

fun ItemStack.isItemOf(item: AbstractItem): Boolean = this.getTag(ItemLoader.typeTag) == item.id.asString()

fun ItemStack.name(minimessage: String): ItemStack {
    return withCustomName(undecorate(minimessage.minimessage()))
}

fun ItemStack.lore(minimessage: String): ItemStack {
    return get(DataComponents.LORE)?.let { components ->
        withLore(components.toMutableList().also { it.add(undecorate(minimessage.minimessage())) })
    } ?: withLore(undecorate(minimessage.minimessage()))
}
