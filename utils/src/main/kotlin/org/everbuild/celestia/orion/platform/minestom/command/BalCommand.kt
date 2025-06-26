package org.everbuild.celestia.orion.platform.minestom.command

import org.everbuild.celestia.orion.core.translation.SharedTranslations
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.command.CoinCommand.sendBal
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission
import org.everbuild.celestia.orion.platform.minestom.util.orion
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated

object BalCommand : Kommand("bal") {
    init {
        default { _, _ ->
            if (!player.hasPermission("orion.command.coins.bal.self")) {
                player.sendTranslated(SharedTranslations.noPermissions)
                return@default
            }

            player.sendTranslated("orion.command.coins.bal") {
                it.replace("coins", player.orion.coins)
            }
        }
        val argPlayer = Arg.orionPlayer("player")

        requiresPermission("orion.command.coins.bal.others") {
            executes(argPlayer) {
                sendBal(player, argPlayer())
            }
        }
    }
}