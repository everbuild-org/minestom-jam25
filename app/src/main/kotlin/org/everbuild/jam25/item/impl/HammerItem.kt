package org.everbuild.jam25.item.impl

import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.block.api.WrenchComponent
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name
import org.everbuild.jam25.item.api.with

object HammerItem : AbstractItem(
    key = "hammer",
    item = itemStackOf(JamItems.hammer)
        .name("<yellow>Construction Tool")
        .with(WrenchComponent())
)