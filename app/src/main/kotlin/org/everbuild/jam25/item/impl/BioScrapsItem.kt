package org.everbuild.jam25.item.impl

import org.everbuild.asorda.resources.data.items.JamItems
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.api.itemStackOf
import org.everbuild.jam25.item.api.name

object BioScrapsItem : AbstractItem(
    key = "bio_scraps",
    item = itemStackOf(JamItems.bioScraps)
        .name("<green>Bio Scraps")
        .withMaxStackSize(64)
)