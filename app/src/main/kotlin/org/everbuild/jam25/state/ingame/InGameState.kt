package org.everbuild.jam25.state.ingame

import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import net.kyori.adventure.key.Key
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.DynamicGroup
import org.everbuild.jam25.GlobalTickEvent
import org.everbuild.jam25.Jam
import org.everbuild.jam25.block.impl.pipe.PipeNetworkController
import org.everbuild.jam25.resource.SpawneableResource
import org.everbuild.jam25.resource.ResourceNode
import org.everbuild.jam25.state.GameState
import org.everbuild.jam25.state.lobby.LobbyGroup
import org.everbuild.jam25.world.GameWorld
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer

class InGameState(lobby: LobbyGroup) : GameState {
    private val id = UUID.randomUUID()
    private val key = Key.key("jam", "in-game/$id")
    private val players = mutableSetOf<Player>()
    private val audience = DynamicGroup { players.contains(it) }
    val world = GameWorld()
    val teamRed: GameTeam
    val teamBlue: GameTeam
    val teams: List<GameTeam>
    val advanceable = mutableSetOf<AdvanceableWorldElement>()
    val networkController = PipeNetworkController(this)
    val start = Clock.System.now()

    private val instanceEvents = EventNode.event("in-game/$id/instance", EventFilter.INSTANCE) {
        it.instance == world.instance
    }

    private val playerEvents = EventNode.event("in-game/$id/player", EventFilter.PLAYER) {
        players.contains(it.player)
    }.listen { event: PlayerDisconnectEvent ->
        players.remove(event.player)
        if (players.isEmpty()) {
            Jam.gameStates.dissolve(this)
        }
    }

    private val sendNukesCooldown = Cooldown(3.seconds)

    private val eventNode = EventNode.all("in-game/$id")
        .addChild(instanceEvents)
        .addChild(playerEvents)
        .listen<GlobalTickEvent, _> {
            val myAdvanceables = advanceable.map { it }
            myAdvanceables.forEach { it.advance(world.instance) }
            if (!sendNukesCooldown.get()) return@listen
            teams.map(GameTeam::tryLaunch)
        }

    init {
        players.addAll(lobby.players)
        var isRed = false
        val redPlayers = mutableListOf<Player>()
        val bluePlayers = mutableListOf<Player>()
        players.toList().shuffled().forEach {
            if (isRed) redPlayers.add(it) else bluePlayers.add(it)
            isRed = !isRed
        }
        teamRed = GameTeam(redPlayers, GameTeamType.RED, this)
        teamBlue = GameTeam(bluePlayers, GameTeamType.BLUE, this)
        teams = listOf(teamRed, teamBlue)

        playerEvents.addChild(teamRed.node)
        playerEvents.addChild(teamBlue.node)

        advanceable.add(teamRed.poi.shieldGenerator)
        advanceable.add(teamBlue.poi.shieldGenerator)

        advanceable.addAll(teams.flatMap { it.poi.nodes }.toList())

        val jobs = mutableListOf<CompletableFuture<*>>()
        for (x in -10..10) {
            for (z in -10..10) {
                jobs.add(world.instance.loadChunk(x, z))
            }
        }
        CompletableFuture.allOf(*jobs.toTypedArray()).join()

        teams.forEach {
            it.init(world.instance)
        }

        teamRed.setInstance(world.instance, teamRed.poi.spawn)
        teamBlue.setInstance(world.instance, teamBlue.poi.spawn)
    }

    fun teamOf(player: Player): GameTeam? = teams.find { it.players.contains(player) }

    fun createResourceNode(type: SpawneableResource, pos: Pos) {
        advanceable.add(ResourceNode(pos, type).also { it.setInstance(world.instance, teamRed) })
    }

    fun teamAt(position: BlockVec): GameTeam {
        return teams.find { it.poi.area.contains(position) }
            ?: throw IllegalArgumentException("No team found at $position")
    }

    fun dissolve() {
        teams.forEach {
            it.homeBase.disable()
            for (missile in it.missileTracker) {
                missile.remove()
            }
        }
        Mc.instance.unregisterInstance(world.instance)
    }

    inline fun <reified T> getAdvanceable(pos: BlockVec): T? = advanceable
        .filter { it.getBlockPosition() == pos }
        .filterIsInstance<T>()
        .firstOrNull()
    override fun events(): EventNode<out Event> = eventNode
    override fun players(): List<Player> = players.toList()
    override fun key(): Key = key
}