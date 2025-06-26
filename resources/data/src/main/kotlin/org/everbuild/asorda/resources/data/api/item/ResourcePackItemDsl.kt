package org.everbuild.asorda.resources.data.api.item

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.BuildableResource
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.item.Item
import team.unnamed.creative.item.ItemModel

class ResourcePackItemDsl(val id: Key) : ItemModelCreationContext(), BuildableResource {
    var parent: String = "minecraft:iron_horse_armor"
    var handAnimationOnSwap = true
    var oversizedInGui = true
    var itemModel: ItemModel? = null

    override fun addItemModel(itemModel: ItemModel) {
        this.itemModel = itemModel
    }

    override fun createModel(): ItemModel {
        return itemModel ?: throw IllegalStateException("No item model defined")
    }

    override fun buildInto(pack: ResourcePack) {
        pack.item(Item.item(id, itemModel!!, handAnimationOnSwap, oversizedInGui))
    }
}