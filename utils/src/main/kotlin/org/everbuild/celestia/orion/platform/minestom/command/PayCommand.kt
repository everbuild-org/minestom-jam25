package org.everbuild.celestia.orion.platform.minestom.command

import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.command.CoinCommand.runPayCommand
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated

object PayCommand : Kommand("pay") {
    init {
        default { _, _ ->
            player.sendTranslated("orion.command.coins.pay.usage")
        }

        val argPlayer = Arg.orionPlayer("payed-player")
        val argAmount = Arg.long("amount")

        requiresPermission("orion.command.coins.pay") {
            executes(argPlayer, argAmount) {
                runPayCommand(argAmount, argPlayer)
            }
        }
    }
}