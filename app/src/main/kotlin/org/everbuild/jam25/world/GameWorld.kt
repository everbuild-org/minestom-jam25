package org.everbuild.jam25.world

import net.hollowcube.schem.Rotation
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos

class GameWorld : ZippedWorld("map") {
    companion object {
        val blue = GamePointOfInterests(
            spawn = Pos(9.5, -61.0, 39.5, -180f, 0f),
            turret = Turret(
                pos = BlockVec(8, -61, 23),
                rotation = Rotation.NONE
            )
        )
        val red = GamePointOfInterests(
            spawn = Pos(8.5, -61.0, -23.5, 0f, 0f),
            turret = Turret(
                pos = BlockVec(8, -61, -8),
                rotation = Rotation.CLOCKWISE_180
            )
        )
    }
}