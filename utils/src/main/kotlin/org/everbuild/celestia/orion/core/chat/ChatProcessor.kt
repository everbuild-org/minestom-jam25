package org.everbuild.celestia.orion.core.chat

import net.kyori.adventure.text.Component

object ChatProcessor {
    fun process(theMessage: String, canUseFormatCodes: Boolean): Component {
        if (!canUseFormatCodes) {
            return Component.text(theMessage)
        }

        var message = theMessage.replace("&l", "§l")
        message = message.replace("&n", "§n")
        message = message.replace("&o", "§o")
        message = message.replace("&k", "§k")
        message = message.replace("&m", "§m")
        message = message.replace("&0", "§0")
        message = message.replace("&1", "§1")
        message = message.replace("&2", "§2")
        message = message.replace("&3", "§3")
        message = message.replace("&4", "§4")
        message = message.replace("&5", "§5")
        message = message.replace("&6", "§6")
        message = message.replace("&7", "§7")
        message = message.replace("&8", "§8")
        message = message.replace("&9", "§9")
        message = message.replace("&a", "§a")
        message = message.replace("&b", "§b")
        message = message.replace("&c", "§c")
        message = message.replace("&d", "§d")
        message = message.replace("&e", "§e")
        message = message.replace("&f", "§f")
        message = message.replace("&r", "§r")

        return Component.text(message)
    }
}