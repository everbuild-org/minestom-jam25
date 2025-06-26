package org.everbuild.celestia.orion.platform.minestom.menu

import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.Click

data class ReInventoryClickEvent(val slot: Int, val click: Click, val event: InventoryPreClickEvent)