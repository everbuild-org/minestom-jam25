package org.everbuild.asorda.resources.data.api.item

import org.everbuild.asorda.resources.data.api.InElementDsl
import team.unnamed.creative.item.ItemModel
import team.unnamed.creative.item.property.ItemNumericProperty
import team.unnamed.creative.item.property.ItemStringProperty

class RangeDispatchItemModelCreationContext(property: ItemNumericProperty, scale: Float) {
    private val model = ItemModel.rangeDispatch()
        .property(property)
        .scale(scale)

    @InElementDsl
    fun threshold(value: Float, block: SimpleItemModelCreationContext.() -> Unit) {
        model.addEntry(value, SimpleItemModelCreationContext().apply(block).createModel())
    }

    @InElementDsl
    fun default(block: SimpleItemModelCreationContext.() -> Unit) {
        model.fallback(SimpleItemModelCreationContext().apply(block).createModel())
    }

    fun createModel(): ItemModel {
        return model.build()
    }
}