package org.everbuild.jam25.command

import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.jam25.Jam
import kotlin.time.Duration.Companion.seconds

object QuickStartCommand : Kommand("quickstart") {
    init {
        permission = "jam.quickstart"
        default { _, _ ->
            player.sendMiniMessage("<green>Quickstart activated")
            for (i in 1..3) i.seconds later{
                player.sendMiniMessage("${Jam.PREFIX} <green>$i</green>")
            }
            3.seconds later {
                Jam.gameStates.tryQuickStart(player)
            }
        }
    }
}