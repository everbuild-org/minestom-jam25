package org.everbuild.celestia.orion.platform.minestom.command

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.translation.SharedTranslations
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.util.orion
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated
import net.minestom.server.network.packet.client.play.ClientChangeGameModePacket
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission

object GamemodeCommand : Kommand("gamemode", "gm") {
    init {
        permission = "orion.command.gamemode"
        default { player, _ ->
            player.sendTranslated(SharedTranslations.GamemodeCommand.gameModeUsage)
        }
        val gameMode = Arg.enum<GameMode, GameMode>("gamemode")
        val gameModeNumber = Arg.int("gamemodenumber")
        val target = Arg.player("target")

        command {
            args += gameMode
            executes {
                player.gameMode = args[gameMode]
                sendOwnGameModeChange(player, args[gameMode])
            }
            requiresPermission("apollo.command.gamemode") {
                executes(target) {
                    val target = args[target]
                    target.gameMode = args[gameMode]
                    if (player == target) {
                        sendOwnGameModeChange(player, args[gameMode])
                        return@executes
                    }
                    sendTargetGameModeChange(player, args[gameMode], target)
                }
            }
        }
        command {
            args += gameModeNumber
            executes {
                val playerGameMode = GameMode.entries[args[gameModeNumber]]
                player.gameMode = playerGameMode
                sendOwnGameModeChange(player, playerGameMode)
            }
            requiresPermission("orion.command.gamemode.other") {
                executes(target) {
                    val target = args[target]
                    val targetGameMode = GameMode.entries[args[gameModeNumber]]
                    target.gameMode = targetGameMode
                    if (player == target) {
                        sendOwnGameModeChange(player, targetGameMode)
                        return@executes
                    }
                    sendTargetGameModeChange(player, targetGameMode, target)
                }
            }
        }

        MinecraftServer.getPacketListenerManager()
            .setPlayListener(ClientChangeGameModePacket::class.java) { packet, player ->
                if (!player.hasPermission("orion.command.gamemode")) return@setPlayListener
                player.gameMode = packet.gameMode
                sendOwnGameModeChange(player, packet.gameMode)
            }
    }

    private fun sendTargetGameModeChange(
        player: Player,
        targetGameMode: GameMode,
        target: Player
    ) {
        player.sendTranslated(
            when (targetGameMode) {
                GameMode.SURVIVAL -> SharedTranslations.GamemodeCommand.gamemodeSetOtherSurvivalSender
                GameMode.CREATIVE -> SharedTranslations.GamemodeCommand.gamemodeSetOtherCreativeSender
                GameMode.ADVENTURE -> SharedTranslations.GamemodeCommand.gamemodeSetOtherAdventureSender
                GameMode.SPECTATOR -> SharedTranslations.GamemodeCommand.gamemodeSetOtherSpectatorSender
            }
        ) {
            it.replace("player", target.orion)
        }
        target.sendTranslated(
            when (targetGameMode) {
                GameMode.SURVIVAL -> SharedTranslations.GamemodeCommand.gamemodeSetOtherSurvivalReceiver
                GameMode.CREATIVE -> SharedTranslations.GamemodeCommand.gamemodeSetOtherCreativeReceiver
                GameMode.ADVENTURE -> SharedTranslations.GamemodeCommand.gamemodeSetOtherAdventureReceiver
                GameMode.SPECTATOR -> SharedTranslations.GamemodeCommand.gamemodeSetOtherSpectatorReceiver
            }
        ) {
            it.replace("player", player.orion)
        }
    }

    private fun sendOwnGameModeChange(
        player: Player,
        playerGameMode: GameMode
    ) {
        player.sendTranslated(
            when (playerGameMode) {
                GameMode.SURVIVAL -> SharedTranslations.GamemodeCommand.gamemodeSetSelfSurvival
                GameMode.CREATIVE -> SharedTranslations.GamemodeCommand.gamemodeSetSelfCreative
                GameMode.ADVENTURE -> SharedTranslations.GamemodeCommand.gamemodeSetSelfAdventure
                GameMode.SPECTATOR -> SharedTranslations.GamemodeCommand.gamemodeSetSelfSpectator
            }
        )
    }
}