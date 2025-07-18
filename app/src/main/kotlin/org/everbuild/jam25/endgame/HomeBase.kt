package org.everbuild.jam25.endgame

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.jam25.Jam
import org.everbuild.jam25.state.ingame.GameTeam

class HomeBase(val team: GameTeam) {
    val hpTotal = 10
    var hpLeft = hpTotal
    var enabled = false
        set(value) {
            field = value
            if (field) ensureSpawned()
            else ensureRemoved()
        }

    val negativeTwo = OrionPacks.getCharacterCodepoint("spacing_-2")

    val entity = Entity(EntityType.TEXT_DISPLAY).also { entity ->
        entity.setNoGravity(false)
        entity.editEntityMeta(TextDisplayMeta::class.java) {
            it.text = "Home Base".minimessage()
            it.translation = Vec(0.0, 10.0, 0.0)
            it.scale = Vec(2.0, 2.0, 2.0)
            it.viewRange = 5f
            it.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
        }
    }

    val bossBar = BossBar.bossBar(
        "${team.type.short} ${team.type.long} <gray>Home Base</gray>".minimessage(),
        1.0f,
        BossBar.Color.GREEN,
        BossBar.Overlay.NOTCHED_10
    )

    val damageSound = Sound.sound {
        it.type(Key.key("entity.guardian.hurt"))
        it.volume(0.5f)
    }

    init {
        updateHealth()
    }

    private fun ensureSpawned() {
        team.opposite.sendMiniMessage("${Jam.PREFIX} <green>The shield of ${team.type.long} has fallen!")
        team.players.forEach { bossBar.addViewer(it) }
        team.opposite.players.forEach { bossBar.addViewer(it) }
        entity.setInstance(team.game.world.instance, team.poi.spawn)
    }

    private fun ensureRemoved() {
        team.players.forEach { bossBar.removeViewer(it) }
        team.opposite.players.forEach { bossBar.removeViewer(it) }
        entity.remove()
    }

    private fun updateHealth() {
        val ratio = hpLeft.toFloat() / hpTotal.toFloat()
        bossBar.progress(ratio)

        val blocks = (ratio * 10).toInt()
        val spaces = 10 - blocks
        val bar = "<green>" + "█${negativeTwo}".repeat(blocks) + "<red>" + "█${negativeTwo}".repeat(spaces)

        entity.editEntityMeta(TextDisplayMeta::class.java) {
            it.text = "${team.type.short} <gray>Home Base</gray>\n<gray>  $bar  </gray>\n<gray>$hpLeft/$hpTotal</gray>".minimessage()
        }
    }

    fun damage(by: Int) {
        hpLeft -= by
        if (hpLeft <= 0) {
            Jam.gameStates.endGame(team.game, team.opposite)
            enabled = false
            return
        }
        team.forEach { it.playSound(damageSound, Sound.Emitter.self()) }
        updateHealth()
    }

    fun disable() {
        enabled = false
        ensureRemoved()
    }
}