package org.everbuild.jam25.world

import net.hollowcube.schem.Rotation
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import org.everbuild.jam25.world.shield.Shield


class GameWorld : ZippedWorld("map") {
    sealed class Poi(
        val spawn: Pos,
        val turret: Turret,
        val area: FlatArea,
        val minY: Int = -75,
        val maxY: Int = -40,
        val mainShield: Shield = blueShield,
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

    companion object {
        val blueShield = Shield(
            listOf(
                listOf(
                    Pos(16.5, -56.00, -0.5),
                    Pos(0.5, -56.00, -0.5),
                    Pos(0.5, -50.00, -7.5),
                    Pos(16.5, -50.00, -7.5)
                ),
                listOf(
                    Pos(0.5, -50.00, -7.5),
                    Pos(16.5, -50.00, -7.5),
                    Pos(16.5, -50.00, -25.5),
                    Pos(0.5, -50.00, -25.5)
                )
            )
        )
    }
}
