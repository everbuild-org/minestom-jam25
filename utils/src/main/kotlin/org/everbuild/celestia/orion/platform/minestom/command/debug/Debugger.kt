package org.everbuild.celestia.orion.platform.minestom.command.debug

import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import java.util.*

interface Debugger {
    val identifier: String

    fun createCommand(): Command {
        val params = javaClass
            .declaredMethods
            .filter { method -> method.annotations.any { it.annotationClass == Debuggable::class } }
            .map { method ->
                method.isAccessible = true
                WrappedMethod(
                    method.name,
                    method,
                    this,
                    Arrays.stream(method.parameters).skip(1).map { WrappedParameter(it, method.name) }.toList()
                )
            }

        return object : Command(identifier) {
            init {
                addSyntax({ sender, _ ->
                    sender.sendMessage("Available methods:")
                    params.forEach {
                        sender.sendMessage(" - $it")
                    }
                })

                for (wrapped in params) {
                    val args = wrapped.params.map { it.argument }
                    addSyntax({sender, ctx ->
                        val mappedArgs = MutableList<Any?>(wrapped.params.size) { null }

                        for ((i, arg) in wrapped.params.withIndex()) {
                            mappedArgs[i] = arg.cast(ctx.get(arg.argument), sender as Player)
                        }

                        wrapped.method.invoke(wrapped.instance, sender as Player, *mappedArgs.toTypedArray())
                    }, Arg.literal(wrapped.name), *args.toTypedArray())
                }
            }
        }
    }
}