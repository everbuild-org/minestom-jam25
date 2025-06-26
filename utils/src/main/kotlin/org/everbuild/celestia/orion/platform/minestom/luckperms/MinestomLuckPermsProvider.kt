package org.everbuild.celestia.orion.platform.minestom.luckperms

import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter
import me.lucko.luckperms.minestom.CommandRegistry
import me.lucko.luckperms.minestom.LuckPermsMinestom
import me.lucko.luckperms.minestom.context.ContextProvider
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.user.User
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent
import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.util.listen
import java.nio.file.Path
import java.util.*

object MinestomLuckPermsProvider {
    private val directory = Path.of("luckperms")
    lateinit var luckperms: LuckPerms
    private lateinit var orionServer: OrionServer

    var queryFn = fun(_: Player): Optional<String> {
        return Optional.empty()
    }

    fun load(orionServer: OrionServer) {
        this.orionServer = orionServer
        luckperms = LuckPermsMinestom.builder(directory)
            .commandRegistry(CommandRegistry.minestom())
            .contextProvider(object : ContextProvider {
                override fun key(): String = "orion-minestom"
                override fun query(subject: Player): Optional<String> = queryFn(subject)
            })
            .configurationAdapter {
                MultiConfigurationAdapter(
                    it,
                    EnvironmentVariableConfigAdapter(it),
                    HoconConfigurationAdapter(it)
                )
            }
            .dependencyManager(true)
            .enable()

        listen<PlayerSpawnEvent> {
            val player = it.player
            val user = luckperms.userManager.getUser(player.uuid) ?: return@listen
            user.auditTemporaryNodes()
            if (user.cachedData.permissionData.checkPermission("orion.command.gamemode").asBoolean()) {
                player.permissionLevel = 4
            }
        }
    }

    operator fun invoke(player: Player): User {
        return luckperms.userManager.getUser(player.uuid) ?: error("User not found")
    }
}

fun CommandSender.hasPermission(permission: String): Boolean {
    return when (this) {
        is Player -> MinestomLuckPermsProvider.luckperms.userManager.getUser(uuid)?.cachedData?.permissionData?.checkPermission(permission)?.asBoolean() ?: false
        is ConsoleSender -> true
        else -> false
    }
}