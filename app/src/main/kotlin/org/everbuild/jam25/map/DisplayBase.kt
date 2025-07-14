package org.everbuild.jam25.map

import net.minestom.server.entity.Entity
import java.util.concurrent.CompletableFuture
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.core.util.component

class DisplayBase() : Entity(EntityType.TEXT_DISPLAY) {
    private val passenger1Display: Entity
    private val passenger2Display: Entity

    init {
        this.setNoGravity(true)

        this.editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = "".component()
            meta.textOpacity = -1
            meta.lineWidth = 200
            meta.isSeeThrough = false
            meta.isShadow = false
            meta.backgroundColor = 0x0

            meta.scale = Vec(7.624999, 2.5000007, 1.0)
            meta.translation = Vec(0.0, 0.0, 0.0)
            meta.leftRotation = floatArrayOf(-0.70710677f, 0.0f, 0.0f, 0.70710677f)
        }

        passenger1Display = Entity(EntityType.TEXT_DISPLAY).apply {
            setNoGravity(true)
            editEntityMeta(TextDisplayMeta::class.java) { meta ->
                meta.text = Component.text("▭").color(TextColor.fromHexString("#005959"))
                meta.textOpacity = -1
                meta.lineWidth = 200
                meta.isSeeThrough = false
                meta.isShadow = false
                meta.backgroundColor = 0x0

                meta.scale = Vec(1.8125005, 2.1249998, 0.99999994)
                meta.translation = Vec(0.104123235, -0.13700277, -0.06485047).rotateAroundY(Math.toRadians(position.yaw.toDouble()))
                meta.leftRotation = floatArrayOf(-0.7071068f, 0.0f, 0.0f, 0.7071068f)
            }
        }

        passenger2Display = Entity(EntityType.TEXT_DISPLAY).apply {
            setNoGravity(true)
            editEntityMeta(TextDisplayMeta::class.java) { meta ->
                meta.text = Component.text("▭").color(TextColor.fromHexString("#039999"))
                meta.textOpacity = -1
                meta.lineWidth = 200
                meta.isSeeThrough = false
                meta.isShadow = false
                meta.backgroundColor = 0x0

                meta.scale = Vec(4.75, 2.5000007, 1.0000001)
                meta.translation = Vec(0.055664062, -0.07450277, 0.0).rotateAroundY(Math.toRadians(position.yaw.toDouble()))
                meta.leftRotation = floatArrayOf(-0.7071068f, 0.0f, 0.0f, 0.70710677f)
            }
        }

    }

    override fun setInstance(instance: Instance, position: Pos): CompletableFuture<Void?> {
        val absPos = position.add(Vec(-0.1, -0.05, 0.3))
        val mainFuture = super.setInstance(instance, absPos)

        val passenger1Future = passenger1Display.setInstance(instance)
        val passenger2Future = passenger2Display.setInstance(instance)

        return CompletableFuture.allOf(mainFuture, passenger1Future, passenger2Future).thenRun {
            this.addPassenger(passenger1Display)
            this.addPassenger(passenger2Display)
        }
    }

    override fun remove() {
        passenger1Display.remove()
        passenger2Display.remove()
        super.remove()
    }
}
