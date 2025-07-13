package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.addContent
import org.everbuild.asorda.resources.data.items.GameModeMenuIcons
import org.everbuild.asorda.resources.data.items.GlobalIcons
import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.asorda.resources.data.items.PlayerMenuIcons
import org.everbuild.asorda.resources.data.items.SystemIcons
import org.everbuild.asorda.resources.data.items.TradeMenuIcons
import team.unnamed.creative.ResourcePack

suspend fun addItems(pack: ResourcePack) {
    addContent(pack, GlobalIcons)
    addContent(pack, SystemIcons)
    addContent(pack, PlayerMenuIcons)
    addContent(pack, TradeMenuIcons)
    addContent(pack, GameModeMenuIcons)
    addContent(pack, JamItems)
}