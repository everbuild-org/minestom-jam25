package org.everbuild.celestia.orion.core.remote.schedulers

import org.everbuild.celestia.orion.core.platform.OrionPlatform
import org.everbuild.celestia.orion.core.redis.jedis
import org.everbuild.celestia.orion.core.util.serverName

fun getPlayerServerStateKey(player: Int, proxy: Boolean): String {
    return "remote:player:$player:${if (proxy) "proxy" else "server"}"
}

class RedisPlayerData(
    val player: Int,
    var proxy: String?,
    var server: String?
) {
   constructor(player: Int) : this(player, null, null)
}

fun broadcastPlayerState(platform: OrionPlatform) {
    val isProxy = platform.isProxy()
    platform.getPlayers().forEach {
        jedis.setex(getPlayerServerStateKey(it.id, isProxy), 4, serverName)
    }
}