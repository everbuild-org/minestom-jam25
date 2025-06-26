package org.everbuild.celestia.orion.core.remote

import org.everbuild.celestia.orion.core.redis.jedis

data class RemoteServer(
    val name: String,
    var isProxy: Boolean,
    var logicalMaxPlayerCount: Long,
    var schedulable: Boolean,
    var players: Int,
) {
    fun stillExists(): Boolean {
        return RemoteServerPool.exists(name)
    }

    fun runCommand(command: String) {
        jedis.publish("remote:server:$name:command", command)
    }

    fun players(): List<RemotePlayer> {
        return RemotePlayerPool.filter {
            if (isProxy) {
                it.proxy == name
            } else {
                it.server == name
            }
        }
    }
}

object RemoteServerPool {
    private val servers = mutableMapOf<String, RemoteServer>()

    fun <T> refreshOnly(servers: Map<String, T>, mapper: (T, RemoteServer) -> RemoteServer) {
        // Remove servers that are not in the list
        this.servers.keys.removeAll { it !in servers }

        // Update servers
        servers.forEach { (name, data) ->
            if (this.servers.containsKey(name)) {
                this.servers[name] = mapper(data, this.servers[name]!!)
            } else {
                this.servers[name] = mapper(data, RemoteServer(name, false, 0, false, 0))
            }
        }
    }

    fun exists(name: String): Boolean {
        return servers.containsKey(name)
    }

    fun get(name: String): RemoteServer? {
        return servers[name]
    }

    fun all(): List<RemoteServer> {
        return servers.values.toList()
    }
}