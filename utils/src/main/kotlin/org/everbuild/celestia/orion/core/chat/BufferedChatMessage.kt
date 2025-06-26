package org.everbuild.celestia.orion.core.chat

data class BufferedChatMessage(
    val message: ChatMessage,
    val id: Int,
    var deleted: Boolean = false
)