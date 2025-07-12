package org.everbuild.jam25.world.placeable

import net.minestom.server.instance.Instance

interface StatefulWorldElement<Event> {
    fun consumeEvent(instance: Instance, event: Event)
}