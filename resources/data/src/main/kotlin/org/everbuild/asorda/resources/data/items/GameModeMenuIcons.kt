package org.everbuild.asorda.resources.data.items

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.item.ItemResource
import org.everbuild.asorda.resources.data.api.model.ModelResource
import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.models.UtilityModels
import team.unnamed.creative.item.property.ItemBooleanProperty

typealias TabModelPair = Pair<ModelResource, ModelResource>

object GameModeMenuIcons : ContentList("gamemodemenu") {
    val leftButton = createModel("gamemodemenu/button_left") {
        parent(UtilityModels.baseButton)
        textures(
            "layer0" to Texture("gamemode_menu/left_tab")
        )
    }
    val leftButtonActive = createModel("gamemodemenu/button_left_active") {
        parent(UtilityModels.baseButton)
        textures(
            "layer0" to Texture("gamemode_menu/left_tab_active")
        )
    }
    val rightButton = createModel("gamemodemenu/right_tab") {
        parent(UtilityModels.baseButton)
        textures(
            "layer0" to Texture("gamemode_menu/right_tab")
        )
    }
    val rightButtonActive = createModel("gamemodemenu/right_tab_active") {
        parent(UtilityModels.baseButton)
        textures(
            "layer0" to Texture("gamemode_menu/right_tab_active")
        )
    }

    val left = leftButton to leftButtonActive
    val right = rightButton to rightButtonActive

    sealed interface TabOverlay {
        @JvmInline
        value class StringOverlay(val value: String) : TabOverlay

        @JvmInline
        value class ModelOverlay(val model: ModelResource) : TabOverlay
    }

    fun createTab(
        stateModels: TabModelPair,
        overlay: TabOverlay,
        tabIdentifier: String
    ): ItemResource {
        return createItem(tabIdentifier) {
            composite {
                val customModelDataCondition = ItemBooleanProperty.customModelData(0)
                condition(customModelDataCondition) { isActive ->
                    model(if (isActive) stateModels.second else stateModels.first)
                }

                when (overlay) {
                    is TabOverlay.StringOverlay -> model(overlay.value)
                    is TabOverlay.ModelOverlay -> model(overlay.model)
                }
            }
        }
    }
    val leftBuildingBlocks = createTab(left, TabOverlay.StringOverlay("minecraft:block/stone"), "building_blocks")
    val leftEnergyBlocks = createTab(left, TabOverlay.StringOverlay("minecraft:item/redstone_torch"), "energy_blocks")
    val leftNaturalBlocks = createTab(left, TabOverlay.StringOverlay("minecraft:block/flowering_azalea_leaves"), "natural_blocks")
    val leftFunctionalBlocks = createTab(left, TabOverlay.StringOverlay("minecraft:block/furnace"), "functional_blocks")
    val leftMachineryBlocks = createTab(left, TabOverlay.StringOverlay("minecraft:block/crafter"), "machinery_blocks")
    val rightStorageBlocks = createTab(right, TabOverlay.StringOverlay("minecraft:block/barrel"), "storage_blocks")
    val rightToolsAndCombatBlocks = createTab(right, TabOverlay.StringOverlay("minecraft:item/diamond_pickaxe"), "toolsandcombat")
    val rightFoodAndDrinksBlocks = createTab(right, TabOverlay.StringOverlay("minecraft:item/potato"), "foodanddrinks")
    val rightTransportationBlocks = createTab(right, TabOverlay.StringOverlay("minecraft:item/minecart"), "transportation_blocks")
    val rightMiscBlocks = createTab(right, TabOverlay.StringOverlay("minecraft:block/jukebox"), "misc_blocks")
}