package org.everbuild.jam25.world

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import org.everbuild.jam25.map.WarroomMap
import org.everbuild.jam25.resource.Resource
import org.everbuild.jam25.resource.ResourceNode
import org.everbuild.jam25.shop.MechanicNPC
import org.everbuild.jam25.shop.ShopNPC
import org.everbuild.jam25.shop.UpgradeNPC
import org.everbuild.jam25.world.shield.Shield
import org.everbuild.jam25.world.shield.generator.ShieldGenerator
import org.joml.Vector2i


class GameWorld : ZippedWorld("map") {
    sealed class Poi(
        val spawn: Pos,
        val area: FlatArea,
        val minY: Int = -28,
        val maxY: Int = 40,
        val mainShield: Shield,
        val oilChunks: Polygon,
        val map: WarroomMap,
        val shieldGenerator: ShieldGenerator,
        val nodes: List<ResourceNode>,
        val shops: List<ShopNPC>
    ) {
        class Red : Poi(
            spawn = Pos(16.5, -7.0, 40.5, 0f, 0f),
            area = FlatArea(-52, -64, 88, 81),
            mainShield = redShield,
            oilChunks = Polygon(
                Vector2i(39, 64),
                Vector2i(69, 66),
                Vector2i(81, 26),
                Vector2i(61, 5),
                Vector2i(56, -17),
                Vector2i(17, -17),
                Vector2i(9, 0),
                Vector2i(12, 15),
                Vector2i(32, 14),
                Vector2i(37, 34)
            ),
            map = WarroomMap(Pos(16.5, -12.8, 53.5, 180f, 0f)),
            shieldGenerator = ShieldGenerator(BlockVec(28, -4, 30)),
            nodes = listOf(),
            shops = listOf(
                MechanicNPC(Pos(11.0, -14.00, 43.5, 90f, 0f)),
                UpgradeNPC(Pos(22.0, -14.00, 43.5, -90f, 0f))
            )
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
            map = WarroomMap(Pos(16.5, -12.8, 111.5, 0f, 0f)),
            shieldGenerator = ShieldGenerator(BlockVec(4, -4, 134)),
            nodes = listOf(
                ResourceNode(Pos.fromPoint(BlockVec(43, 7, 121)), Resource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(48, 7, 131)), Resource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(52, 7, 144)), Resource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(64, 6, 137)), Resource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(50, -15, 122)), Resource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(57, -15, 126)), Resource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(28, -23, 108)), Resource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(9, -25, 119)), Resource.SILICON_DUST),
                ResourceNode(Pos.fromPoint(BlockVec(10, -23, 162)), Resource.SILICON_DUST),
                ResourceNode(Pos.fromPoint(BlockVec(41, -19, 169)), Resource.METAL_SCRAPS)
            ),
            shops = listOf(
                UpgradeNPC(Pos(11.0, -14.00, 121.5, -90f, 0f)),
                MechanicNPC(Pos(22.0, -14.00, 121.5, 90f, 0f))
            )
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
