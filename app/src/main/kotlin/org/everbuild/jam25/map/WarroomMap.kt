package org.everbuild.jam25.map

import java.util.concurrent.CompletableFuture
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.minimessage

class WarroomMap(val base: Pos) : Entity(EntityType.TEXT_DISPLAY) {
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

    init {
        editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = "\n\n\n\n ${OrionPacks.getCharacterCodepoint("war_map")} \n".minimessage()
            meta.scale = Vec(2.0, 2.0, 2.0)
            meta.backgroundColor = 0x7f000000
        }
        setNoGravity(true)
    }

    override fun setInstance(instance: Instance): CompletableFuture<Void?>? {
        interactionController = InteractionController(base.add(base.direction().normalize().rotateAroundY(90.0).mul(2.0)), instance)
        infoChild.setInstance(instance, base.add(base.direction().normalize().mul(0.01)))
        displayBase.setInstance(instance, base)
        return super.setInstance(instance, base)
    }

    override fun remove() {
        infoChild.remove()
        displayBase.remove()
        interactionController?.remove()
        super.remove()
    }
}