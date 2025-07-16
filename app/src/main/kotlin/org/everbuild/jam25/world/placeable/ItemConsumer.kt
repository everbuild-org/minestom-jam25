package org.everbuild.jam25.world.placeable

import net.minestom.server.item.ItemStack
import org.everbuild.jam25.world.Resource

interface ItemConsumer {
    fun consumeItem(item: ItemOrOil)

    sealed interface ItemOrOil {
        fun display(): ItemStack

        data class Item(val itemStack: ItemStack) : ItemOrOil {
            override fun display(): ItemStack {
                return itemStack
            }
        }

        data class Oil(val amount: Int) : ItemOrOil {
            override fun display(): ItemStack {
                return Resource.OIL.symbol
            }
        }
    }
}
