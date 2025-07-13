package org.everbuild.jam25.block.api

import net.kyori.adventure.key.Key
import net.minestom.server.collision.BoundingBox
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.GameMode
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerStartDiggingEvent
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.everbuild.jam25.block.impl.PipeBlock
import org.everbuild.jam25.item.api.get
import org.everbuild.jam25.item.api.has
import org.everbuild.jam25.item.api.with

object BlockController {
    val blocks = listOf<CustomBlock>(
        PipeBlock
    )
    val typeTag = Tag.String("blocktype")

    fun getBlockImpl(key: Key): CustomBlock? = blocks.find { it.key() == key }
    fun getBlock(key: Key): CustomBlock = getBlockImpl(key) ?: throw IllegalArgumentException("Block with key $key not found")
    fun getBlock(itemStack: ItemStack): CustomBlock? = itemStack.get<BlockItemComponent>()?.let { getBlock(Key.key(it.customBlock)) }
    fun hasBlock(itemStack: ItemStack): Boolean = itemStack.has<BlockItemComponent>() && getBlock(itemStack) != null
    fun hasBlock(key: Key): Boolean = blocks.find { it.key() == key } != null
    fun hasBlock(itemStack: ItemStack, key: Key): Boolean = itemStack.has<BlockItemComponent>() && itemStack.get<BlockItemComponent>()?.customBlock == key.asString()
    fun hasBlock(itemStack: ItemStack, block: CustomBlock): Boolean = itemStack.has<BlockItemComponent>() && itemStack.get<BlockItemComponent>()?.customBlock == block.key().asString()

    fun eventNode(): EventNode<Event> {
        return EventNode.all("block-controller")
            .listen<PlayerBlockInteractEvent, _> { event ->
                val targetPos = event.blockPosition.relative(event.blockFace)
                val item = event.player.getItemInHand(event.hand)
                val block = getBlock(item) ?: return@listen
                val bounds = BoundingBox(1.0, 1.0, 1.0)
                if (bounds.intersectEntity(targetPos, event.player)) return@listen

                block.placeBlock(event.player.instance!!, targetPos, event.player)
                updateAround(event.player.instance!!, targetPos)

                if (event.player.gameMode != GameMode.CREATIVE) {
                    val newItem = item.withAmount(item.amount() - 1)
                    event.player.setItemInHand(event.hand, newItem)
                }
            }
            .listen<PlayerBlockInteractEvent, _> { event ->
                val item = event.player.itemInMainHand
                if (!item.has<WrenchComponent>()) return@listen
                val targetType = event.block.getTag(typeTag) ?: return@listen
                val targetBlock = getBlockImpl(Key.key(targetType)) ?: return@listen
                targetBlock.breakBlock(event.player.instance!!, event.blockPosition, event.player)
                updateAround(event.player.instance!!, event.blockPosition)
                event.player.swingMainHand()
            }
    }

    fun updateAround(instance: Instance, block: BlockVec) {
        for (x in block.blockX() - 1..block.blockX() + 1) {
            for (y in block.blockY() - 1..block.blockY() + 1) {
                for (z in block.blockZ() - 1..block.blockZ() + 1) {
                    if (x == block.blockX() && y == block.blockY() && z == block.blockZ()) continue
                    val pos = BlockVec(x, y, z)
                    val blockType = instance.getBlock(pos).getTag(typeTag) ?: continue
                    val blockImpl = getBlockImpl(Key.key(blockType)) ?: continue
                    blockImpl.update(instance, pos)
                }
            }
        }
    }
}

fun ItemStack.attachCustomBlock(customBlock: CustomBlock) = this.with(
    BlockItemComponent(customBlock.key().asString())
)