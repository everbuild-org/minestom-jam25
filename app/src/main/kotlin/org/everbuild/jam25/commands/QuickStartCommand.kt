package org.everbuild.jam25.commands

import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.jam25.Jam

object QuickStartCommand : Kommand("quickstart") {
    init {
        permission = "jam.quickstart"
        default { _, _ ->
            player.sendMiniMessage("<green>Quickstart activated")
            Jam.gameStates.tryQuickStart(player)
        }
    }
}