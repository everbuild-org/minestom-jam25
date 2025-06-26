package org.everbuild.celestia.orion.core.platform

import org.everbuild.celestia.orion.core.util.FullPosition

interface Teleportation {
    fun toLocation(): FullPosition
}