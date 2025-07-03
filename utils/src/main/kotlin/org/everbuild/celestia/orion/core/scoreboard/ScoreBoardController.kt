package org.everbuild.celestia.orion.core.scoreboard

import net.kyori.adventure.text.Component
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer

object ScoreBoardController : ScoreBoardCallback {
    var scoreBoardCallback: ScoreBoardCallback = ScoreBoardController
    val teamUniques = listOf(
        "§1",
        "§2",
        "§3",
        "§4",
        "§5",
        "§6",
        "§7",
        "§8",
        "§9",
        "§0",
        "§a",
        "§b",
        "§c",
        "§d",
        "§e",
        "§f"
    )

    /**
     * Get The Key-Value Pair for a Scoreboard entry
     *
     * @param player The Player, the Scoreboard is generated for
     * @param id     The number of the scoreboard entry [0,1,2,3,4]
     * @return [Key, Value]
     */
    override fun getKV(player: OrionPlayer, id: Int): Array<Component> {
        return arrayOf(Component.text("Info"),Component.text("Nr.$id"))
    }

    /**
     * Should the ScoreBoard be showed
     *
     * @param player The Player, the Scoreboard is generated for
     * @return The Display Status
     */
    override fun active(player: OrionPlayer): Boolean {
        return false
    }
}