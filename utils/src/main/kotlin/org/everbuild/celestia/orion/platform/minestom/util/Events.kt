package org.everbuild.celestia.orion.platform.minestom.util

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

inline fun <reified T : Event> listen(noinline callback: (T) -> Unit) {
    MinecraftServer.getGlobalEventHandler().addListener(T::class.java, callback)
}

inline fun <reified T : Event> EventNode<in T>.listen(noinline callback: (T) -> Unit) {
    this.addListener(T::class.java, callback)
}
