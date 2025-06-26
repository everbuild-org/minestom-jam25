package org.everbuild.celestia.orion.core.remote.subscribers

import org.everbuild.celestia.orion.core.remote.RemotePlatform
import redis.clients.jedis.JedisPubSub

class RemoteUpdateSubscriber : JedisPubSub() {
    override fun onMessage(channel: String, message: String) {
        RemotePlatform.refreshServersAndClients()
    }
}