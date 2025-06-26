package org.everbuild.celestia.orion.platform.minestom.api.utils

import net.kyori.adventure.key.Key
import net.minestom.server.instance.block.BlockHandler
import org.everbuild.celestia.orion.platform.minestom.api.Mc

fun BlockHandler.register() = Mc.block.registerHandler(this.key) { this }
fun BlockHandler.register(namespace: String) = Mc.block.registerHandler(namespace) { this }
fun BlockHandler.register(namespace: Key) = Mc.block.registerHandler(namespace) { this }