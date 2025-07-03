package org.everbuild.celestia.orion.core.scoreboard

import net.kyori.adventure.text.Component
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.c

interface ScoreBoardCallback {
    /**
     * Get The Key-Value Pair for a Scoreboard entry
     *
     * @param player The Player, the Scoreboard is generated for
     * @param id The number of the scoreboard entry [0,1,2,3,4]
     * @return [Key, Prefix, Suffix]
     */
    fun getKV(player: OrionPlayer, id: Int): Array<Component>

    /**
     * Get the Scoreboard Title
     *
     * @param player The Player, the Scoreboard is generated for
     * @return The Title for the Scoreboard
     */
    fun getTitle(player: OrionPlayer): Component {
        return player.c("orion.scoreboard.default.title")
    }

    fun getExtra(player: OrionPlayer): Component = Component.text("")

    /**
     * Should the ScoreBoard be showed
     *
     * @param player The Player, the Scoreboard is generated for
     * @return The Display Status
     */
    fun active(player: OrionPlayer): Boolean
}