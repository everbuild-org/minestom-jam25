package org.everbuild.jam25.shop

import kotlin.math.min
import net.kyori.adventure.text.Component
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.util.listen


abstract class ShopNPC(val name: String, val skin: PlayerSkin?) : LivingEntity(EntityType.PLAYER) {
    init {
        isCustomNameVisible = true
        set(DataComponents.CUSTOM_NAME, name.minimessage())
        setNoGravity(true)
        setPlayerSkin()

        listen(::onInteract)
    }

    private fun onInteract(event: PlayerEntityInteractEvent) {
        if (event.target != this) return
        openMenu(event.player)
    }

    abstract fun openMenu(player: Player)

    private fun setPlayerSkin() {
        if (entityType !== EntityType.PLAYER || !isActive) return
        val removePacket = PlayerInfoRemovePacket(uuid)
        val destroyPacket = DestroyEntitiesPacket(entityId)
        for (p in viewers) {
            p.sendPacket(removePacket)
            p.sendPacket(destroyPacket)
            updateNewViewer(p)
        }
    }

    override fun updateNewViewer(player: Player) {
        if (entityType === EntityType.PLAYER) {
            val properties: MutableList<PlayerInfoUpdatePacket.Property?> =
                ArrayList()
            if (skin != null) {
                properties.add(
                    PlayerInfoUpdatePacket.Property(
                        "textures",
                        skin.textures(),
                        skin.signature()
                    )
                )
            }
            val playerEntry = PlayerInfoUpdatePacket.Entry(
                uuid, getTrimmedPlayerUsername(), properties,
                false, 0, GameMode.CREATIVE, Component.empty(), null, 0
            )
            val updatePacket = PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, playerEntry)
            player.sendPacket(updatePacket)
        }
        super.updateNewViewer(player)
    }

    private fun getTrimmedPlayerUsername(): String {
        return name.substring(0, min(name.length, 15))
    }

    companion object {
        val SKIN_MECHANIC = PlayerSkin.fromUuid("022122c9-b99f-4a6f-813d-75d6c01995e6")
        val SKIN_ENGINEER = PlayerSkin.fromUuid("cd8be6f5-2155-4e71-aa74-7dc2a61abb2a")
    }
}