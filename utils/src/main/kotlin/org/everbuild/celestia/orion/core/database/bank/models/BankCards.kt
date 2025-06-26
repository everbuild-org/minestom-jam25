package org.everbuild.celestia.orion.core.database.bank.models

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface BankCard : Entity<BankCard> {
    companion object : Entity.Factory<BankCard>()
    val cardId: Int
    var account: BankAccount
    var pinHash: String
    var spendingLimit: Int
    var description: String
}

object BankCards : Table<BankCard>("orion_bank_cards") {
    val cardId = int("cardId").primaryKey().bindTo { it.cardId }
    val accountId = int("accountId").references(BankAccounts) {it.account}
    val pinHash = varchar("pinHash").bindTo { it.pinHash }
    val spendingLimit = int("spendingLimit").bindTo { it.spendingLimit }
    val description = varchar("description").bindTo { it.description }
}

val Database.bankCards get() = this.sequenceOf(BankCards)