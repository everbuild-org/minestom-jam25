package org.everbuild.celestia.orion.core.chatmanager.rules

import org.everbuild.celestia.orion.core.chatmanager.ChatRule

class CapsLockRule : ChatRule {
    override fun processMessage(message: String): String {
        return if (message == message.uppercase()) {
            message.lowercase()
        } else {
            message
        }
    }
}