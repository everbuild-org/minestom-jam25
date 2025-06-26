package org.everbuild.celestia.orion.core.database.coins

import org.everbuild.celestia.orion.core.database.database
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.ktorm.entity.add
import java.security.InvalidParameterException

open class CoinTransactionManager(var target: OrionPlayer) {
    open fun transact(
        transactionPartner: OrionPlayer?,
        playerAccountReduction: Long,
        reason: String
    ): TransactionResult {
        if (target.coins - playerAccountReduction < 0)
            return TransactionResult.PLAYER_NO_MONEY

        if (transactionPartner != null && transactionPartner.coins + playerAccountReduction < 0)
            return TransactionResult.PARTNER_NO_MONEY

        val finalReason = "${System.currentTimeMillis()};P.${target.id}.${transactionPartner?.id ?: "[M]"}.$reason"
        if (finalReason.length > 255) throw InvalidParameterException("Transaction Reason too long")

        target = PlayerLoader.reload(target)
        val partner = if (transactionPartner != null) {
            PlayerLoader.reload(transactionPartner)
        } else {
            null
        }

        database.useTransaction {
            target.coins -= playerAccountReduction
            target.flushChanges()

            if (partner != null) {
                partner.coins += playerAccountReduction
                partner.flushChanges()
            }

            database.coinTransactions.add(CoinTransaction {
                this.player = target
                this.transactionPartner = partner
                this.amount = playerAccountReduction
                this.reason = finalReason
            })
        }

        target = PlayerLoader.reload(target)
        if (partner != null) {
            try {
                PlayerLoader.reload(partner)
            } catch (ignored: Exception) {
            }
        }

        return TransactionResult.OK
    }
}