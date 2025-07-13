package org.everbuild.jam25.item.impl

import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name

object HammerItem : AbstractItem(
    key = "hammer",
    item = itemStackOf(JamItems.hammer)
        .name("<yellow>Construction Tool")
)