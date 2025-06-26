package org.everbuild.celestia.orion.core.database.bank.models

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayers
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.timestamp
import java.time.Instant

interface BankTransaction : Entity<BankTransaction> {
    companion object : Entity.Factory<BankTransaction>()
    val txId: Int
    var account: BankAccount
    var from: OrionPlayer?
    var amount: Long
    var diagnostics: String
    var timestamp: Instant
}

object BankTransactions : Table<BankTransaction>("orion_bank_transactions") {
    val id = int("id").primaryKey().bindTo { it.txId }
    val account = int("accountId").references(BankAccounts) { it.account }
    val from = int("from").references(OrionPlayers) { it.from }
    val diagnostics = text("diagnostics").bindTo { it.diagnostics }
    val timestamp = timestamp("timestamp").bindTo { it.timestamp }
}

val Database.bankTransactions get() = this.sequenceOf(BankTransactions)