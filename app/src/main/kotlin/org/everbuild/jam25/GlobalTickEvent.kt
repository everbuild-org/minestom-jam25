package org.everbuild.jam25

import net.minestom.server.event.Event
import net.minestom.server.event.EventDispatcher
import net.minestom.server.timer.TaskSchedule
import org.everbuild.celestia.orion.platform.minestom.api.Mc

object GlobalTickEvent : Event

fun withGlobalTickEvent() {
    Mc.scheduler.buildTask {
       EventDispatcher.call(GlobalTickEvent)
    }.repeat(TaskSchedule.nextTick()).schedule()
}