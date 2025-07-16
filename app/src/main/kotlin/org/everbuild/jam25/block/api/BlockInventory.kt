package org.everbuild.jam25.block.api

import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

class BlockInventory(val maxSize: Int, val items: List<ItemStack> = emptyList()) {
    fun toNBT(): CompoundBinaryTag {
        return CompoundBinaryTag.builder()
            .putInt("maxSize", maxSize)
            .put("items", ListBinaryTag.listBinaryTag(BinaryTagTypes.COMPOUND, items.map(ItemStack::toItemNBT)))
            .build()
    }

    companion object {
        val tag = Tag.NBT("customInventory")

        fun fromNBT(nbt: CompoundBinaryTag): BlockInventory {
            return BlockInventory(
                nbt.getInt("maxSize"),
                nbt.getList("items").map { ItemStack.fromItemNBT(it as CompoundBinaryTag) }
            )
        }
    }
}

fun Block.withInventory(customInventory: BlockInventory) = withTag(BlockInventory.tag, customInventory.toNBT())
fun Block.getInventory(): BlockInventory? {
    return (getTag(BlockInventory.tag) as? CompoundBinaryTag)?.let { BlockInventory.fromNBT(it) }
}