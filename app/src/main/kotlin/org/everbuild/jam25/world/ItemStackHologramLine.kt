package org.everbuild.jam25.world

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.ItemDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import org.everbuild.asorda.resources.data.shader.TextShader
import org.everbuild.celestia.orion.core.util.minimessage

class ItemStackHologramLine(pos: BlockVec, index: Int, resource: ItemStack) {
    val basePos = pos.add(0.5, 0.5 + 0.5 * index, 0.5)
    val textPos = basePos.sub(0.0, 0.0, 0.0)

    var amount = 0
        set(value) {
            if (field == value) return
            field = value
            update()
        }

    val baseEntity = Entity(EntityType.ITEM_DISPLAY).also {
        it.setNoGravity(true)
        it.editEntityMeta(ItemDisplayMeta::class.java) { meta ->
            meta.itemStack = resource
            meta.displayContext = ItemDisplayMeta.DisplayContext.GROUND
            meta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
        }
    }

    val textEntity = Entity(EntityType.TEXT_DISPLAY).also {
        it.setNoGravity(true)
        it.editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = getText()
            meta.backgroundColor = 0x00000000
            meta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
        }
    }

    fun update() {
        textEntity.editEntityMeta(TextDisplayMeta::class.java) { meta ->
            meta.text = getText()
        }
    }

    fun getText(): Component {
        val amountLen = amount.toString().length
        return "<${TextShader.INVIS}>. ${" ".repeat(amountLen.coerceAtLeast(0))}      <white>x$amount".minimessage()
    }

    fun setInstance(instance: Instance) {
        baseEntity.setInstance(instance, basePos)
        textEntity.setInstance(instance, textPos)
    }

    fun remove() {
        baseEntity.remove()
        textEntity.remove()
    }
}