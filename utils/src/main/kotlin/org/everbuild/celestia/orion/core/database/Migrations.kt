package org.everbuild.celestia.orion.core.database

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface Migration : Entity<Migration> {
    companion object : Entity.Factory<Migration>()
    val id: Int
    var plugin: String
    var name: String
}

object Migrations : Table<Migration>("orion_migrations") {
    val id = int("id").primaryKey().bindTo { it.id }
    val plugin = varchar("plugin").bindTo { it.plugin }
    val name = varchar("name").bindTo { it.name }
}

val Database.migrations get() = this.sequenceOf(Migrations)