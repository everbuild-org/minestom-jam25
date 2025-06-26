package org.everbuild.celestia.orion.platform.minestom.command.debug

import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.luckperms.hasPermission
import org.reflections.Reflections

object DebugCommand : Command("debug") {
    private val debuggers: HashMap<String, Debugger> = HashMap()

    init {
        val reflections = Reflections("org.everbuild")
        reflections.getSubTypesOf(Debugger::class.java).forEach {
            // should be a kotlin OBJECT
            val debugger = it.getField("INSTANCE").get(null) as Debugger
            debuggers[debugger.identifier] = debugger
        }

        setCondition { sender, _ -> (sender as Player).hasPermission("orion.adm.debug") }

        setDefaultExecutor { sender, _ ->
            sender.sendMessage("Available debuggers:")
            debuggers.forEach { (id, debugger) ->
                sender.sendMessage(" - $id: ${debugger.javaClass.simpleName}")
            }
        }

        debuggers.forEach { (_, debugger) ->
            addSubcommand(debugger.createCommand())
        }
    }
}