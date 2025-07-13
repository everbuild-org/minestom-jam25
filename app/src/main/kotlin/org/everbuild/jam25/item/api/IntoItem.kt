package org.everbuild.jam25.item.api

import net.minestom.server.item.ItemStack

interface IntoItem {
    fun createItem(): ItemStack?

    companion object {
        fun of(item: ItemStack): IntoItem = object : IntoItem {
            override fun createItem(): ItemStack = item
        }
    }
}