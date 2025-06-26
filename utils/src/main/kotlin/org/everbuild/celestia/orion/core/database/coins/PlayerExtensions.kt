package org.everbuild.celestia.orion.core.database.coins

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import java.security.InvalidParameterException

fun OrionPlayer.transactCoinsTo(target: OrionPlayer, amount: Long, reason: String): TransactionResult {
    return CoinTransactionManager(this)
        .transact(target, amount, reason)
}

fun OrionPlayer.transactCoinsFrom(source: OrionPlayer, amount: Long, reason: String): TransactionResult {
    return source.transactCoinsTo(this, amount, reason)
}

fun OrionPlayer.removeCoins(amount: Long, reason: String): Boolean {
    return CoinTransactionManager(this)
        .transact(null, amount, reason) == TransactionResult.OK
}

fun OrionPlayer.addCoins(amount: Long, reason: String) {
    CoinTransactionManager(this)
        .transact(null, -amount, reason)
}

fun OrionPlayer.setCoins(amount: Long, reason: String) {
    if (amount < 1) throw InvalidParameterException("Amount cannot be negative")
    addCoins(amount - coins, reason)
}