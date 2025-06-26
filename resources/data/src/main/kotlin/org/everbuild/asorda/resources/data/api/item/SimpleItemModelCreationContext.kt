package org.everbuild.asorda.resources.data.api.item

import team.unnamed.creative.item.ItemModel

class SimpleItemModelCreationContext : ItemModelCreationContext() {
    private var model: ItemModel? = null

    override fun addItemModel(itemModel: ItemModel) {
        if (model != null) {
            throw IllegalStateException("Only one item model is allowed")
        }

        model = itemModel
    }

    override fun createModel(): ItemModel {
        return model ?: throw IllegalStateException("No item model defined")
    }
}