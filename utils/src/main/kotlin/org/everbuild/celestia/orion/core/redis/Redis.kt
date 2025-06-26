package org.everbuild.celestia.orion.core.redis

import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.JedisPubSub
import redis.clients.jedis.UnifiedJedis
import redis.clients.jedis.params.ScanParams

val jedis = JedisPooled(SharedPropertyConfig.redisHost)
val jedisSubscriber = JedisPooled(SharedPropertyConfig.redisHost)

fun UnifiedJedis.scanAll(pattern: String): List<String> {
    val keys = mutableListOf<String>()
    val params = ScanParams()
        .count(1000)
        .match(pattern)
    var cursor = "0"
    do {
        val scanResult = this.scan(cursor, params)
        keys.addAll(scanResult.result)
        cursor = scanResult.cursor
    } while (cursor != "0")
    return keys
}

fun UnifiedJedis.subscribeRetry(subscriber: JedisPubSub, vararg channels: String) {
    while (true) {
        try {
            this.subscribe(subscriber, *channels)
            break
        } catch (_: Exception) {
            try {
                Thread.sleep(100)
            } catch (_: InterruptedException) {
            }
        }
    }
}