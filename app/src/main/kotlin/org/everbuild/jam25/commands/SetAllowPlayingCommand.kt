package org.everbuild.jam25.commands

import net.minestom.server.adventure.audience.Audiences
import org.everbuild.celestia.orion.core.util.sendMiniMessage
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.jam25.Jam

object SetAllowPlayingCommand : Kommand("setallowplaying") {
    var allowPlaying = true

    init {
        permission = "jam.setallowplaying"
        val arg = Arg.bool("allowPlaying")
        executes(arg) {
            allowPlaying = arg()
            player.sendMiniMessage("<green>Allow playing is now set to $allowPlaying")
            Audiences.all().sendMiniMessage("${Jam.PREFIX} <green>Allow playing is now set to $allowPlaying by ${player.username}")
        }
    }
}