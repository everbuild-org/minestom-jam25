package org.everbuild.celestia.orion.platform.minestom.api.command

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission

class PermissionContext(private val permission: String?, private val parent: FunctionalPermissionContext?, private val command: Command, val args: MutableList<Argument<*>> = mutableListOf()) :
    FunctionalPermissionContext {
    override fun check(player: Player): Boolean {
        return if (permission == null) {
            true
        } else {
            player.hasPermission(permission)
        } && (parent?.check(player) ?: true)
    }

    fun command(runnable: KommandContext.() -> Unit) {
        val context = KommandContext(this, command, args)
        runnable.invoke(context)
        context.build()
    }

    fun executesWithCtx(
        vararg args: Argument<*>,
        runnable: KommandContext.KommandExecutionContext.(Player, CommandContext) -> Unit
    ) {
        val context = KommandContext(this, command, this.args)
        context.args.addAll(args)
        context.executesWithCtx(runnable)
        context.build()
    }

    fun executes(
        vararg args: Argument<*>,
        runnable: KommandContext.KommandExecutionContext.() -> Unit
    ) {
        val context = KommandContext(this, command, this.args)
        context.args.addAll(args)
        context.executes(runnable)
        context.build()
    }

    fun requiresPermission(permission: String, runnable: PermissionContext.() -> Unit) {
        val context = PermissionContext(permission, this, command, args)
        context.runnable()
    }
}