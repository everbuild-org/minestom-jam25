package org.everbuild.celestia.orion.platform.minestom.util

import net.minestom.server.MinecraftServer
import net.minestom.server.timer.TaskSchedule
import java.time.Duration
import kotlin.time.toJavaDuration

fun scheduler(repeat: Duration, callback: () -> Unit) {
    MinecraftServer.getSchedulerManager().buildTask(callback).repeat(repeat).schedule()
}

fun scheduler(repeat: kotlin.time.Duration, callback: () -> Unit) {
    scheduler(repeat.toJavaDuration(), callback)
}

fun tickLater(callback: () -> Unit) {
    MinecraftServer.getSchedulerManager().buildTask(callback).delay(TaskSchedule.nextTick()).schedule()
}

fun later(delay: Duration, callback: () -> Unit) {
    MinecraftServer.getSchedulerManager().buildTask(callback).delay(delay).schedule()
}

infix fun kotlin.time.Duration.later(callback: () -> Unit) {
    later(this.toJavaDuration(), callback)
}