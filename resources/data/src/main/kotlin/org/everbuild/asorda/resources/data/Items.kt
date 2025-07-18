package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.addContent
import org.everbuild.asorda.resources.data.api.resource
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

//    pack.unknownFile("assets/minecraft/models/item/spyglass_in_hand.json", resource("jam/spyglass.json"))
//    pack.unknownFile("assets/minecraft/textures/item/spyglass_in_hand.png", resource("jam/spyglass.png"))
}