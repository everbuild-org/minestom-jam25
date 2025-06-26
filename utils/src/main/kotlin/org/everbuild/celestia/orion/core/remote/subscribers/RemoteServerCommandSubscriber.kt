package org.everbuild.celestia.orion.core.remote.subscribers

import org.everbuild.celestia.orion.core.util.globalOrion
import redis.clients.jedis.JedisPubSub

class RemoteServerCommandSubscriber : JedisPubSub() {
    override fun onMessage(channel: String, message: String) {
        globalOrion.platform.executeServerCommand(message)
    }
}