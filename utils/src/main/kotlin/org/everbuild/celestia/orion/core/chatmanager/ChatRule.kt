package org.everbuild.celestia.orion.core.chatmanager

interface ChatRule {
    fun processMessage(message: String): String?
}