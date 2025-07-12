package org.everbuild.jam25.world

import net.hollowcube.schem.Rotation
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos

data class GamePointOfInterests(
    val spawn: Pos,
    val turret: Turret,
)

open class SpawnableStructure(val pos: BlockVec, val rotation: Rotation, val type: String)
class Turret(pos: BlockVec, rotation: Rotation) : SpawnableStructure(pos, rotation, "turret")