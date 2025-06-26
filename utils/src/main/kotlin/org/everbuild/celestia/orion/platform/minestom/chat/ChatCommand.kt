package org.everbuild.celestia.orion.platform.minestom.chat

import net.kyori.adventure.text.event.ClickEvent
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.globalServer
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission
import org.everbuild.celestia.orion.platform.minestom.util.orion
import org.everbuild.celestia.orion.platform.minestom.util.t

object ChatCommand : Kommand("chat") {
    init {
        val clear = Arg.literal("clear")
        val delete = Arg.literal("delete")
        val restore = Arg.literal("restore")
        val save = Arg.literal("save")
        val id = Arg.int("id")

        requiresPermission("orion.chat.clear") {
            executes(clear) {
                globalServer.chat.clear()
            }
        }

        requiresPermission("orion.chat.delete") {
            executes(delete, id) {
                globalServer.chat.message(args[id])?.let { msg ->
                    if (!msg.message.canDelete(player.orion, player.hasPermission("*"))) return@executes
                    globalServer.chat.delete(args[id])
                }

            }
        }

        requiresPermission("orion.chat.delete") {
            executes(restore, id) {
                globalServer.chat.restore(args[id])
            }
        }

        executes(save, id) {
            val href = globalServer.chat.save(args[id])
            player.sendMessage(player.t("orion.chat.link").c.clickEvent(ClickEvent.openUrl(href)))
        }
    }
}