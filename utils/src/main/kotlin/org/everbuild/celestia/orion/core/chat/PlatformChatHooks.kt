package org.everbuild.celestia.orion.core.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.c
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.component

interface PlatformChatHooks {
    fun send(message: BufferedChatMessage)
    fun clear()

    fun getDeletionPrefix(): Component =
        Component.text("[").color(NamedTextColor.DARK_GRAY)
            .append(Component.text(OrionPacks.getCharacterCodepoint("icon_trash")).color(NamedTextColor.RED))
            .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))

    fun getChatTools(canDelete: Boolean, message: BufferedChatMessage, orionPlayer: OrionPlayer): Component =
        Component.text("[").color(NamedTextColor.DARK_GRAY).let {
            if (canDelete) {
                if (!message.deleted) {
                    it.append(
                        OrionPacks.getCharacterCodepoint("icon_trash")
                            .component()
                            .color(NamedTextColor.GRAY)
                            .hoverEvent(HoverEvent.showText(orionPlayer.c("orion.chat.delete")))
                            .clickEvent(ClickEvent.runCommand("/chat delete ${message.id}"))
                    )
                } else {

                    it.append(
                        OrionPacks.getCharacterCodepoint("icon_recycle")
                            .component()
                            .color(NamedTextColor.GRAY)
                            .hoverEvent(HoverEvent.showText(orionPlayer.c("orion.chat.restore")))
                            .clickEvent(ClickEvent.runCommand("/chat restore ${message.id}"))
                    )
                }
                    .append(" ".component())
            } else it
        }
            .append(
                OrionPacks.getCharacterCodepoint("icon_copy")
                    .component()
                    .color(NamedTextColor.GRAY)
                    .hoverEvent(HoverEvent.showText(orionPlayer.c("orion.chat.copy")))
                    .clickEvent(ClickEvent.suggestCommand(message.message.asText()))
            )
            .append(" ".component())
            .append(
                OrionPacks.getCharacterCodepoint("icon_floppy")
                    .component()
                    .color(NamedTextColor.GRAY)
                    .hoverEvent(HoverEvent.showText(orionPlayer.c("orion.chat.save")))
                    .clickEvent(ClickEvent.runCommand("/chat save ${message.id}"))
            )
            .append(Component.text("] ").color(NamedTextColor.DARK_GRAY))
}