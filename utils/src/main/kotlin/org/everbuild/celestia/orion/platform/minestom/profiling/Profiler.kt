package org.everbuild.celestia.orion.platform.minestom.profiling

import io.pyroscope.http.Format
import io.pyroscope.javaagent.EventType
import io.pyroscope.javaagent.PyroscopeAgent
import io.pyroscope.javaagent.api.Logger
import io.pyroscope.javaagent.config.Config
import io.pyroscope.labels.LabelsSet
import io.pyroscope.labels.Pyroscope
import java.util.concurrent.Callable
import org.everbuild.celestia.orion.core.util.serverName
import org.everbuild.celestia.orion.platform.minestom.MinestomOrionConfig
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object Profiler {
    private val enable = MinestomOrionConfig.pyroscopeServer.isNotBlank()
    var started: Long = -1
        private set

    fun start() {
        if (!enable) return
        started = System.currentTimeMillis()
        PyroscopeAgent.start(
            Config.Builder()
                .setApplicationName("orion_$serverName")
                .setFormat(Format.JFR)
                .setProfilingEvent(EventType.ITIMER)
                .setProfilingAlloc("512k")
                .setServerAddress(MinestomOrionConfig.pyroscopeServer)
                .build()
        )
    }

    fun stop() {
        if (!enable) return
        started = -1
        PyroscopeAgent.stop()
    }

    operator fun <T> invoke(areaLabel: String, block: Callable<T>): T {
        if (!enable) return block.call()
        return Pyroscope.LabelsWrapper.run(
            LabelsSet(
                mapOf(
                    "area" to areaLabel
                )
            ), block
        )
    }

    operator fun <T> invoke(vararg areaLabels: Pair<String, String>, block: Callable<T>): T {
        if (!enable) return block.call()
        return Pyroscope.LabelsWrapper.run(LabelsSet(areaLabels.toMap()), block)
    }
}