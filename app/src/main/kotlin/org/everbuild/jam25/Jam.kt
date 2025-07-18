package org.everbuild.jam25

import java.io.InputStreamReader
import java.io.Reader
import kotlin.jvm.java
import net.minestom.server.color.Color
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.extras.velocity.VelocityProxy
import net.minestom.server.network.packet.client.play.ClientSelectTradePacket
import net.minestom.server.world.biome.Biome
import net.minestom.server.world.biome.BiomeEffects
import net.worldseed.multipart.ModelEngine
import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.api.utils.logger
import org.everbuild.celestia.orion.platform.minestom.pack.withResourcePack
import org.everbuild.celestia.orion.platform.minestom.pack.withResourcePacksInDev
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.command.GiveCommand
import org.everbuild.jam25.command.QueueCommand
import org.everbuild.jam25.command.QuickStartCommand
import org.everbuild.jam25.command.SetAllowPlayingCommand
import org.everbuild.jam25.command.SpectatorCommand
import org.everbuild.jam25.item.api.ItemLoader
import org.everbuild.jam25.item.api.withCustomItemListeners
import org.everbuild.jam25.listener.ClientSelectTradePacketListener
import org.everbuild.jam25.listener.setupPlayerDropEvent
import org.everbuild.jam25.map.InteractionController
import org.everbuild.jam25.state.GameStateController
import org.everbuild.jam25.world.placeable.ItemHolder

object Jam : OrionServer() {
    const val NAME = "<gradient:#FFAA00:#FF5555>Shield Defense</gradient>"
    const val PREFIX = "<gradient:#FFAA00:#FF5555>SD ✧</gradient>"

    val oilBiome = Mc.biome.register(
        "jam:oil", Biome.builder()
            .effects(
                BiomeEffects.builder()
                    .fogColor(Color(0xC0D8FF))
                    .skyColor(Color(0x78A7FF))
                    .waterColor(Color(0x0f0f0f))
                    .waterFogColor(Color(0x0f0f0f))
                    .build()
            )
            .build()
    )

    val gameStates = GameStateController()

    init {
        Mc.globalEvent
            .addChild(gameStates.eventNode())
            .addChild(PingResponder.eventNode())
            .addChild(BlockController.eventNode())
            .addChild(ItemHolder.interactEventNode())
            .addChild(InteractionController.eventNode())
            .addChild(PerInstanceTabList.eventNode())

        Mc.packetListener.setPlayListener(ClientSelectTradePacket::class.java, ClientSelectTradePacketListener::listener)

        withCustomItemListeners()
        ItemLoader.withCustomItemSupport()

        setupPlayerDropEvent()

        TabListController.schedule()

        if (System.getenv("VELOCITY_SECRET") != null) {
            logger.info("Using Velocity Secret Manager")
            VelocityProxy.enable(System.getenv("VELOCITY_SECRET"))
        } else {
            logger.info("Using Dev Resources")
            withResourcePacksInDev()
        }

        listen<PlayerSpawnEvent> { event ->
            val player = event.player
            val inGameState = gameStates.getInGamePhase(player)
            if (inGameState != null) {
                val spectatorState = gameStates.getOrCreateSpectatorState(inGameState)
                if (spectatorState.isSpectator(player)) {
                    player.gameMode = GameMode.SPECTATOR
                    player.getAttribute(Attribute.BLOCK_BREAK_SPEED).baseValue = 0.0
                    return@listen
                }
            }
            player.gameMode = GameMode.SURVIVAL
            player.getAttribute(Attribute.BLOCK_BREAK_SPEED).baseValue = 0.0
        }

        val models = extractToDir("models")
        val mappingsData: Reader = InputStreamReader(Jam::class.java.getResourceAsStream("/model_mappings.json")!!)
        ModelEngine.loadMappings(mappingsData, models)

        SetAllowPlayingCommand.register()
        QuickStartCommand.register()
        GiveCommand.register()
        QueueCommand.register()
        SpectatorCommand.register()

        withGlobalTickEvent()
    }
}


fun main() {
    Jam.bind()
}