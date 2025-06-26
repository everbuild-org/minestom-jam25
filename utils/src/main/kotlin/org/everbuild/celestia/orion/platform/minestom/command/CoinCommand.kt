package org.everbuild.celestia.orion.platform.minestom.command

import net.minestom.server.command.builder.arguments.number.ArgumentLong
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.database.coins.TransactionResult
import org.everbuild.celestia.orion.core.database.coins.removeCoins
import org.everbuild.celestia.orion.core.database.coins.setCoins
import org.everbuild.celestia.orion.core.database.coins.transactCoinsTo
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.isOnline
import org.everbuild.celestia.orion.core.database.playerdata.sendTranslated
import org.everbuild.celestia.orion.core.translation.SharedTranslations
import org.everbuild.celestia.orion.core.util.asUuid
import org.everbuild.celestia.orion.platform.minestom.api.command.*
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission
import org.everbuild.celestia.orion.platform.minestom.util.orion
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated
import kotlin.math.abs

object CoinCommand : Kommand("coins") {
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
        val argAmount = Arg.long("amount")
        val argBal = Arg.literal("bal")
        val argPay = Arg.literal("pay")
        val argSet = Arg.literal("set")
        val argAdd = Arg.literal("add")
        val argRemove = Arg.literal("remove")

        requiresPermission("orion.command.coins.bal.others") {
            executes(argPlayer) {
                sendBal(player, argPlayer())
            }

            executes(argBal, argPlayer) {
                sendBal(player, argPlayer())
            }
        }

        requiresPermission("orion.command.coins.pay") {
            executes(argPay, argPlayer, argAmount) {
                runPayCommand(argAmount, argPlayer)
            }
        }

        requiresPermission("orion.command.coins.set") {
            executes(argSet, argPlayer, argAmount) {
                val coins = try {
                    argAmount()
                } catch (ignored: NumberFormatException) {
                    player.sendTranslated(SharedTranslations.InvalidFormat.number)
                    return@executes
                }

                val other = argPlayer()

                other.setCoins(coins, "coins_set::" + player.name)
                player.sendTranslated("orion.command.coins.set.success") {
                    it.replacePlayer("player", player.orion)
                    it.replaceNumber("coins", coins)
                }

                if (other.isOnline) {
                    other.sendTranslated("orion.command.coins.set.success.other") {
                        it.replaceNumber("coins", coins)
                    }
                }
            }
        }

        requiresPermission("orion.command.coins.add") {
            executes(argAdd, argPlayer, argAmount) {
                transactImmutable(player, argPlayer(), argAmount(), true, "add")
            }
        }

        requiresPermission("orion.command.coins.remove") {
            executes(argRemove, argPlayer, argAmount) {
                transactImmutable(player, argPlayer(), argAmount(), false, "remove")
            }
        }
    }

    fun KommandContext.KommandExecutionContext.runPayCommand(
        argAmount: ArgumentLong,
        argPlayer: OrionPlayerArgument
    ) {
        val amount = try {
            abs(argAmount())
        } catch (ignored: NumberFormatException) {
            player.sendTranslated(SharedTranslations.InvalidFormat.number)
            return
        }

        val other = argPlayer()

        if (other.uuid.asUuid() == player.uuid) {
            player.sendTranslated("orion.command.coins.pay.self")
            return
        }

        when (player.orion.transactCoinsTo(other, amount, "coins_pay")) {
            TransactionResult.PLAYER_NO_MONEY -> {
                player.sendTranslated(SharedTranslations.notEnoughCoins)
            }

            TransactionResult.PARTNER_NO_MONEY -> {
                player.sendTranslated(SharedTranslations.notEnoughCoinsOther)
            }

            TransactionResult.OK -> {
                player.sendTranslated("orion.command.coins.pay.success") {
                    it.replacePlayer("player", other)
                    it.replaceNumber("coins", amount)
                }
                if (other.isOnline) {
                    other.sendTranslated("orion.command.coins.pay.success.other") {
                        it.replacePlayer("player", player.orion)
                        it.replaceNumber("coins", amount)
                    }
                }
            }
        }
    }

    fun sendBal(sender: Player, player: OrionPlayer) {
        sender.sendTranslated("orion.command.coins.bal.others") {
            it.replace("player", player)
            it.replace("coins", player.coins)
        }
    }

    private fun transactImmutable(
        sender: Player,
        other: OrionPlayer,
        theAmount: Long,
        doNegate: Boolean,
        type: String
    ) {
        val rawAmount = if (doNegate) -abs(theAmount) else abs(theAmount)
        val amount = if (doNegate) -abs(rawAmount) else abs(theAmount)
        if (!other.removeCoins(amount, type + "_" + sender.name)) {
            sender.sendTranslated("orion.command.coins.notenoughcoins.other")
        } else {
            sender.sendTranslated("orion.command.coins.$type.success") {
                it.replacePlayer("player", other)
                it.replaceNumber("coins", abs(amount))
            }
            if (other.isOnline) {
                other.sendTranslated("orion.command.coins.$type.success.other") {
                    it.replacePlayer("player", sender.orion)
                    it.replaceNumber("coins", abs(amount))
                }
            }
        }

        return
    }
}