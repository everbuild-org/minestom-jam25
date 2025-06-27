package org.everbuild.celestia.orion.core.database.bank

import org.everbuild.celestia.orion.core.database.DataSource
import org.everbuild.celestia.orion.core.database.bank.models.BankCard
import org.intellij.lang.annotations.Language

fun BankCard.asSelector() = object : BankCardSelector.SingleCardSelector {
    override fun getId(): Int = this@asSelector.cardId
    override fun resolve(): BankCard = this@asSelector
}

fun BankCard.checkPin(pin: String): Boolean {
    return HashUtil.checkPin(pin, pinHash)
}

fun BankCard.getAmountLeftToday(): Long {
    if (spendingLimit.toLong() == 0L) return Long.MAX_VALUE

    @Language("SQL")
    val sql =
        "SELECT SUM(amount) AS amount FROM (SELECT IF(amount > 0, 0, amount) AS amount, 1 as id FROM orion_bank_transactions WHERE diagnostics LIKE 'cardId=${cardId}:%' AND (timestamp BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL 1 DAY)) tx GROUP BY id"
    return DataSource.database.useConnection {
        it.prepareStatement(sql)
            .use { statement ->
                statement.executeQuery().use { result ->
                    if (result.next()) {
                        spendingLimit + result.getInt("amount")
                    } else {
                        spendingLimit
                    }
                }
            }
    }.toLong()
}