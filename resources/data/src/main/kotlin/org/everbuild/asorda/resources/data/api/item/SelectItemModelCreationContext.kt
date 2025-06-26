package org.everbuild.asorda.resources.data.api.item

import org.everbuild.asorda.resources.data.api.InElementDsl
import team.unnamed.creative.item.ItemModel
import team.unnamed.creative.item.property.ItemStringProperty

class SelectItemModelCreationContext(property: ItemStringProperty) {
    private val model = ItemModel.select()
        .property(property)

    @InElementDsl
    fun case(vararg value: String, block: SimpleItemModelCreationContext.() -> Unit) {
        model.addCase(SimpleItemModelCreationContext().apply(block).createModel(), *value)
    }

    @InElementDsl
    fun default(block: SimpleItemModelCreationContext.() -> Unit) {
        model.fallback(SimpleItemModelCreationContext().apply(block).createModel())
    }

    fun createModel(): ItemModel {
        return model.build()
    }
}