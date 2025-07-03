package org.everbuild.celestia.orion.platform.minestom.luckperms

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.luckperms.LuckPermsData
import org.everbuild.celestia.orion.core.packs.withEmojis

data class MinestomLuckPermsData(private val player: Player) : LuckPermsData {
    private val api = MinestomLuckPermsProvider.luckperms
    private val user = api.userManager.getUser(player.uuid)
    private val primaryGroup = api.groupManager.getGroup(user!!.primaryGroup)

    override val permissionsPrefix: String = (primaryGroup!!.cachedData.metaData.prefix ?: "§3Devmode §7| §3").withEmojis()
    override val permissionsColor: String = primaryGroup!!.cachedData.metaData.suffix ?: "§3"
    override val permissionsDisplayName: String = primaryGroup!!.displayName ?: "§3Devmode"
    override val permissionsWeight: Int = if (primaryGroup!!.weight.isPresent) primaryGroup.weight.getAsInt() else 0
    override val permissionsGroupNameRaw: String = user!!.primaryGroup
    override val playerName: String = player.username
    override val isChatImportant: Boolean = player.hasPermission("orion.chat.important")
}

fun Player.lp(): MinestomLuckPermsData = MinestomLuckPermsData(this)