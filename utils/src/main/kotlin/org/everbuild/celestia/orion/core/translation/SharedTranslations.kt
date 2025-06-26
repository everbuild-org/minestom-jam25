package org.everbuild.celestia.orion.core.translation

object SharedTranslations {
    val playerNotFound = "orion.no-player-found"
    val noPermissions = "orion.no-perms"
    val notEnoughCoins = "orion.not-enough-coins"
    val notEnoughCoinsOther = "orion.not-enough-coins-other"

    object InvalidFormat {
        val number = "orion.invalid-format.number"
    }

    object GamemodeCommand {
        val gameModeUsage = "orion.command.gamemode.usage"
        val gamemodeSetSelfSurvival = "orion.command.gamemode.survival.set.self" // You set your gamemode to survival
        val gamemodeSetSelfCreative = "orion.command.gamemode.creative.set.self"    // You set your gamemode to creative
        val gamemodeSetSelfAdventure = "orion.command.gamemode.adventure.set.self"  // You set your gamemode to adventure
        val gamemodeSetSelfSpectator = "orion.command.gamemode.spectator.set.self"  // You set your gamemode to spectator
        val gamemodeSetOtherSurvivalSender = "orion.command.gamemode.creative.set.other.sender"  // You set %s's gamemode to survival
        val gamemodeSetOtherCreativeSender = "orion.command.gamemode.creative.set.other.sender"  // You set %s's gamemode to creative
        val gamemodeSetOtherAdventureSender = "orion.command.gamemode.adventure.set.other.sender"    // You set %s's gamemode to adventure
        val gamemodeSetOtherSpectatorSender = "orion.command.gamemode.spectator.set.other.sender"    // You set %s's gamemode to spectator
        val gamemodeSetOtherSurvivalReceiver = "orion.command.gamemode.survival.set.other.receiver"  // %s set your gamemode to survival
        val gamemodeSetOtherCreativeReceiver = "orion.command.gamemode.creative.set.other.receiver"  // %s set your gamemode to creative
        val gamemodeSetOtherAdventureReceiver = "orion.command.gamemode.adventure.set.other.receiver"    // %s set your gamemode to adventure
        val gamemodeSetOtherSpectatorReceiver = "orion.command.gamemode.spectator.set.other.receiver"    // %s set your gamemode to spectator
    }

    object FlyCommand {
        val flyUsage = "orion.commandfly.usage"
        val flyEnabledSelf= "orion.command.fly.set.on.self"  // You enabled fly mode
        val flyDisabledSelf = "orion.command.fly.set.off.self"    // You disabled fly mode
        val flyEnalbedOtherReceiver = "orion.command.fly.set.on.receiver"  // You have been set to fly mode
        val flyDisabledOtherReceiver = "orion.command.fly.set.off.receiver"    // You have been set out of fly mode
        val flyEnabledOtherSender = "orion.command.fly.set.on.sender"  // You set %s to fly mode
        val flyDisabledOtherSender = "orion.command.fly.set.off.sender"    // You set %s out of fly mode
    }

    object Scoreboard{
        val onlineTime = "orion.scoreboard.onlinetime"
        val plot = "orion.scoreboard.plot"
        val ping = "orion.scoreboard.ping"
        val coins = "orion.scoreboard.coins"
        val world = "orion.scoreboard.world"
        val teamspeak = "orion.scoreboard.teamspeak"
        val teamspeakValue = "orion.scoreboard.teamspeak.value"
        val website = "orion.scoreboard.website"
        val websiteValue = "orion.scoreboard.website.value"
        val server = "orion.scoreboard.server"
        val rank = "orion.scoreboard.rank"
    }

}