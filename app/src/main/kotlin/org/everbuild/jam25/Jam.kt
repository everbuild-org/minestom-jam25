package org.everbuild.jam25

import net.minestom.server.extras.velocity.VelocityProxy
import org.everbuild.celestia.orion.platform.minestom.OrionServer
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.pack.withResourcePack
import org.everbuild.celestia.orion.platform.minestom.pack.withResourcePacksInDev
import org.everbuild.jam25.command.GiveCommand
import org.everbuild.jam25.command.QuickStartCommand
import org.everbuild.jam25.command.SetAllowPlayingCommand
import org.everbuild.jam25.item.api.ItemLoader
import org.everbuild.jam25.item.api.withCustomItemListeners
import org.everbuild.jam25.state.GameStateController

object Jam : OrionServer() {
    const val NAME = "<gradient:#FFAA00:#FF5555>Border Defense</gradient>"
    const val PREFIX = "<gradient:#FFAA00:#FF5555>BD ✧</gradient>"

    val gameStates = GameStateController()

    init {
        Mc.globalEvent
            .addChild(gameStates.eventNode())
            .addChild(PingResponder.eventNode())

        withCustomItemListeners()
        ItemLoader.withCustomItemSupport()
        TabListController.schedule()

        if (JamConfig.velocityEnable) {
            VelocityProxy.enable(JamConfig.velocitySecret)
            withResourcePack(JamConfig.resourcePackUri)
        } else {
            withResourcePacksInDev()
        }

        SetAllowPlayingCommand.register()
        QuickStartCommand.register()
        GiveCommand.register()

        withGlobalTickEvent()
    }
}


fun main() {
    Jam.bind()
}