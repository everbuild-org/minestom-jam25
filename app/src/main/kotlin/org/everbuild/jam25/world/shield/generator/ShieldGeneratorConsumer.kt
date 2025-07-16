package org.everbuild.jam25.world.shield.generator

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.platform.minestom.api.utils.dropItem
import org.everbuild.jam25.world.Resource
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer

class ShieldGeneratorConsumer(val generator: ShieldGenerator, val instance: Instance, val pos: BlockVec) : AdvanceableWorldElement, ItemConsumer {
    override fun advance(instance: Instance) {
    }

    override fun consumeItem(item: ItemConsumer.ItemOrOil) {
        if (item is ItemConsumer.ItemOrOil.Oil) {
            generator.refill(generator.refillOilPowerGain * item.amount)
        } else if (item is ItemConsumer.ItemOrOil.Item) {
            if (Resource.BIO_SCRAPS.symbol.isSimilar(item.itemStack)) {
                generator.refill(generator.refillBioScrapsPowerGain * item.itemStack.amount())
            } else {
                instance.dropItem(item.itemStack, generator.position.asVec().add(0.5, 4.5, 0.5))
            }
        }
    }

    override fun getBlockPosition(): BlockVec = pos
}