package org.everbuild.jam25.world.placeable

interface ItemHolder {
    fun hasItem(item: ItemConsumer.ItemOrOil): Boolean
    fun removeItem(item: ItemConsumer.ItemOrOil): ItemConsumer.ItemOrOil?
}