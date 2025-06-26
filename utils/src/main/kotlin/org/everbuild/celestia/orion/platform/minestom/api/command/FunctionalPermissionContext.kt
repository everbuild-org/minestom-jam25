package org.everbuild.celestia.orion.platform.minestom.api.command

import net.minestom.server.entity.Player

interface FunctionalPermissionContext {
    fun check(player: Player): Boolean
}