package org.everbuild.celestia.orion.platform.minestom.api.command

import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.translation.SharedTranslations
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated

class KommandContext(
    private val permission: FunctionalPermissionContext?,
    private val command: Command,
    val args: MutableList<Argument<*>> = mutableListOf()
) {
    private var executor = CommandExecutor { _, _ -> }

    fun executesWithCtx(runnable: KommandExecutionContext.(Player, CommandContext) -> Unit) {
        executor = CommandExecutor { sender, context ->
            runnable.invoke(KommandExecutionContext(context, sender as Player), sender, context)
        }
    }

    fun executes(runnable: KommandExecutionContext.() -> Unit) {
        executesWithCtx(runnable = { _, _ -> runnable.invoke(this) })
    }

    fun command(runnable: KommandContext.() -> Unit) {
        val context = KommandContext(permission, command, this.args.map { it }.toMutableList())
        runnable.invoke(context)
        context.build()
    }

    fun executesWithCtx(
        vararg args: Argument<*>,
        runnable: KommandContext.KommandExecutionContext.(Player, CommandContext) -> Unit
    ) {
        val context = KommandContext(permission, command, this.args.map { it }.toMutableList())
        context.args.addAll(args)
        context.executesWithCtx(runnable)
        context.build()
    }

    fun executes(
        vararg args: Argument<*>,
        runnable: KommandContext.KommandExecutionContext.() -> Unit
    ) {
        executesWithCtx(*args) { _, _ -> runnable() }
    }

    fun requiresPermission(permission: String, runnable: PermissionContext.() -> Unit) {
        val context = PermissionContext(permission, this.permission, command, args.map { it }.toMutableList())
        context.runnable()
    }

    inner class KommandExecutionContext(val ctx: CommandContext, private val sender: Player) {
        inner class ArgList {
            operator fun <T> get(arg: Argument<T>): T = ctx.get(arg)
            operator fun get(arg: PlayerArgument): Player = ctx.get(arg).findFirstPlayer(sender) ?: run {
                sender.sendTranslated(SharedTranslations.playerNotFound)
                throw EarlyExit()
            }
            operator fun get(arg: OrionPlayerArgument): OrionPlayer = loadOrionPlayer(arg)
        }

        val args = ArgList()
        val player = sender

        operator fun <T> Argument<T>.invoke(): T = args[this]
        operator fun PlayerArgument.invoke(): Player = args[this]
        operator fun OrionPlayerArgument.invoke(): OrionPlayer = args[this]

        private fun loadOrionPlayer(arg: OrionPlayerArgument): OrionPlayer {
            val player = ctx.get(arg)
            val orionPlayer = PlayerLoader.load(player)

            if (orionPlayer == null) {
                sender.sendTranslated(SharedTranslations.playerNotFound)
                throw EarlyExit()
            }

            return orionPlayer
        }
    }

    internal fun build() {
        if (permission != null) {
            command.addSyntax({ a, b ->
                if (a is Player && !permission.check(a)) {
                    a.sendTranslated(SharedTranslations.noPermissions)
                    return@addSyntax
                }
                try {
                    executor.apply(a, b)
                } catch (e: EarlyExit) {
                    // Do nothing, this is control flow
                }
            }, *args.toTypedArray())
        } else {
            command.addSyntax({ a, b ->
                try {
                    executor.apply(a, b)
                } catch (e: EarlyExit) {
                    // Do nothing, this is control flow
                }
            }, *args.toTypedArray())
        }
    }
}