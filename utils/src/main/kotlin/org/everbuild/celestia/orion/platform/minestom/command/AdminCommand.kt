package org.everbuild.celestia.orion.platform.minestom.command

import net.kyori.adventure.text.format.NamedTextColor
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.translation.Translator
import org.everbuild.celestia.orion.core.util.component
import org.everbuild.celestia.orion.core.util.globalOrion
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.command.debug.DebugCommand
import org.everbuild.celestia.orion.platform.minestom.pack.ResourcesReloadEvent
import org.everbuild.celestia.orion.platform.minestom.util.orion
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated

object AdminCommand : Kommand("asordactl") {
    init {
        permission = "orion.command.admin"
        setDefaultExecutor { sender, _ ->
            sender.sendMessage("Usage: /admin <subcommand>")
        }

        val keyArg = Arg.string("key")
        val playerArg = Arg.player("player")
        val messageArg = Arg.string("message")
        addSubcommand(DebugCommand)

        requiresPermission("orion.i18n.reload") {
            command {
                args += Arg.literal("translate")
                executes(Arg.literal("reload")) {
                    Translator.reloadTranslations()
                    player.sendMessage("[OK]".component().color(NamedTextColor.GREEN))
                }

                executes(Arg.literal("test"), keyArg) {
                    player.sendTranslated(args[keyArg])
                }

                executes(Arg.literal("texture"), playerArg) {
                    player.sendMessage("     ".component().append(globalOrion.chatTextureResolver.getSkin(args[playerArg].orion)?.chatText ?: "".component()))
                }
            }
        }

        requiresPermission("orion.pack.reload") {
            command {
                args += Arg.literal("resourcepack")
                executes(Arg.literal("reload")) {
                    player.sendMessage("Reloading resource pack...")
                    OrionPacks.refreshResourcePack()
                    Mc.globalEvent.call(ResourcesReloadEvent())
                    player.sendMessage("[OK]".component().color(NamedTextColor.GREEN))
                }
            }
        }
    }
}