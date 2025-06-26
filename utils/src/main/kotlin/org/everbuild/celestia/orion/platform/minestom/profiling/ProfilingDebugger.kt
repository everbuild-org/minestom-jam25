package org.everbuild.celestia.orion.platform.minestom.profiling

import net.minestom.server.entity.Player
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debuggable
import org.everbuild.celestia.orion.platform.minestom.command.debug.Debugger
import java.text.DateFormat
import java.time.Instant
import java.util.Date

object ProfilingDebugger : Debugger {
    override val identifier: String = "orion/profiling"

    @Debuggable
    fun isProfiling(player: Player) {
        if (Profiler.started == -1L) {
            player.sendMessage("Not active")
        } else {
            val startDate = Date.from(Profiler.started.let { Instant.ofEpochMilli(it) })
            player.sendMessage("Active, started at ${DateFormat.getDateTimeInstance().format(startDate)}")
        }
    }
}