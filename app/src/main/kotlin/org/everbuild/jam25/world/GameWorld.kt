package org.everbuild.jam25.world

import net.minestom.server.coordinate.Pos
import org.everbuild.jam25.world.shield.Shield


class GameWorld : ZippedWorld("map") {
    sealed class Poi(
        val spawn: Pos,
        val area: FlatArea,
        val minY: Int = -75,
        val maxY: Int = -40,
        val mainShield: Shield,
    ) {
        class Red : Poi(
            spawn = Pos(12.5, -7.0, 1.5, 0f, 0f),
            area = FlatArea(-56, -64, 84, 41),
            mainShield = redShield
        )

        class Blue : Poi(
            spawn = Pos(16.5, -7.00, 124.5, 180f, 0f),
            area = FlatArea(-30, -67, 55, 4),
            mainShield = blueShield
        )
    }

    companion object {
        val redShield = Shield(
            listOf(
                listOf(
                    Pos(2.5, 10.00, -3.5),
                    Pos(28.5, 10.00, -3.5),
                    Pos(28.5, 10.00, 33.5),
                    Pos(2.5, 10.00, 33.5)
                ),
                listOf(
                    Pos(28.5, 10.00, 33.5),
                    Pos(2.5, 10.00, 33.5),
                    Pos(2.5, 3.00, 40.5),
                    Pos(28.5, 3.00, 40.5)
                )
            )
        )

        val blueShield = Shield(
            listOf(
                listOf(
                    Pos(-5.5, 12.00, 129.5),
                    Pos(20.5, 12.00, 129.5),
                    Pos(20.5, 12.00, 92.5),
                    Pos(-5.5, 12.00, 92.5)
                ),
                listOf(
                    Pos(20.5, 12.00, 92.5),
                    Pos(-5.5, 12.00, 92.5),
                    Pos(-5.5, 5.00, 85.5),
                    Pos(20.5, 5.00, 85.5)
                )
            )
        )
    }
}
