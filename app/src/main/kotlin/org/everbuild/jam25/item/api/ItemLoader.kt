package org.everbuild.jam25.item.api

import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.everbuild.celestia.orion.platform.minestom.api.utils.logger
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.withCustomItemListeners
import org.reflections.Reflections

object ItemLoader {
    private val items = mutableListOf<AbstractItem>()
    private val overridingItems = mutableMapOf<Material, AbstractItem>()
    private val components = hashMapOf<KClass<*>, Key>()
    private var loaded = false
    val typeTag = Tag.String("averium:type")
    fun withCustomItemSupport() {
        if (loaded) return
        loaded = true
        withCustomItemListeners()

        val reflections = Reflections("org.everbuild")

        var itemCount = 0
        var itemComponentCount = 0

        reflections.getTypesAnnotatedWith(ItemDataComponent::class.java).forEach { clazz ->
            try {
                if (!clazz.kotlin.hasAnnotation<Serializable>()) {
                    logger.error("Class ${clazz.name} is annotated with @ItemComponent but is not annotated with @Serializable")
                    return@forEach
                }

                val annotation = clazz.kotlin.annotations.find { it is ItemDataComponent } as? ItemDataComponent ?: return@forEach
                components[clazz.kotlin] = Key.key(annotation.namespace, annotation.id)
                itemComponentCount++
            } catch (e: Exception) {
                logger.error("Failed to load custom item component ${clazz.name}", e)
            }
        }

        reflections.getSubTypesOf(AbstractItem::class.java).forEach {
            try {
                val instance = it.kotlin.objectInstance ?: return@forEach
                if (instance.overriddenMaterial != null) {
                    overridingItems[instance.overriddenMaterial!!] = instance
                }
                items.add(instance)
                itemCount++
            } catch (e: Exception) {
                logger.error("Failed to load custom item ${it.name}", e)
            }
        }

        logger.info("Loaded $itemCount custom items and $itemComponentCount custom item components")
    }

    fun all(): List<AbstractItem> {
        withCustomItemSupport()
        return items
    }

    fun byKey(key: Key): AbstractItem? {
        withCustomItemSupport()
        return items.find { it.id == key }
    }

    fun keyOfComponent(clazz: KClass<*>): Key? {
        withCustomItemSupport()
        return components[clazz]
    }

    fun getComponentClassByKey(key: Key): KClass<*>? {
        withCustomItemSupport()
        return components.filter { it.value == key }.keys.firstOrNull()
    }

    fun isCustomItem(itemStack: ItemStack): Boolean {
        withCustomItemSupport()
        return itemStack.getTag(typeTag) != null
    }

    fun getOverriddenItem(material: Material): AbstractItem? {
        withCustomItemSupport()
        return overridingItems[material]
    }

    fun byItem(item: ItemStack): AbstractItem? {
        withCustomItemSupport()
        if (!isCustomItem(item)) return null
        return byKey(Key.key(item.getTag(typeTag)!!))
    }
}