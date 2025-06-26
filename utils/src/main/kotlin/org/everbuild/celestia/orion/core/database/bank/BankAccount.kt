package org.everbuild.celestia.orion.core.database.bank

import org.everbuild.celestia.orion.core.database.bank.models.BankAccount
import org.everbuild.celestia.orion.core.database.bank.models.BankCard
import org.everbuild.celestia.orion.core.database.bank.models.BankTransaction
import org.everbuild.celestia.orion.core.database.bank.models.bankTransactions
import org.everbuild.celestia.orion.core.database.coins.CoinTransactionManager
import org.everbuild.celestia.orion.core.database.database
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.ktorm.entity.add

fun BankAccount.transaction(from: OrionPlayer, amount: Long, card: BankCard, data: String): Boolean {
    val coinDAO = CoinTransactionManager(from)
    if (amount > 0 && from.coins < amount) return false
    if (this.amount + amount > maxAmount) return false
    if (amount < 0 && this.amount < -amount) return false

    this.amount += amount
    coinDAO.transact(null, amount, "BANK:TTO$accountId:$data")

    val dataWithCard = "cardId=${card.cardId}:$data"

    database.bankTransactions.add(
        BankTransaction().also {
            it.account = this
            it.amount = this.amount
            it.from = from
            it.diagnostics = dataWithCard
        }
    )

    this.flushChanges()
    return true
}