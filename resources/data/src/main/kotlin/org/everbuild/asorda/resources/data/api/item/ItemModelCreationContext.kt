package org.everbuild.asorda.resources.data.api.item

import org.everbuild.asorda.resources.data.api.InElementDsl
import org.everbuild.asorda.resources.data.api.maybeAsordaKey
import org.everbuild.asorda.resources.data.api.model.ModelResource
import team.unnamed.creative.item.ItemModel
import team.unnamed.creative.item.property.ItemBooleanProperty
import team.unnamed.creative.item.property.ItemNumericProperty
import team.unnamed.creative.item.property.ItemStringProperty
import team.unnamed.creative.item.special.SpecialRender
import team.unnamed.creative.item.tint.TintSource

abstract class ItemModelCreationContext {
    internal abstract fun addItemModel(itemModel: ItemModel)
    internal abstract fun createModel(): ItemModel

    @InElementDsl
    fun model(id: String, vararg tintSource: TintSource) {
        addItemModel(ItemModel.reference(id.maybeAsordaKey(), *tintSource))
    }

    @InElementDsl
    fun model(id: ModelResource, vararg tintSource: TintSource) {
        addItemModel(ItemModel.reference(id.key, *tintSource))
    }

    @InElementDsl
    fun composite(block: CompositeItemModelCreationContext.() -> Unit) {
        val data = CompositeItemModelCreationContext()
        data.block()
        addItemModel(data.createModel())
    }

    @InElementDsl
    fun condition(
        condition: ItemBooleanProperty,
        whenTrue: SimpleItemModelCreationContext.() -> Unit,
        whenFalse: SimpleItemModelCreationContext.() -> Unit
    ) {
        val whenTrueValue = SimpleItemModelCreationContext().apply(whenTrue).createModel()
        val whenFalseValue = SimpleItemModelCreationContext().apply(whenFalse).createModel()

        addItemModel(ItemModel.conditional(condition, whenTrueValue, whenFalseValue))
    }

    @InElementDsl
    fun condition(
        condition: ItemBooleanProperty,
        value: SimpleItemModelCreationContext.(Boolean) -> Unit,
    ) {
        val whenTrueValue = SimpleItemModelCreationContext().also { it.value(true) }.createModel()
        val whenFalseValue = SimpleItemModelCreationContext().also { it.value(false) }.createModel()

        addItemModel(ItemModel.conditional(condition, whenTrueValue, whenFalseValue))
    }

    @InElementDsl
    fun select(property: ItemStringProperty, cases: SelectItemModelCreationContext.() -> Unit) {
        addItemModel(SelectItemModelCreationContext(property).apply(cases).createModel())
    }

    @InElementDsl
    fun rangeDispatch(
        property: ItemNumericProperty,
        scale: Float = 1.0f,
        cases: RangeDispatchItemModelCreationContext.() -> Unit
    ) {
        addItemModel(RangeDispatchItemModelCreationContext(property, scale).apply(cases).createModel())
    }

    @InElementDsl
    fun empty() {
        addItemModel(ItemModel.empty())
    }

    @InElementDsl
    fun bundleSelectedItem() {
        addItemModel(ItemModel.bundleSelectedItem())
    }

    @InElementDsl
    fun special(base: String, special: SpecialRender) {
        addItemModel(ItemModel.special(special, base.maybeAsordaKey()))
    }

    @InElementDsl
    fun special(base: ModelResource, special: SpecialRender) {
        addItemModel(ItemModel.special(special, base.key))
    }
}