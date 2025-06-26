package org.everbuild.asorda.resources.data.items

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture
import team.unnamed.creative.item.property.ItemBooleanProperty
import team.unnamed.creative.model.Model

object TradeMenuIcons : ContentList("trademenu") {
    val buttonCheckbox = createItem("trademenu/button_left_checkbox") {
        condition(ItemBooleanProperty.customModelData(0)) { active ->
            model(createModel {
                parent(Model.ITEM_GENERATED)
                textures(
                    "layer0" to Texture(if (active) "trade_menu/checkbox_yes" else "trade_menu/checkbox_no")
                )
            })
        }
    }

    val buttonWaiting = createItem("trademenu/button_left_waiting") {
        condition(ItemBooleanProperty.customModelData(0)) { active ->
            model(SystemIcons.hourglassModel)
        }
    }

    val buttonCubit = createItem("trademenu/button_cubit") {
        model(createModel {
            parent(Model.ITEM_GENERATED)
            textures(
                "layer0" to Texture("general/cubit_coin")
            )
        })
    }
}