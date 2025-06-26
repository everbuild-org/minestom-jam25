package org.everbuild.celestia.orion.core.remote

import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.platform.OrionPlatform
import org.everbuild.celestia.orion.core.redis.jedis
import org.everbuild.celestia.orion.core.redis.jedisSubscriber
import org.everbuild.celestia.orion.core.redis.scanAll
import org.everbuild.celestia.orion.core.redis.subscribeRetry
import org.everbuild.celestia.orion.core.remote.schedulers.BroadcastingScheduler
import org.everbuild.celestia.orion.core.remote.schedulers.RedisPlayerData
import org.everbuild.celestia.orion.core.remote.schedulers.RedisServerInformation
import org.everbuild.celestia.orion.core.remote.schedulers.RequeryScheduler
import org.everbuild.celestia.orion.core.remote.subscribers.RemotePlayerUpdateSubscriber
import org.everbuild.celestia.orion.core.remote.subscribers.RemoteServerCommandSubscriber
import org.everbuild.celestia.orion.core.remote.subscribers.RemoteUpdateSubscriber
import org.everbuild.celestia.orion.core.util.FullPosition
import org.everbuild.celestia.orion.core.util.globalOrion
import org.everbuild.celestia.orion.core.util.greenThread
import org.everbuild.celestia.orion.core.util.serverName

object RemotePlatform {
    private val playerListeners = mutableMapOf<Int, RemotePlayerListenerMeta>()

    fun listenAndSchedule(platform: OrionPlatform) {
        greenThread {
            jedisSubscriber.subscribeRetry(RemoteUpdateSubscriber(), "remote:updates")
        }

        greenThread {
            jedisSubscriber.subscribeRetry(RemoteServerCommandSubscriber(), "remote:server:$serverName:command")
        }

        BroadcastingScheduler(platform).start()
        RequeryScheduler().start()

        jedis.publish("remote:updates", "refresh")
    }

    fun playerJoined(player: OrionPlayer) {
        val listener = RemotePlayerUpdateSubscriber(player)
        val channels = mutableListOf(
            "remote:player:${player.id}:msg",
            "remote:player:${player.id}:impersonateMsg",
        )

        if (globalOrion.platform.isProxy()) {
            channels += "remote:player:${player.id}:proxyImpersonateCmd"
            channels += "remote:player:${player.id}:send"
        } else {
            channels += "remote:player:${player.id}:gameImpersonateCmd"
        }

        val thread = greenThread {
            jedisSubscriber.subscribeRetry(
                listener,
                *channels.toTypedArray()
            )
        }

        playerListeners[player.id] = RemotePlayerListenerMeta(listener, channels, thread)
        jedis.publish("remote:updates", "refresh")

        val pos = jedis.get("remote:player:${player.id}:server:${serverName}:teleport")
        if (pos == null) {
            return
        }

        val factory = globalOrion.platform.teleportationFactory()
        val position = pos.split(" ").map { it.toDoubleOrNull() }
        val teleportation = if (position.none { it == null }) {
            factory.to(
                if (position.size == 3) {
                    FullPosition(position[0]!!, position[1]!!, position[2]!!, 0f, 0f)
                } else {
                    FullPosition(position[0]!!, position[1]!!, position[2]!!, position[3]!!.toFloat(), position[4]!!.toFloat())
                }
            )
        } else {
            factory.to(
                PlayerLoader.load(pos.toIntOrNull() ?: return) ?: return
            )
        }

        globalOrion.platform.teleport(player, teleportation)
    }

    fun playerLeft(player: OrionPlayer) {
        val listener = playerListeners[player.id] ?: return
        listener.listener.unsubscribe(*listener.channels.toTypedArray())
        listener.thread.interrupt()
        playerListeners.remove(player.id)
        jedis.publish("remote:updates", "refresh")
    }

    fun refreshServersAndClients() {
        val servers = mutableMapOf<String, RedisServerInformation>()
        for (key in jedis.scanAll("remote:server:*")) {
            val value = jedis.get(key)
            try {
                val info = RedisServerInformation(value)
                servers[info.name] = info
            } catch (e: Exception) {
                jedis.del(key)
                continue
            }
        }

        RemoteServerPool.refreshOnly(servers) { info, server ->
            server.isProxy = info.proxy
            server.logicalMaxPlayerCount = info.logicalMaxPlayerCount
            server.schedulable = info.schedulable
            server.players = info.players
            server
        }

        val players = mutableMapOf<Int, RedisPlayerData>()
        for (key in jedis.scanAll("remote:player:*")) {
            val player = key.split(":")[2].toInt()
            val value = jedis.get(key)
            val entity = players.getOrPut(player) { RedisPlayerData(player) }

            if (key.endsWith(":server")) {
                entity.server = value
            } else if (key.endsWith(":proxy")) {
                entity.proxy = value
            }
        }

        RemotePlayerPool.refreshOnly(players) { info, player ->
            player.proxy = info.proxy
            player.server = info.server
            player
        }
    }

    data class RemotePlayerListenerMeta(
        val listener: RemotePlayerUpdateSubscriber,
        val channels: List<String>,
        val thread: Thread
    )
}