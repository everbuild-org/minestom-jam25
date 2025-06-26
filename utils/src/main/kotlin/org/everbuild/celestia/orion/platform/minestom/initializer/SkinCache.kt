package org.everbuild.celestia.orion.platform.minestom.initializer

import kotlin.coroutines.Continuation
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.item.component.HeadProfile
import org.everbuild.celestia.orion.core.OrionCore
import org.everbuild.celestia.orion.platform.minestom.api.utils.logger

object SkinCache {
    private val cache = mutableMapOf<String, PlayerSkin?>()

    fun getSkin(username: String): PlayerSkin? {
        if (!cache.containsKey(username))
            loadSkin(username)
        return cache[username]
    }

    fun setSkin(username: String, skin: PlayerSkin) {
        cache[username] = skin
    }

    fun isOnFile(username: String): Boolean {
        return cache.containsKey(username)
    }

    fun loadSkin(username: String): PlayerSkin? {
        return cache.getOrPut(username) {
            for (i in 0 until 5) {
                try {
                    return@getOrPut PlayerSkin.fromUuid(uuidByUsername(username))
                } catch (e: Exception) {
                    logger.warn("Failed to load player uuid for $username", e)
                }
            }

            return@getOrPut null
        }
    }

    fun lazy(playerHeadPlayerName: String): Deferred<PlayerSkin?> = OrionCore.scope.async {
        getSkin(playerHeadPlayerName)
    }
}

fun PlayerSkin?.asHeadProfile(): HeadProfile {
    if (this == null) return HeadProfile.EMPTY
    return HeadProfile(this)
}