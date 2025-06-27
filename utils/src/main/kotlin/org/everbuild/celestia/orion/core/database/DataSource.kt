package org.everbuild.celestia.orion.core.database

import org.ktorm.database.Database

object DataSource {
    val database = Database.connect("jdbc:sqlite:orion.db").also { db ->
        db.useConnection {
            it.prepareStatement("pragma journal_mode=wal")
        }
    }
}

val database get() = DataSource.database