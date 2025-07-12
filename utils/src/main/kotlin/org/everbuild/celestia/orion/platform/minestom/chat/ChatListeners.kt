package org.everbuild.celestia.orion.platform.minestom.chat

import net.minestom.server.event.EventNode
import net.minestom.server.event.player.*
import org.everbuild.celestia.orion.core.chat.BufferedChat
import org.everbuild.celestia.orion.core.chat.ChatMessage
import org.everbuild.celestia.orion.core.chat.ChatProcessor
import org.everbuild.celestia.orion.core.chatmanager.processMessage
import org.everbuild.celestia.orion.core.luckperms.LuckPermsSnapshot
import org.everbuild.celestia.orion.core.luckperms.takeLuckPermsSnapshot
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission
import org.everbuild.celestia.orion.platform.minestom.util.later
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.celestia.orion.platform.minestom.util.orion
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

fun registerChatListeners(chat: BufferedChat) {
    val cachedTex = mutableMapOf<UUID, LuckPermsSnapshot>()

    Mc.globalEvent.addChild(EventNode.all("orion-chat-listeners").also { node ->
        node.setPriority(1000)

        node.listen<PlayerChatEvent, _> { event ->
            if (event.isCancelled) return@listen

            event.isCancelled = true
            val message = processMessage(event.rawMessage)
            if (message == null) {
                event.player.sendTranslated("orion.chat.notallowed")
                return@listen
            }
            val processed = ChatProcessor.process(message, event.player.hasPermission("orion.chat.format"))
            chat.send(ChatMessage.text(event.player.orion, processed, message))
        }
    })

    listen<PlayerSpawnEvent> { event ->
        if (!event.isFirstSpawn) {
            return@listen
        }

        0.25.seconds later {
            chat.registerJoin(event.player.uuid)
            chat.send(ChatMessage.join(event.player.orion, null))
            cachedTex[event.player.uuid] = event.player.orion.takeLuckPermsSnapshot()
        }
    }
}