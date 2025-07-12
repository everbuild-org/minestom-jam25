package org.everbuild.jam25.state.lobby

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import net.minestom.server.entity.Player
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.Jam
import org.everbuild.jam25.commands.SetAllowPlayingCommand
import org.everbuild.jam25.state.GameStateController

@OptIn(ExperimentalTime::class)
class LobbyGroup {
    val players = mutableListOf<Player>()
    val audience = DynamicGroup { players.contains(it) }
    var startAt: Instant? = null

    fun process() {
        if (!SetAllowPlayingCommand.allowPlaying) {
            audience.sendMiniMessageActionBar("<red>The game is currently not accepting players.")
            startAt = null
            return
        }
        val canStart = canStartTheoretically()
        if (canStart && startAt == null) {
            startAt = Clock.System.now() + getStartOffsetTime()
            audience.sendMiniMessage("${Jam.PREFIX} <gray>Start has been scheduled. You're playing with:")
            players.forEach { audience.sendMiniMessage("${Jam.PREFIX} <gray>- <white>${it.username}") }
        } else if (!canStart && startAt != null) {
            startAt = null
        }

        if (startAt != null) {
            val start = startAt!!
            if (Clock.System.now() >= start) {
                startGame()
            }
            audience.sendMiniMessageActionBar("<green>Start scheduled in ${(start - Clock.System.now()).inWholeSeconds}s")
            val nextStart = Clock.System.now() + getStartOffsetTime()
            if (nextStart < start) startAt = nextStart
        } else {
            audience.sendMiniMessageActionBar("<red>Start cancelled. Not enough players.")
        }
    }

    fun canStartTheoretically(): Boolean {
        return players.size >= LobbyGameState.MIN_PLAYERS_PER_INSTANCE && players.size <= LobbyGameState.PLAYERS_PER_INSTANCE
    }

    fun getStartOffsetTime(): Duration {
        return if (players.size >= LobbyGameState.PLAYERS_PER_INSTANCE) {
            0.seconds
        } else if (players.size >= LobbyGameState.PLAYERS_PER_INSTANCE_SLOW_START) {
            10.seconds
        } else {
            30.seconds
        }
    }

    fun startGame() {
        Jam.gameStates.transitionIntoPlay(this)
    }

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun containsPlayer(player: Player) = players.contains(player)

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    fun hasSpace(): Boolean {
        return players.size < LobbyGameState.PLAYERS_PER_INSTANCE
    }
}