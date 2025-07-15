package org.everbuild.jam25.item.impl

import net.minestom.server.item.Material
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name

object SiliconItem : AbstractItem(
    key = "silicon_dust",
    item = itemStackOf(Material.GUNPOWDER)
        .name("<white>Silicon Dust")
        .withMaxStackSize(64)
)