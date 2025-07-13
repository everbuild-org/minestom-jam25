package org.everbuild.jam25.item.impl

import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name

object CableComponentItem : AbstractItem(
    key = "cable_component",
    item = itemStackOf(JamItems.cableComponent)
        .name("<gold>Cable Component")
        .withMaxStackSize(64)
)