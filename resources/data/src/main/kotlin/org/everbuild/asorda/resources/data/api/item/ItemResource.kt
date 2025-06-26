package org.everbuild.asorda.resources.data.api.item

import net.kyori.adventure.key.Key

interface AbstractItemResource {
    fun getCustomModel(): String
    fun getParentMaterial(): String
}

data class ItemResource(
    val parent: String,
    val model: Key
) : AbstractItemResource {
    override fun getCustomModel(): String = model.asString()
    override fun getParentMaterial(): String = parent
}