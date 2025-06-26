package org.everbuild.celestia.orion.core.remote.schedulers

import org.everbuild.celestia.orion.core.platform.OrionPlatform
import org.everbuild.celestia.orion.core.redis.jedis
import org.everbuild.celestia.orion.core.util.serverName
import redis.clients.jedis.json.JsonSetParams

data class RedisServerInformation(
    val name: String,
    val players: Int,
    val logicalMaxPlayerCount: Long,
    val schedulable: Boolean,
    val proxy: Boolean,
) {
    constructor(platform: OrionPlatform) : this(
        serverName,
        platform.getPlayers().size,
        platform.getLogicalMaxPlayerCount(),
        platform.isReady(),
        platform.isProxy()
    )

    constructor(redisValue: String) : this(
        redisValue.split(":")[0],
        redisValue.split(":")[1].toInt(),
        redisValue.split(":")[2].toLong(),
        redisValue.split(":")[3].toBoolean(),
        redisValue.split(":")[4].toBoolean()
    )

    fun toRedisKey(): String {
        return "remote:server:$name"
    }

    fun toRedisValue(): String {
        return "$name:$players:$logicalMaxPlayerCount:$schedulable:$proxy"
    }
}

fun broadcastServerState(platform: OrionPlatform) {
    val local = RedisServerInformation(platform)
    jedis.setex(local.toRedisKey(), 4, local.toRedisValue())
}