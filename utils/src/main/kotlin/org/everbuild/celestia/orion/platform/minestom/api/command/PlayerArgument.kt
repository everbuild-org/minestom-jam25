package org.everbuild.celestia.orion.platform.minestom.api.command

import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity

class PlayerArgument(id: String) : ArgumentEntity(id) {
    init {
        onlyPlayers(true)
        singleEntity(true)
    }
}