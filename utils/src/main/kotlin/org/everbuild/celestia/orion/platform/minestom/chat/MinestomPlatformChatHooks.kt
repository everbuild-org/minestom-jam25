package org.everbuild.celestia.orion.platform.minestom.chat

import net.kyori.adventure.text.Component
import org.everbuild.celestia.orion.core.chat.BufferedChat
import org.everbuild.celestia.orion.core.chat.BufferedChatMessage
import org.everbuild.celestia.orion.core.chat.PlatformChatHooks
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission
import org.everbuild.celestia.orion.platform.minestom.util.orion

class MinestomPlatformChatHooks : PlatformChatHooks {
    override fun send(message: BufferedChatMessage) {
        for (player in Mc.connection.onlinePlayers) {
            if (
                message.deleted &&
                !player.hasPermission("orion.chat.delete")
            ) continue

            if (!BufferedChat.shouldSendMessage(player.uuid, message.id)) continue

            val canDelete = message.message.canDelete(player.orion, player.hasPermission("orion.chat.delete.all"))
            val prefix = if (message.deleted) getDeletionPrefix() else Component.empty()
            val suffix = getChatTools(canDelete, message, player.orion)
            message.message.sendTo(player.orion, prefix, suffix)
        }
    }

    override fun clear() {
        Mc.connection.onlinePlayers.forEach { player ->
            player.sendMessage(Component.text("\n".repeat(100)))
        }
    }
}