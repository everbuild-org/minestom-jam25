package org.everbuild.celestia.orion.core.database.playerdata

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.Instant

interface OrionPlayer : Entity<OrionPlayer> {
    companion object : Entity.Factory<OrionPlayer>()
    val id: Int
    var uuid: String
    var name: String
    var firstLogin: Instant
    var lastLogin: Instant
    var debug: Boolean
    var teamLogin: Boolean
    var socialSpy: Boolean
    var commandSpy: Boolean
    var coins: Long
    var locale: String
}

object OrionPlayers : Table<OrionPlayer>("orion_player") {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").bindTo { it.uuid }
    val name = varchar("name").bindTo { it.name }
    val firstLogin = timestamp("first_login").bindTo { it.firstLogin }
    val lastLogin = timestamp("last_login").bindTo { it.lastLogin }
    val debug = boolean("debug").bindTo { it.debug }
    val teamLogin = boolean("team_login").bindTo { it.teamLogin }
    val socialSpy = boolean("social_spy").bindTo { it.socialSpy }
    val commandSpy = boolean("command_spy").bindTo { it.commandSpy }
    val coins = long("coins").bindTo { it.coins }
    val locale = varchar("locale").bindTo { it.locale }
}

val Database.orionPlayers get() = this.sequenceOf(OrionPlayers)