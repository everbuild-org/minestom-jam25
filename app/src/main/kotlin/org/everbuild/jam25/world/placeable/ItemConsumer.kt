package org.everbuild.jam25.world.placeable

import net.minestom.server.item.ItemStack
import org.everbuild.jam25.world.Resource

interface ItemConsumer {
    fun consumeItem(item: ItemOrOil)

    sealed interface ItemOrOil {
        fun display(): ItemStack
        fun amount(): Int
        fun withAmount(amount: Int): ItemOrOil

        data class Item(val itemStack: ItemStack) : ItemOrOil {
            override fun display(): ItemStack {
                return itemStack
            }

            override fun amount() = itemStack.amount()
            override fun withAmount(amount: Int) = Item(itemStack.withAmount(amount))
        }

        data class Oil(val amount: Int) : ItemOrOil {
            override fun display(): ItemStack {
                return Resource.OIL.symbol
            }

            override fun amount() = amount
            override fun withAmount(amount: Int) = Oil(amount)
        }
    }
}
