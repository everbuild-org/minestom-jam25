package org.everbuild.celestia.orion.core.chatmanager

import org.everbuild.celestia.orion.core.chatmanager.rules.BadWordRule
import org.everbuild.celestia.orion.core.chatmanager.rules.CapsLockRule

val processors = listOf(
    CapsLockRule(),
    BadWordRule()
)

fun processMessage(message: String): String? {
    var processedMessage = message
    for (processor in processors) {
        processedMessage = processor.processMessage(processedMessage) ?: return null
    }

    if (message != processedMessage) {
    }
    return processedMessage
}