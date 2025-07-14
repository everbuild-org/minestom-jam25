package org.everbuild.jam25.world

import net.minestom.server.coordinate.Pos
import org.everbuild.jam25.map.WarroomMap
import org.everbuild.jam25.world.shield.Shield
import org.joml.Vector2i


class GameWorld : ZippedWorld("map") {

    sealed class Poi(
        val spawn: Pos,
        val area: FlatArea,
        val minY: Int = -28,
        val maxY: Int = 40,
        val mainShield: Shield,
        val oilChunks: Polygon,
        val map: WarroomMap
    ) {
        class Red : Poi(
            spawn = Pos(12.5, -7.0, 1.5, 0f, 0f),
            area = FlatArea(-56, -64, 84, 41),
            mainShield = redShield,
            oilChunks = Polygon(
                Vector2i(35, 25),
                Vector2i(65, 27),
                Vector2i(77, -13),
                Vector2i(57, -36),
                Vector2i(52, -58),
                Vector2i(13, -58),
                Vector2i(5, -41),
                Vector2i(8, -24),
                Vector2i(28, -25),
                Vector2i(33, -5)
            ),
            map = WarroomMap(Pos(12.5, -12.8, 14.5, 180f, 0f))
        )

        class Blue : Poi(
            spawn = Pos(16.5, -7.00, 124.5, 180f, 0f),
            area = FlatArea(-30, -67, 55, 4),
            mainShield = blueShield,
            oilChunks = Polygon(
                Vector2i(-9, 98),
                Vector2i(-45, 105),
                Vector2i(-49, 146),
                Vector2i(-29, 160),
                Vector2i(-27, 182),
                Vector2i(22, 178),
                Vector2i(22, 150),
                Vector2i(-2, 148)
            ),
            map = WarroomMap(Pos(16.5, -12.8, 111.5, 0f, 0f))
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
