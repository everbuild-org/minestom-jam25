package org.everbuild.jam25.resource

import net.minestom.server.item.ItemStack
import org.everbuild.jam25.item.impl.BioScrapsItem
import org.everbuild.jam25.item.impl.MetalScrapsItem
import org.everbuild.jam25.item.impl.SiliconItem
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class SpawneableResource(val display: String, val item: ItemStack, val timeToSpawn: Duration, val maxSize: Int) {
    BIO_SCRAPS("<gold>Spawning:<br><green>Bio Scraps", BioScrapsItem.createItem(), 3.seconds, 64),
    METAL_SCRAPS("<gold>Spawning:<br><white>Metal Scraps", MetalScrapsItem.createItem(), 4.seconds, 64),
    SILICON_DUST("<gold>Spawning:<br><white>Silicon Dust", SiliconItem.createItem(), 7.seconds, 32),
}