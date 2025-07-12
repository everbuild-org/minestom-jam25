package org.everbuild.jam25.state.ingame

import org.everbuild.jam25.world.GameWorld

enum class GameTeamType(val short: String, val long: String, val poi: () -> GameWorld.Poi) {
    RED("<red>█</red>", "<red>RED</red>", { GameWorld.Poi.Red() }),
    BLUE("<blue>█</blue>", "<blue>BLUE</blue>", { GameWorld.Poi.Blue() })
}