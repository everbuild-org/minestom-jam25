package org.everbuild.jam25.item.impl

import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name

object SiliconItem : AbstractItem(
    key = "silicon_dust",
    item = itemStackOf(JamItems.siliconDust)
        .name("<white>Silicon Dust")
        .withMaxStackSize(64)
)