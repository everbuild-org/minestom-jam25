package org.everbuild.celestia.orion.platform.minestom.scoreboard

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.scoreboard.Sidebar
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.time.TimeUnit
import org.everbuild.celestia.orion.core.scoreboard.ScoreBoardController.scoreBoardCallback
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.core.util.plus
import org.everbuild.celestia.orion.platform.minestom.luckperms.lp
import org.everbuild.celestia.orion.platform.minestom.profiling.Profiler
import org.everbuild.celestia.orion.platform.minestom.util.orion
import java.util.regex.Pattern
import java.util.stream.IntStream
import net.minestom.server.component.DataComponents

object MinestomScoreboardTablistController{
    private val teamManager = MinecraftServer.getTeamManager()
    private val scoreboards = mutableMapOf<Player, Sidebar>()

    private fun updateTabList(player: Player) {
        val lpWrapper = player.lp()
        val expectedTeamName = String.format("%04d", 9999 - lpWrapper.permissionsWeight) + player.username
        var team = teamManager.getTeam(expectedTeamName)
        if (team == null) team = teamManager.createTeam(expectedTeamName)
        team.prefix = lpWrapper.permissionsPrefix.component()
        team.suffix = scoreBoardCallback.getExtra(player.orion)
        team.teamColor = getLastEffectiveColor(lpWrapper.permissionsPrefix)
        team.sendUpdatePacket()

        player.displayName =
            (lpWrapper.permissionsPrefix + player.username).component()
                .append(scoreBoardCallback.getExtra(player.orion))
        player[DataComponents.CUSTOM_NAME] = player.displayName as Component
        player.isCustomNameVisible = true

        MinecraftServer.getTeamManager()
            .teams
            .filter { it.players.contains(player) }
            .filter { it.teamName != expectedTeamName }
            .forEach { it.removeMember(player.username) }

        team.addMember(player.username)
    }

    private fun scoreboardLine(sidebar: Sidebar, line: Int): Sidebar.ScoreboardLine {
        return sidebar.getLine(line.toString()) ?: run {
            sidebar.createLine(Sidebar.ScoreboardLine(line.toString(), Component.text(""), line))
            sidebar.getLine(line.toString())!!
        }
    }

    private fun updateScoreboard(player: Player) {
        val sidebar =
            scoreboards[player] ?: Sidebar(scoreBoardCallback.getTitle(player.orion)).also { scoreboards[player] = it }

        if (!sidebar.isViewer(player) && scoreBoardCallback.active(player.orion)) {
            sidebar.addViewer(player)
        }
        if (sidebar.isViewer(player) && !scoreBoardCallback.active(player.orion)) {
            sidebar.removeViewer(player)
        }

        var c = 0
        for (i in 0..4) {
            val kv = scoreBoardCallback.getKV(player.orion, i)
            scoreboardLine(sidebar, 15 - (c++)).updateContent(sidebar, Component.text(""))
            scoreboardLine(sidebar, 15 - (c++)).updateContent(sidebar, ("§7» ".component() + kv[0]))
            scoreboardLine(sidebar, 15 - (c++)).updateContent(sidebar, ("  ".component() + kv[1]))
        }
    }

    private fun getLastEffectiveColor(text: String): NamedTextColor {
        // Match the last color in the string (§{0123456789abcdef})
        val colorPattern = "§[0-9a-fA-F]"
        val matcher = Pattern.compile(colorPattern).matcher(text)
        var lastColor = NamedTextColor.WHITE
        while (matcher.find()) {
            val color = matcher.group()
            lastColor = when (color) {
                "§0" -> NamedTextColor.BLACK
                "§1" -> NamedTextColor.DARK_BLUE
                "§2" -> NamedTextColor.DARK_GREEN
                "§3" -> NamedTextColor.DARK_AQUA
                "§4" -> NamedTextColor.DARK_RED
                "§5" -> NamedTextColor.DARK_PURPLE
                "§6" -> NamedTextColor.GOLD
                "§7" -> NamedTextColor.GRAY
                "§8" -> NamedTextColor.DARK_GRAY
                "§9" -> NamedTextColor.BLUE
                "§a" -> NamedTextColor.GREEN
                "§b" -> NamedTextColor.AQUA
                "§c" -> NamedTextColor.RED
                "§d" -> NamedTextColor.LIGHT_PURPLE
                "§e" -> NamedTextColor.YELLOW
                "§f" -> NamedTextColor.WHITE
                else -> NamedTextColor.WHITE
            }
        }

        return lastColor
    }

    fun start() {
        MinecraftServer.getSchedulerManager().scheduleTask({
            Profiler("scoreboard") {
                MinecraftServer.getConnectionManager().onlinePlayers.forEach { player ->
                    updateTabList(player)
                    updateScoreboard(player)
                }
            }
        }, TaskSchedule.tick(20), TaskSchedule.duration(500, TimeUnit.MILLISECOND))
    }
}

private fun Sidebar.ScoreboardLine.updateContent(sidebar: Sidebar, text: Component) {
    sidebar.updateLineContent(this.id, text)
}