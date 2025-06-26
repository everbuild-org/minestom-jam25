package org.everbuild.celestia.orion.core.chat

import java.util.*
import kotlin.collections.ArrayDeque
import org.everbuild.celestia.orion.core.util.httpClient
import org.http4k.core.Method
import org.http4k.core.Request

class BufferedChat(private val hooks: PlatformChatHooks) {
    private val buffer = ArrayDeque<BufferedChatMessage>(100)
    private var lastId = 0

    fun send(message: ChatMessage, callback: () -> Unit = {}) {
        val bufferedChatMessage = BufferedChatMessage(message, lastId++)
        callback()
        buffer.addLast(bufferedChatMessage)
        if (buffer.size > 100) {
            buffer.removeFirst()
        }

        hooks.send(bufferedChatMessage)
    }

    fun registerJoin(uuid: UUID) {
        joinIds[uuid] = lastId - 1
    }

    fun delete(id: Int) {
        buffer.find { it.id == id }?.deleted = true
        resend()
    }

    fun message(id: Int): BufferedChatMessage? {
        return buffer.find { it.id == id }
    }

    fun restore(id: Int) {
        buffer.find { it.id == id }?.deleted = false
        resend()
    }

    fun clear() {
        buffer.forEach { it.deleted = true }
        resend()
    }

    private fun resend() {
        hooks.clear()
        buffer.forEach {
            hooks.send(it)
        }
    }

    fun save(startId: Int): String {
        val messages = buffer.filter { it.id >= startId && !it.deleted }.map {
            it.message.asText()
        }
        return hasteMessages(*messages.toTypedArray())
    }

    private fun hasteMessages(vararg messages: String): String {
        val result = httpClient(
            Request(Method.POST, "https://hastebin.cc/documents").body(messages.joinToString("\n"))
        ).bodyString()
        val key = regex.find(result)?.groupValues?.get(1) ?: return "Failed to upload messages"
        return "https://hastebin.cc/$key"
    }

    companion object {
        private val regex = """\{"key":"(.+?)"}""".toRegex()
        private val joinIds = mutableMapOf<UUID, Int>()

        fun shouldSendMessage(uuid: UUID, id: Int): Boolean {
            return (joinIds[uuid] ?: 0) < id
        }
    }
}