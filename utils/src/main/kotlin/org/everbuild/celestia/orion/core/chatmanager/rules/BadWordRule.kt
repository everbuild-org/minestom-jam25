package org.everbuild.celestia.orion.core.chatmanager.rules

import org.everbuild.celestia.orion.core.chatmanager.BadWordConfig
import org.everbuild.celestia.orion.core.chatmanager.ChatRule

class BadWordRule : ChatRule {
    val badWords = BadWordConfig.badWords.split("|")
    val goodWords = BadWordConfig.goodWords.split("|")

    override fun processMessage(message: String): String? {
        val replacedMessage = badWords.fold(message) { acc, badWord ->
            val randomGoodWord = goodWords.random()
            acc.replace(Regex("(?i)\\b$badWord\\b"), randomGoodWord)
        }
        if (message != replacedMessage) {
        }
        return replacedMessage
    }
}