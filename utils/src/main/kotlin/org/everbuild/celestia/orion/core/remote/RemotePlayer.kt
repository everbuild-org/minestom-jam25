package org.everbuild.celestia.orion.core.remote

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.redis.jedis
import org.everbuild.celestia.orion.core.util.FullPosition
import org.everbuild.celestia.orion.core.util.Vec3d
import org.everbuild.celestia.orion.core.util.Vec3f
import org.everbuild.celestia.orion.core.util.Vec3i
import org.everbuild.celestia.orion.core.util.toNulledFull

data class RemotePlayer(
    val id: Int,
    var proxy: String?,
    var server: String?
) {
    fun stillExists(): Boolean {
        return RemotePlayerPool.exists(id)
    }

    fun proxy(): RemoteServer {
        return RemoteServerPool.get(proxy!!)!!
    }

    fun server(): RemoteServer {
        return RemoteServerPool.get(server!!)!!
    }

    fun sendMessage(message: Component) {
        jedis.publish("remote:player:$id:msg", GsonComponentSerializer.gson().serialize(message))
    }

    fun sendMessageAs(message: Component) {
        jedis.publish("remote:player:$id:impersonateMsg", GsonComponentSerializer.gson().serialize(message))
    }

    fun runCommand(command: String) {
        jedis.publish("remote:player:$id:gameImpersonateCmd", command)
    }

    fun runProxyCommand(command: String) {
        jedis.publish("remote:player:$id:proxyImpersonateCmd", command)
    }

    fun sendToServer(server: RemoteServer) {
        jedis.publish("remote:player:$id:send", server.name)
    }

    fun teleport(orionPlayer: OrionPlayer) {
        jedis.setex("remote:player:$id:server:${orionPlayer.asRemote().server}:teleport", 30, orionPlayer.id.toString())
        sendToServer(orionPlayer.asRemote().server())
    }

    fun teleport(server: RemoteServer, position: FullPosition) {
        jedis.setex("remote:player:$id:server:$server:teleport", 30, "${position.x} ${position.y} ${position.z} ${position.x} ${position.yaw} ${position.pitch}")
        sendToServer(server)
    }

    fun teleport(server: RemoteServer, position: Vec3i) {
        teleport(server, position.toNulledFull())
    }

    fun teleport(server: RemoteServer, position: Vec3d) {
        teleport(server, position.toNulledFull())
    }

    fun teleport(server: RemoteServer, position: Vec3f) {
        teleport(server, position.toNulledFull())
    }
}

object RemotePlayerPool {
    private val players = mutableMapOf<Int, RemotePlayer>()

    fun <T> refreshOnly(servers: Map<Int, T>, mapper: (T, RemotePlayer) -> RemotePlayer) {
        // Remove servers that are not in the list
        players.keys.removeAll { it !in servers }

        // Update servers
        servers.forEach { (name, data) ->
            if (players.containsKey(name)) {
                players[name] = mapper(data, players[name]!!)
            } else {
                players[name] = mapper(data, RemotePlayer(name, "", ""))
            }
        }
    }

    fun exists(name: Int): Boolean {
        return players.containsKey(name)
    }

    fun filter(function: (RemotePlayer) -> Boolean): List<RemotePlayer> {
        return players.values.filter(function)
    }

    fun all(): List<RemotePlayer> {
        return players.values.toList()
    }

    fun get(name: Int): RemotePlayer {
        return players[name]!!
    }
}

fun OrionPlayer.asRemote(): RemotePlayer {
    return RemotePlayerPool.get(this.id)
}