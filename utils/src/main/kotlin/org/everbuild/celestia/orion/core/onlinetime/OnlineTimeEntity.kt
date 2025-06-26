package org.everbuild.celestia.orion.core.onlinetime

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayers
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long

interface OnlineTimeEntity: Entity<OnlineTimeEntity>{
    companion object: Entity.Factory<OnlineTimeEntity>()

    var player: OrionPlayer
    var onlineTime: Long
}

object OnlineTimeTable : Table<OnlineTimeEntity>("babylon_onlinetime"){
    val playerId = int("player_id").primaryKey().references(OrionPlayers) { it.player}
    val onlineTime = long("onlinetime").bindTo { it.onlineTime }
}

val Database.onlineTime get() = this.sequenceOf(OnlineTimeTable)