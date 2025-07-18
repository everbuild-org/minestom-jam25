package org.everbuild.jam25.world

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.utils.Direction
import org.everbuild.jam25.map.MapMapper
import org.everbuild.jam25.map.WarroomMap
import org.everbuild.jam25.resource.SpawneableResource
import org.everbuild.jam25.resource.ResourceNode
import org.everbuild.jam25.shop.MechanicNPC
import org.everbuild.jam25.shop.ShopNPC
import org.everbuild.jam25.shop.UpgradeNPC
import org.everbuild.jam25.world.shield.Shield
import org.everbuild.jam25.world.shield.generator.ShieldGenerator
import org.joml.Vector2i


class GameWorld : ZippedWorld("map") {
    init {
        InfoDisplays.spawnAll(instance)
    }
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
        val shops: List<ShopNPC>,
        val mapper: MapMapper
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
            map = WarroomMap(Pos(16.5, -12.8, 53.5, 180f, 0f), Vec(1.0, 0.0, 0.0)),
            mapper = MapMapper(72, 77, -39, -17),
            shieldGenerator = ShieldGenerator(BlockVec(28, -4, 30), Direction.SOUTH),
            nodes = listOf(
                ResourceNode(Pos.fromPoint(BlockVec(-11, 7, 43)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(-32, 6, 27)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(-16, 7, 33)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(-20, 7, 20)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(-25, -15, 38)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(-18, -15, 42)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(22, -23, 2)), SpawneableResource.SILICON_DUST),
                ResourceNode(Pos.fromPoint(BlockVec(-9, -19, -5)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(4, -23, 56)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(23, -25, 45)), SpawneableResource.SILICON_DUST),
                ResourceNode(Pos.fromPoint(BlockVec(-8, -19, -4)), SpawneableResource.SILICON_DUST),
            ),
            shops = listOf(
                MechanicNPC(Pos(11.0, -14.00, 43.5, -90f, 0f)),
                UpgradeNPC(Pos(22.0, -14.00, 43.5, 90f, 0f))
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
            map = WarroomMap(Pos(16.5, -12.8, 111.5, 0f, 0f), Vec(-1.0, 0.0, 0.0)),
            mapper = MapMapper(-44, 92, 73, 182),
            shieldGenerator = ShieldGenerator(BlockVec(4, -4, 134), Direction.NORTH),
            nodes = listOf(
                ResourceNode(Pos.fromPoint(BlockVec(43, 7, 121)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(48, 7, 131)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(52, 7, 144)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(64, 6, 137)), SpawneableResource.BIO_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(50, -15, 122)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(57, -15, 126)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(28, -23, 108)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(9, -25, 119)), SpawneableResource.SILICON_DUST),
                ResourceNode(Pos.fromPoint(BlockVec(10, -23, 162)), SpawneableResource.SILICON_DUST),
                ResourceNode(Pos.fromPoint(BlockVec(41, -19, 169)), SpawneableResource.METAL_SCRAPS),
                ResourceNode(Pos.fromPoint(BlockVec(40, -19, 168)), SpawneableResource.SILICON_DUST),
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
                    Pos(7.5, 10.00, 24.5),
                    Pos(33.5, 10.00, 24.5),
                    Pos(33.5, 10.00, 61.5),
                    Pos(7.5, 10.00, 61.5)
                ),
                listOf(
                    Pos(33.5, 10.00, 61.5),
                    Pos(7.5, 10.00, 61.5),
                    Pos(7.5, 3.00, 68.5),
                    Pos(33.5, 3.00, 68.5)
                )
            )
        )

        val blueShield = Shield(
            listOf(
                listOf(
                    Pos(-1.5, 12.00, 138.5),
                    Pos(24.5, 12.00, 138.5),
                    Pos(24.5, 12.00, 101.5),
                    Pos(-1.5, 12.00, 101.5)
                ),
                listOf(
                    Pos(24.5, 12.00, 101.5),
                    Pos(-1.5, 12.00, 101.5),
                    Pos(-1.5, 5.00, 94.5),
                    Pos(24.5, 5.00, 94.5)
                )
            )
        )
    }
}
