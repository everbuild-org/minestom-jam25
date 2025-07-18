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
import java.io.File
import java.nio.file.Path
import java.util.*

object MinestomLuckPermsProvider {
    private val directory = Path.of("luckperms")
    lateinit var luckperms: LuckPerms
    private lateinit var orionServer: OrionServer

    var queryFn = fun(_: Player): Optional<String> {
        return Optional.empty()
    }

    private fun extract(from: String, to: String) {
        val file = File(directory.toFile(), to)
        file.parentFile.mkdirs()
        MinestomLuckPermsProvider.javaClass.getResourceAsStream(from)!!.copyTo(file.outputStream())
    }

    private fun addAdminUser(name: String, uuid: String) {
        val prototype = """
            name=$name
            parents=[
                {
                    group=dev
                }
            ]
            permissions=[
                {
                    permission="*"
                    value=true
                }
            ]
            primary-group=dev
            uuid="$uuid"
        """.trimIndent()

        val file = File(directory.toFile(), "hocon-storage/users/$uuid.conf")
        file.parentFile.mkdirs()
        file.createNewFile()
        file.writeText(prototype)
    }

    fun load(orionServer: OrionServer) {
        this.orionServer = orionServer
        extract("/luckperms/default.conf", "hocon-storage/groups/default.conf")
        extract("/luckperms/dev.conf", "hocon-storage/groups/dev.conf")

        addAdminUser("Bloeckchengrafik", "35afa48c-de21-4869-ae97-0918ca3dd82d")
        addAdminUser("TheNico24", "2aadcbff-f4a6-4ccf-b4e6-4823ca513bf6")
        addAdminUser("_CreepyX_", "18f76a31-eac0-4cad-9a3e-ef0b498a38a5")
        addAdminUser("wi1helm_", "57d72ed1-9766-4054-9c87-6b2b0548934d")
        addAdminUser("p3sto", "3f7db8d7-7537-4ff8-87c1-b3c9e110b03f")
        addAdminUser("JustAlittleWolf", "51709a74-fd73-4c24-a1be-863c0bc4ced7")

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