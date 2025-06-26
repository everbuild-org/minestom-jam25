package org.everbuild.celestia.orion.core.database.bank.models

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayers
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long

interface BankAccount : Entity<BankAccount> {
    companion object : Entity.Factory<BankAccount>()
    val accountId: Int
    var maxAmount: Long
    var amount: Long
    var owner: OrionPlayer
}

object BankAccounts : Table<BankAccount>("orion_bank_account") {
    val accountId = int("account_id").primaryKey().bindTo { it.accountId }
    val maxAmount = long("maxAmount").bindTo { it.maxAmount }
    val amount = long("amount").bindTo { it.amount }
    val owner = int("owner").references(OrionPlayers) { it.owner }
}

val Database.bankAccounts get() = this.sequenceOf(BankAccounts)