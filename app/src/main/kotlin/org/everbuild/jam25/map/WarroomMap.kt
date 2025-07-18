package org.everbuild.jam25.map

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.tag.Tag
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.state.ingame.GameTeamType
import org.joml.Vector2i
import java.util.concurrent.CompletableFuture

class WarroomMap(val base: Pos, val dir: Vec) : Entity(EntityType.TEXT_DISPLAY) {
    val infoChild = Entity(EntityType.TEXT_DISPLAY).also {
        it.editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text =
                "<red>Right click on enemy territory to shoot\n<red> You can place multiple markers".minimessage()
            meta.backgroundColor = 0x7f000000
            meta.scale = Vec(0.7, 0.7, 0.7)
        }
        it.setNoGravity(true)
    }
    var interactionController: InteractionController? = null

    val displayBase = DisplayBase()

    val xEntities = mutableListOf<Entity>()

    lateinit var team: GameTeam

    val enableSound = Sound.sound {
        it.type(Key.key("ui.button.click"))
        it.volume(0.2f)
        it.pitch(1f)
    }
    val disableSound = Sound.sound {
        it.type(Key.key("ui.button.click"))
        it.volume(0.2f)
        it.pitch(0.8f)
    }

    init {
        editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = "\n\n\n\n ${OrionPacks.getCharacterCodepoint("war_map")} \n".minimessage()
            meta.scale = Vec(2.0, 2.0, 2.0)
            meta.backgroundColor = 0x7f000000
        }
        setNoGravity(true)
    }

    override fun setInstance(instance: Instance): CompletableFuture<Void?>? {
        interactionController = InteractionController(base, dir, instance, 2, 3, 0.05, team.type == GameTeamType.RED, ::onClick)
        infoChild.setInstance(instance, base.add(base.direction().normalize().mul(0.01)))
        displayBase.setInstance(instance, base)
        return super.setInstance(instance, base)
    }

    fun createX(x: Int, y: Int, bp: Pos, tp: Vector2i) {
        xEntities.add(Entity(EntityType.TEXT_DISPLAY).also {
            it.setNoGravity(true)
            it.setInstance(instance, bp.add(bp.direction().mul(0.01)))
            it.editEntityMeta(TextDisplayMeta::class.java) { meta ->
                meta.text = "<dark_red><b>⨯".minimessage()
                meta.backgroundColor = 0x0
                meta.scale = Vec(0.25, 0.25, 0.25)
                meta.translation = Vec(0.01, -0.0125, -0.001)
            }
            it.setTag(xTag, x)
            it.setTag(yTag, y)
            it.setTag(tpTag, tp)
        })

        team.targetPositions.add(tp)
    }

    fun removeX(x: Int, y: Int) = xEntities.removeIf { entity ->
        val xTagVal = entity.getTag(xTag)
        val yTagVal = entity.getTag(yTag)

        if (xTagVal == x && yTagVal == y) {
            val tp = entity.getTag(tpTag)
            team.targetPositions.remove(tp)
            entity.remove()
            return@removeIf true
        } else {
            return@removeIf false
        }
    }

    fun removeX(pos: Vector2i) {
        xEntities.removeIf {
            if (!it.hasTag(tpTag)) return@removeIf false
            val tag = it.getTag(tpTag)
            if (tag != pos) return@removeIf false
            it.remove()
            return@removeIf true
        }
    }

    fun toggleX(x: Int, y: Int, bp: Pos, tp: Vector2i): Boolean {
        if (!removeX(x, y)) {
            createX(x, y, bp, tp)
            return true
        }
        return false
    }

    fun onClick(x: Int, y: Int, bp: Pos, player: Player) {
        println("WarroomMap: clicked on ($x, $y) at $bp")
        var pos = team.opposite.poi.mapper.mapToWorld(x, y)
        if (team.opposite.type == GameTeamType.RED) {
            pos = pos.add(0, -6)
        } else {
            pos = pos.add(0, -6)
        }
        if (toggleX(x, y, bp, pos)) player.playSound(enableSound, Sound.Emitter.self())
        else player.playSound(disableSound, Sound.Emitter.self())
    }

    override fun remove() {
        infoChild.remove()
        displayBase.remove()
        interactionController?.remove()
        super.remove()
    }

    companion object {
        val xTag = Tag.Integer("x")
        val yTag = Tag.Integer("y")
        val tpTag = Tag.Transient<Vector2i>("tp")
    }
}