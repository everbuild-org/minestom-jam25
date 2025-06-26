package org.everbuild.asorda.resources.data.api.item

import team.unnamed.creative.item.ItemModel

class CompositeItemModelCreationContext : ItemModelCreationContext() {
    private val models = mutableListOf<ItemModel>()

    override fun addItemModel(itemModel: ItemModel) {
        models.add(itemModel)
    }

    override fun createModel(): ItemModel = ItemModel.composite(models)
}