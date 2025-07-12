package org.everbuild.jam25.state.ingame

import org.everbuild.jam25.world.GamePointOfInterests
import org.everbuild.jam25.world.GameWorld

enum class GameTeamType(val short: String, val long: String, val poi: GamePointOfInterests) {
    RED("<red>█</red>", "<red>RED</red>", GameWorld.red),
    BLUE("<blue>█</blue>", "<blue>BLUE</blue>", GameWorld.blue)
}