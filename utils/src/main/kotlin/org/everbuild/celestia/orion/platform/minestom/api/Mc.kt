package org.everbuild.celestia.orion.platform.minestom.api

import net.minestom.server.MinecraftServer.*
import net.minestom.server.advancements.AdvancementManager
import net.minestom.server.adventure.bossbar.BossBarManager
import net.minestom.server.command.CommandManager
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.exception.ExceptionManager
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.block.BlockManager
import net.minestom.server.listener.manager.PacketListenerManager
import net.minestom.server.monitoring.BenchmarkManager
import net.minestom.server.network.ConnectionManager
import net.minestom.server.recipe.RecipeManager
import net.minestom.server.registry.DynamicRegistry
import net.minestom.server.scoreboard.TeamManager
import net.minestom.server.timer.SchedulerManager
import net.minestom.server.world.DimensionType
import net.minestom.server.world.biome.Biome

/**
 * Shorthand utilities for MinecraftServer
 */
object Mc {

    val packetListener: PacketListenerManager get() = getPacketListenerManager()
    val exception: ExceptionManager get() = getExceptionManager()
    val connection: ConnectionManager get() = getConnectionManager()
    val instance: InstanceManager get() = getInstanceManager()
    val block: BlockManager get() = getBlockManager()
    val command: CommandManager get() = getCommandManager()
    val recipe: RecipeManager get() = getRecipeManager()
    val team: TeamManager get() = getTeamManager()
    val scheduler: SchedulerManager get() = getSchedulerManager()
    val benchmark: BenchmarkManager get() = getBenchmarkManager()
    val dimensionType: DynamicRegistry<DimensionType> get() = getDimensionTypeRegistry()
    val biome: DynamicRegistry<Biome> get() = getBiomeRegistry()
    val advancement: AdvancementManager get() = getAdvancementManager()
    val bossBar: BossBarManager get() = getBossBarManager()
    val globalEvent: GlobalEventHandler get() = getGlobalEventHandler()
}