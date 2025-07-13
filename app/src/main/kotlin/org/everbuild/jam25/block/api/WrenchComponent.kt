package org.everbuild.jam25.block.api

import kotlinx.serialization.Serializable
import org.everbuild.jam25.item.api.ItemDataComponent

@Serializable
@ItemDataComponent(id = "wrench")
data class WrenchComponent(val canBeUsed: Boolean = true)