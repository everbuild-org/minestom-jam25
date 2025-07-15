package org.everbuild.jam25.block.api

import kotlinx.serialization.Serializable
import org.everbuild.jam25.item.api.ItemDataComponent

@Serializable
@ItemDataComponent(id = "shieldgenrefill")
data class ShieldGeneratorRefillComponent(val canRefill: Boolean = true)
