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
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.metadata.item.ItemEntityMeta
import org.everbuild.jam25.state.ingame.GameTeam

class ResourceNode(val pos: Pos, val spawneableResource: SpawneableResource) : AdvanceableWorldElement {
    lateinit var inst: Instance
    lateinit var labelEntity: Entity
    lateinit var timerEntity: Entity
    lateinit var team: GameTeam
    val cooldown = Cooldown(spawneableResource.timeToSpawn)
    val timerCooldown = Cooldown(100.milliseconds)

    fun setInstance(instance: Instance, team: GameTeam) {
        this.inst = instance
        this.team = team

        labelEntity = Entity(EntityType.TEXT_DISPLAY).also { entity ->
            entity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
                meta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
                meta.text = spawneableResource.display.minimessage()
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

    fun trySetTimer(isFull: Boolean) {
        if (!timerCooldown.get()) return
        if ((team.game.start - Clock.System.now()).absoluteValue > 60.seconds && spawneableResource == SpawneableResource.BIO_SCRAPS) {
            timerCooldown.duration = 15.seconds
        }
        val comp = if (isFull) {
            "Resource node full".component().color(NamedTextColor.RED)
        } else {
            "- ${cooldown.getTimeToNextExecution().inWholeSeconds}s -".component().color(NamedTextColor.GRAY)
        }
        timerEntity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = comp
        }
    }

    fun spawnAttempt() {
        dropItemOnFloor(pos.add(0.0, 0.0, 0.0), spawneableResource.item, inst)
    }

    fun checkIsFull(): Boolean {
        val amount = inst.getNearbyEntities(pos, 3.0)
            .filter { it.entityType == EntityType.ITEM }
            .mapNotNull { it.entityMeta as? ItemEntityMeta }
            .sumOf { it.item.amount() }

        return amount > spawneableResource.maxSize
    }

    override fun advance(instance: Instance) {
        val isFull = checkIsFull()
        if (!cooldown.get()) trySetTimer(isFull)
        else if (!isFull) spawnAttempt()
    }

    override fun getBlockPosition(): BlockVec = BlockVec(pos)
}