package org.everbuild.celestia.orion.core.database.bank

import org.everbuild.celestia.orion.core.database.bank.models.BankAccount
import org.everbuild.celestia.orion.core.database.bank.models.BankAccounts
import org.everbuild.celestia.orion.core.database.bank.models.BankCard
import org.everbuild.celestia.orion.core.database.bank.models.BankCards
import org.everbuild.celestia.orion.core.database.bank.models.bankAccounts
import org.everbuild.celestia.orion.core.database.database
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.first
import org.ktorm.entity.toList

sealed interface BankAccountSelector<T> {
    fun resolve(): T

    data class ByUser(val player: OrionPlayer) : BankAccountSelector<List<BankAccount>> {
        override fun resolve(): List<BankAccount> {
            return database.bankAccounts.filter { it.owner eq player.id }.toList()
        }
    }

    data class ByBankCard(val bankCardSelector: BankCardSelector.SingleCardSelector) :
        BankAccountSelector<BankAccount> {
        override fun resolve(): BankAccount {
            val bankCardId = bankCardSelector.getId()

            val id = database
                .from(BankCards)
                .leftJoin(BankAccounts, BankCards.accountId eq BankAccounts.accountId)
                .select(BankAccounts.accountId)
                .where(BankCards.cardId eq bankCardId)
                .limit(1)
                .map { it[BankAccounts.accountId] }
                .first()!!

            return database.bankAccounts.first { it.accountId eq id }
        }
    }

    data class ByAccountId(val accountId: Int) : BankAccountSelector<BankAccount> {
        override fun resolve(): BankAccount {
            return database.bankAccounts.first { it.accountId eq accountId }
        }
    }
}