package org.everbuild.jam25.world

import net.hollowcube.schem.Rotation
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos


class GameWorld : ZippedWorld("map") {
    sealed class Poi(
        val spawn: Pos,
        val turret: Turret,
        val area: FlatArea,
        val minY: Int = -75,
        val maxY: Int = -40,
    ) {
        class Red : Poi(
            spawn = Pos(9.5, -61.0, 39.5, -180f, 0f),
            turret = Turret(
                pos = BlockVec(9, -61, 23),
                rotation = Rotation.CLOCKWISE_180
            ),
            area = FlatArea(-32, 12, 100, 100)
        )

        class Blue : Poi(
            spawn = Pos(8.5, -61.0, -23.5, 0f, 0f),
            turret = Turret(
                pos = BlockVec(7, -61, -8),
                rotation = Rotation.NONE
            ),
            area = FlatArea(-30, -67, 55, 4)
        )
    }
}
