package org.everbuild.jam25.resource

import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.jam25.listener.dropItemOnFloor
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import kotlin.time.Duration.Companion.milliseconds

class ResourceNode(val pos: Pos, val resource: Resource) : AdvanceableWorldElement {
    lateinit var inst: Instance
    lateinit var labelEntity: Entity
    lateinit var timerEntity: Entity
    val cooldown = Cooldown(resource.timeToSpawn)
    val timerCooldown = Cooldown(100.milliseconds)

    fun setInstance(instance: Instance) {
        this.inst = instance

        labelEntity = Entity(EntityType.TEXT_DISPLAY).also { entity ->
            entity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
                meta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
                meta.text = resource.display.minimessage()
            }
            entity.setNoGravity(true)
            entity.setInstance(instance, pos.add(0.5, 1.5, 0.5))
        }

        timerEntity = Entity(EntityType.TEXT_DISPLAY).also { entity ->
            entity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
                meta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
                meta.text = "--.--".component().color(NamedTextColor.GRAY)
            }
            entity.setNoGravity(true)
            entity.setInstance(instance, pos.add(0.5, 1.0, 0.5))
        }
    }

    fun trySetTimer() {
        if (!timerCooldown.get()) return
        val comp ="- ${cooldown.getTimeToNextExecution().inWholeSeconds}s -".component().color(NamedTextColor.GRAY)
        timerEntity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = comp
        }
    }

    fun spawnAttempt() {
        dropItemOnFloor(pos.add(0.0, 0.0, 0.0), resource.item, inst)
    }

    override fun advance(instance: Instance) {
        if (!cooldown.get()) trySetTimer()
        else spawnAttempt()
    }
}