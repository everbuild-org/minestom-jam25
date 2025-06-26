package org.everbuild.celestia.orion.core.database.coins

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayers
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface CoinTransaction: Entity<CoinTransaction> {
    companion object : Entity.Factory<CoinTransaction>()
    val id: Int
    var player: OrionPlayer
    var transactionPartner: OrionPlayer?
    var amount: Long
    var reason: String
}

object CoinTransactions: Table<CoinTransaction>("orion_coin_transaction") {
    val id = int("id").primaryKey().bindTo { it.id }
    val player = int("player").references(OrionPlayers) { it.player }
    val transactionPartner = int("transaction_partner").references(OrionPlayers) { it.transactionPartner }
    val amount = long("amount").bindTo { it.amount }
    val reason = varchar("reason").bindTo { it.reason }
}

val Database.coinTransactions get() = this.sequenceOf(CoinTransactions)