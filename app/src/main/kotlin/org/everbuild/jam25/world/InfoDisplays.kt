package org.everbuild.jam25.world

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.core.util.minimessage


data class TextDisplayConfig(
    val x: Double,
    val y: Double,
    val z: Double,
    val text: String,
)

object InfoDisplays {
    private val configs = listOf(
        TextDisplayConfig(-16.43, -8.72, 23.55, "<gray><gold>Get Metal Scraps and Silicon Dust from the <red>mines</red>!"),
        TextDisplayConfig(49.74, -8.73, 141.81, "<gray><gold>Get Metal Scraps and Silicon Dust from the <red>mines</red>!"),
        TextDisplayConfig(-10.61, -7.98, 119.87, "<gray><aqua>Pick up oil</aqua> to power your generator using a <blue>vacuum</blue>"),
        TextDisplayConfig(42.22, -7.61, 44.23, "<gray><aqua>Pick up oil</aqua> to power your generator using a <blue>vacuum</blue>"),
        TextDisplayConfig(-5.48, -7.61, 53.78, "<gray><green>Walk up here</green> to get <dark_green>Bio scraps</dark_green>"),
        TextDisplayConfig(37.68, -7.23, 111.74, "<gray><green>Walk up here</green> to get <dark_green>Bio scraps</dark_green>")
    )

    fun spawnAll(instance: Instance) {
        configs.forEach { config ->
            val entity = Entity(EntityType.TEXT_DISPLAY)
            entity.setNoGravity(true)

            entity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
                meta.text = config.text.minimessage()
                meta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
                meta.isSeeThrough = true
            }

            entity.setInstance(instance, Pos(config.x, config.y, config.z))
        }
    }
}
