package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.minecraftKey
import org.everbuild.asorda.resources.data.api.resource
import team.unnamed.creative.ResourcePack

suspend fun addContainerUtils(pack: ResourcePack) {
    pack.texture(minecraftKey("gui/container/generic_54.png"), resource("minecraft/container/generic_54.png"))
    pack.texture(minecraftKey("gui/container/inventory.png"), resource("minecraft/container/inventory.png"))
    pack.texture(minecraftKey("gui/sprites/container/slot_highlight_back.png"), resource("minecraft/container/slot_highlight.png"))
    pack.texture(minecraftKey("gui/sprites/container/slot_highlight_front.png"), resource("minecraft/container/slot_highlight.png"))
}