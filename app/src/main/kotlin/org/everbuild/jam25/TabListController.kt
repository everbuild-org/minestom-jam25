package org.everbuild.jam25

import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.everbuild.celestia.orion.core.util.minimessage
import org.everbuild.celestia.orion.platform.minestom.api.Mc


object TabListController {
    fun schedule() {
        Mc.scheduler.buildTask(::updateTabList)
            .repeat(1.seconds.toJavaDuration())
            .schedule()
    }

    private fun updateTabList() {
        for (player in Mc.connection.onlinePlayers) {
            player.sendPlayerListHeaderAndFooter(
                "\n\n<gradient:#FFAA00:#FF5555><bold>${Jam.NAME}</bold></gradient>\n\n".minimessage(),
                "\n<gradient:#FFAA00:#FF5555>✧ Asorda Jam Entry ✧</gradient> \n\n<gray>Wi1helm, _CreepyX_,\n<gray>    TheNico24, Bloeckchengrafik    \n".minimessage()
            )
        }
    }
}