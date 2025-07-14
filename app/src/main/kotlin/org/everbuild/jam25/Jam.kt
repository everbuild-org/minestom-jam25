package org.everbuild.jam25

import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.Reader
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import net.minestom.server.color.Color
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.extras.velocity.VelocityProxy
import net.minestom.server.world.biome.Biome
import net.minestom.server.world.biome.BiomeEffects
import net.worldseed.multipart.ModelEngine
import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.pack.withResourcePack
import org.everbuild.celestia.orion.platform.minestom.pack.withResourcePacksInDev
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.command.GiveCommand
import org.everbuild.jam25.command.QuickStartCommand
import org.everbuild.jam25.command.SetAllowPlayingCommand
import org.everbuild.jam25.item.api.ItemLoader
import org.everbuild.jam25.item.api.withCustomItemListeners
import org.everbuild.jam25.listener.setupPlayerDropEvent
import org.everbuild.jam25.state.GameStateController


object Jam : OrionServer() {
    const val NAME = "<gradient:#FFAA00:#FF5555>Border Defense</gradient>"
    const val PREFIX = "<gradient:#FFAA00:#FF5555>BD ✧</gradient>"

    val gameStates = GameStateController()

    val oilBiome = Mc.biome.register(
        "jam:oil", Biome.builder()
            .effects(
                BiomeEffects.builder()
                    .fogColor(Color(0xC0D8FF))
                    .skyColor(Color(0x78A7FF))
                    .waterColor(Color(0x000000))
                    .waterFogColor(Color(0x50533))
                    .build()
            )
            .build()
    )

    init {
        Mc.globalEvent
            .addChild(gameStates.eventNode())
            .addChild(PingResponder.eventNode())
            .addChild(BlockController.eventNode())
            .addChild(PerInstanceTabList.eventNode())

        withCustomItemListeners()
        ItemLoader.withCustomItemSupport()

        setupPlayerDropEvent()

        TabListController.schedule()

        if (JamConfig.velocityEnable) {
            VelocityProxy.enable(System.getenv("VELOCITY_SECRET"))
            withResourcePack(JamConfig.resourcePackUri)
        } else {
            withResourcePacksInDev()
        }

        listen<PlayerSpawnEvent> { event ->
            event.player.gameMode = GameMode.ADVENTURE
        }

        val models = extractToDir("models")
        val mappingsData: Reader = InputStreamReader(Jam::class.java.getResourceAsStream("/model_mappings.json")!!)
        ModelEngine.loadMappings(mappingsData, models)

        SetAllowPlayingCommand.register()
        QuickStartCommand.register()
        GiveCommand.register()

        withGlobalTickEvent()
    }
}


fun main() {
    Jam.bind()
}