package org.everbuild.celestia.orion.platform.minestom.api.command

import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.*
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission

abstract class Kommand(name: String, vararg aliases: String) : Command(name, *aliases), FunctionalPermissionContext {
    var permission: String? = null

    fun default(runnable: KommandContext.KommandExecutionContext.(Player, CommandContext) -> Unit) {
        defaultExecutor = CommandExecutor { sender, context ->
            KommandContext(null, this)
                .KommandExecutionContext(context, sender as Player)
                .runnable(sender, context)
        }
    }

    fun requiresPermission(permission: String, runnable: PermissionContext.() -> Unit) {
        val context = PermissionContext(permission, null, this)
        context.runnable()
    }

    fun register() {
        if (this.permission != null) {
            setCondition { player, _ -> player.hasPermission(this.permission!!) }
        }

        MinecraftServer.getCommandManager().register(this)
    }

    fun command(runnable: KommandContext.() -> Unit) {
        val context = KommandContext(null, this)
        runnable.invoke(context)
        context.build()
    }

    fun executesWithCtx(
        vararg args: Argument<*>,
        runnable: KommandContext.KommandExecutionContext.(Player, CommandContext) -> Unit
    ) {
        val context = KommandContext(this, this)
        context.args.addAll(args)
        context.executesWithCtx(runnable)
        context.build()
    }

    fun executes(
        vararg args: Argument<*>,
        runnable: KommandContext.KommandExecutionContext.() -> Unit
    ) {
        val context = KommandContext(this, this)
        context.args.addAll(args)
        context.executes(runnable)
        context.build()
    }

    override fun check(player: Player): Boolean {
        return if (permission != null) {
            player.hasPermission(permission!!)
        } else {
            true
        }
    }
}