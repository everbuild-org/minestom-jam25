package org.everbuild.jam25.listener

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.ItemEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.item.PickupItemEvent
import net.minestom.server.instance.Instance
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.utils.time.TimeUnit
import org.everbuild.celestia.orion.platform.minestom.api.utils.eyePosition
import org.everbuild.celestia.orion.platform.minestom.util.listen
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import net.minestom.server.coordinate.Vec
import org.everbuild.celestia.orion.core.util.sendMiniMessageActionBar
import org.everbuild.jam25.block.api.WrenchComponent
import org.everbuild.jam25.item.api.has

fun setupPlayerDropEvent() {
    listen<ItemDropEvent> {
        if (it.itemStack.has<WrenchComponent>()) {
            it.player.sendMiniMessageActionBar("<red>The Construction tool cannot be dropped!")
            it.isCancelled = true
            return@listen
        }
        dropItem(it.player.eyePosition(), it.itemStack, it.instance)
    }

    listen<PickupItemEvent> {
        if (it.entity is Player) {
            val rest = (it.entity as Player).inventory.addItemStack(it.itemStack, TransactionOption.ALL)
            if (rest.material() == Material.AIR) return@listen
            if (rest.amount() == it.itemStack.amount()) {
                it.isCancelled = true
                return@listen
            }
            val entity = ItemEntity(rest)
            entity.setInstance(it.instance, it.itemEntity.position)
            entity.setPickupDelay(1, TimeUnit.SECOND)
            entity.scheduleRemove(5, TimeUnit.MINUTE)
        }
    }
}

fun dropItem(playerPosition: Pos, itemStack: ItemStack, instance: Instance) {
    val itemEntity = ItemEntity(itemStack)

    itemEntity.setInstance(instance, playerPosition.withY { it - 0.3 })
    val pitchRad = Math.toRadians(playerPosition.pitch.toDouble())
    val yawRad = Math.toRadians(playerPosition.yaw.toDouble())

    val x = -cos(pitchRad) * sin(yawRad) * 0.3
    val y = -sin(pitchRad) * 0.3 + 0.1
    val z = cos(pitchRad) * cos(yawRad) * 0.3

    itemEntity.velocity = itemEntity.velocity
        .withX(x)
        .withY(y)
        .withZ(z)
        .mul(20.0)
    itemEntity.setPickupDelay(2, TimeUnit.SECOND)
}


fun dropItemOnFloor(position: Pos, itemStack: ItemStack, instance: Instance) {
    val entity = ItemEntity(itemStack)
    entity.setPickupDelay(500, TimeUnit.MILLISECOND)
    entity.velocity = Vec(
        Random.nextDouble() * 2 - 1,
        2.0,
        Random.nextDouble() * 2 - 1
    )
    entity.setInstance(instance, position.add(0.5, 0.5, 0.5))
}
