package org.everbuild.celestia.orion.core.database.playerdata

import org.everbuild.celestia.orion.core.database.database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.firstOrNull
import java.util.*
import kotlin.ConcurrentModificationException

object PlayerLoader {
    private val cache = mutableSetOf<OrionPlayer>()

    fun load(id: Int): OrionPlayer? {
        return cache.firstOrNull { it.id == id } ?: loadNew(id)
    }

    fun load(uuid: UUID): OrionPlayer? {
        return cache.firstOrNull { it.uuid == uuid.toString() } ?: loadNew(uuid)
    }

    fun load(name: String): OrionPlayer? {
        return cache.firstOrNull { it.name == name } ?: loadNew(name)
    }

    private fun loadNew(id: Int): OrionPlayer? {
        return database.orionPlayers.firstOrNull { it.id eq id }?.also{ storeInCache(it) }
    }

    internal fun loadNew(uuid: UUID): OrionPlayer? {
        return database.orionPlayers.firstOrNull { it.uuid eq uuid.toString() }?.also{ storeInCache(it) }
    }

    private fun loadNew(name: String): OrionPlayer? {
        return database.orionPlayers.firstOrNull { it.name eq name }?.also{ storeInCache(it) }
    }

    private fun storeInCache(player: OrionPlayer?) {
        if (player == null) return
        cache.removeIf { it.id == player.id }
        cache.add(player)
    }

    fun reload(player: OrionPlayer): OrionPlayer {
        cache.remove(player)
        return loadNew(player.id) ?: throw ConcurrentModificationException("User was removed from the database while online")
    }
}