package org.everbuild.jam25.block.impl.missile1

import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.everbuild.jam25.block.api.CustomBlock
import org.everbuild.jam25.block.api.PlacementActor
import org.everbuild.jam25.missile.Missile
import org.everbuild.jam25.missile.MissileEntity
import org.everbuild.jam25.missile.MissileType

object Missile1Block : CustomBlock {
    override fun placeBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        player.getTeam()?.spawnMissile(position, instance, Missile(MissileEntity(MissileType.MISSILE_1)))
    }

    override fun breakBlock(instance: Instance, position: BlockVec, player: PlacementActor) {
        // won't happen
    }

    override fun update(
        instance: Instance,
        position: BlockVec
    ) {
        // won't happen
    }

    override fun key(): Key = Key.key("jam", "missile1")
}