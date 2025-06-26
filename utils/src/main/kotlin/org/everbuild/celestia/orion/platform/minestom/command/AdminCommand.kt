package org.everbuild.celestia.orion.platform.minestom.command

import net.kyori.adventure.text.format.NamedTextColor
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.remote.RemotePlayerPool
import org.everbuild.celestia.orion.core.remote.RemoteServerPool
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
        requiresPermission("orion.command.admin.prism") {
            command {
                args += Arg.literal("prism")
                executes(Arg.literal("servers")) {
                    RemoteServerPool.all().forEach {
                        player.sendMessage("${it.name} (${if (it.isProxy) "Proxy" else "Server"}): ${it.logicalMaxPlayerCount} players, ${if (it.schedulable) "schedulable" else "not schedulable"}")
                    }
                }

                executes(Arg.literal("players")) {
                    RemotePlayerPool.all().forEach {
                        val orionPlayer = PlayerLoader.load(it.id) ?: return@forEach
                        player.sendMessage("${orionPlayer.name} (${it.id}) is on ${it.server} (proxy=${it.proxy})")
                    }
                }

                executes(Arg.literal("send"), playerArg, messageArg) {
                    RemotePlayerPool.get(args[playerArg].orion.id).sendMessage(args[messageArg].component())
                }

                executes(Arg.literal("impersonate-send"), playerArg, messageArg) {
                    RemotePlayerPool.get(args[playerArg].orion.id).sendMessageAs(args[messageArg].component())
                }
            }
        }

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