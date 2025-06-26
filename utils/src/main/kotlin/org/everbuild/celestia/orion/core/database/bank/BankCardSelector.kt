package org.everbuild.celestia.orion.core.database.bank

import org.everbuild.celestia.orion.core.database.bank.models.BankCard
import org.everbuild.celestia.orion.core.database.bank.models.bankCards
import org.everbuild.celestia.orion.core.database.database
import org.ktorm.dsl.eq
import org.ktorm.entity.all
import org.ktorm.entity.filter
import org.ktorm.entity.single
import org.ktorm.entity.toList

interface BankCardSelector<T> {
    interface SingleCardSelector : BankCardSelector<BankCard> {
        fun getId(): Int

        class ById(private val id: Int) : SingleCardSelector {
            override fun getId(): Int {
                return id
            }

            override fun resolve(): BankCard = database.bankCards.single { it.cardId eq id }
        }
    }

    fun resolve(): T

    class All : BankCardSelector<List<BankCard>> {
        override fun resolve(): List<BankCard> = database.bankCards.toList()
    }

    class ByAccountId(private val accountId: Int) : BankCardSelector<List<BankCard>> {
        override fun resolve(): List<BankCard> = database.bankCards.filter { it.accountId eq accountId }.toList()
    }
}