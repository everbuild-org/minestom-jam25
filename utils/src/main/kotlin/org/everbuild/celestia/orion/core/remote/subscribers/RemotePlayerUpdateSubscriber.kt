package org.everbuild.celestia.orion.core.remote.subscribers

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.sendMessage
import org.everbuild.celestia.orion.core.util.globalOrion
import redis.clients.jedis.JedisPubSub

class RemotePlayerUpdateSubscriber(val player: OrionPlayer) : JedisPubSub() {
    override fun onMessage(channel: String, message: String) {
        if (channel.endsWith("msg")) {
            player.sendMessage(
                GsonComponentSerializer
                    .gson()
                    .deserialize(message)
            )
        } else if (channel.endsWith("impersonateMsg")) {
            globalOrion.platform.sendMessageAs(player, GsonComponentSerializer.gson().deserialize(message))
        } else if (channel.endsWith("ImpersonateCmd")) {
            globalOrion.platform.executeCommandAs(player, message)
        } else if (channel.endsWith("send")) {
            globalOrion.platform.sendTo(player, message)
        }
    }
}