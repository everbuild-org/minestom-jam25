package org.everbuild.jam25.item.impl

import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name

object DigitalComponentItem : AbstractItem(
    key = "digital_component",
    item = itemStackOf(JamItems.digitalComponent)
        .name("<gray>Digital Component")
        .withMaxStackSize(64)
)