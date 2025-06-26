package org.everbuild.celestia.orion.platform.minestom.api.utils

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.ItemEntity
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import java.time.Duration
import java.time.temporal.ChronoUnit

fun Instance.dropItem(item: ItemStack, location: Point) {
    val itemEntity = ItemEntity(item)
    itemEntity.setPickupDelay(Duration.of(500, ChronoUnit.MILLIS))
    itemEntity.setInstance(this, location)
}