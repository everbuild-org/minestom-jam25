package org.everbuild.asorda.resources.data.items

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.api.model.ResourcePackModelDsl
import org.everbuild.asorda.resources.data.api.item.ItemResource
import org.everbuild.asorda.resources.data.api.model.ModelResource
import org.everbuild.asorda.resources.data.api.spritesheet.ButtonAutoTileSpriteSheet
import org.everbuild.asorda.resources.data.api.spritesheet.ManualSpriteSheet
import org.everbuild.asorda.resources.data.api.texture.ImageSource
import org.everbuild.asorda.resources.data.api.texture.OverlayImageSource
import org.everbuild.asorda.resources.data.api.texture.ResourceImageSource
import org.everbuild.asorda.resources.data.api.texture.textrendering.RenderedTextImageSource
import org.everbuild.asorda.resources.data.api.translate.ResourcePackTranslation
import org.everbuild.asorda.resources.data.models.UtilityModels
import team.unnamed.creative.base.HeadType
import team.unnamed.creative.base.Vector3Float
import team.unnamed.creative.item.property.ItemBooleanProperty
import team.unnamed.creative.item.special.SpecialRender
import team.unnamed.creative.model.ItemTransform
import team.unnamed.creative.model.Model

object PlayerMenuIcons : ContentList("playermenu") {
    init {
        include(Cosmetics)
        include(Lang)
        include(Ranks)
        include(Shop)
        include(Social)
    }

    val buttonClose = playerMenuButton(UtilityModels.Pairs.right, "close")
    val buttonHome = playerMenuButton(UtilityModels.Pairs.middleLg, "home")
    val buttonProfiles = playerMenuButton(UtilityModels.Pairs.middle, "profiles")
    val buttonSettings = playerMenuButton(UtilityModels.Pairs.middle, "settings")
    val buttonShop = playerMenuButton(UtilityModels.Pairs.middle, "shop")
    val buttonSocial = playerMenuButton(UtilityModels.Pairs.middle, "social")
    val buttonTravel = playerMenuButton(UtilityModels.Pairs.left, "travel") {
        display(
            ItemTransform.Type.GUI to ItemTransform.transform()
                .translation(Vector3Float(0.55f, 0f, 0f))
                .scale(Vector3Float(0.9f, 0.9f, 0.9f))
                .build()
        )
    }

    val hotBarSettingsGear = createItem("settingsGear") {
        model(createModel {
            parent(Model.ITEM_GENERATED)
            textures(
                "layer0" to Texture("playermenu/settings")
            )
            display(
                ItemTransform.Type.GUI to scaleTransform(0.9f),
                ItemTransform.Type.FIRSTPERSON_LEFTHAND to hiddenTransform(),
                ItemTransform.Type.FIRSTPERSON_RIGHTHAND to hiddenTransform(),
                ItemTransform.Type.THIRDPERSON_LEFTHAND to hiddenTransform(),
                ItemTransform.Type.THIRDPERSON_RIGHTHAND to hiddenTransform(),
            )
        })
    }

    private fun playerMenuButton(
        button: Pair<ModelResource, ModelResource>,
        name: String,
        modelSetup: ResourcePackModelDsl.() -> Unit = {}
    ): ItemResource = createItem(name) {
        composite {
            condition(ItemBooleanProperty.customModelData(0)) { active ->
                model(if (active) button.second else button.first)
            }
            model(createModel {
                parent(Model.ITEM_GENERATED)
                display(
                    ItemTransform.Type.GUI to scaleTransform(0.9f)
                )
                textures(
                    "layer0" to Texture("playermenu/$name")
                )
                modelSetup()
            })
        }
    }

    object Cosmetics : ContentList("playermenu/cosmetics") {
        private val emptySlotModel = createModel {
            parent(Model.ITEM_GENERATED)
            textures(
                "layer0" to Texture("playermenu/cosmetics/empty_slot")
            )
            display(
                ItemTransform.Type.GUI to scaleTransform(1.125f)
            )
            guiLight(Model.GuiLight.FRONT)
        }

        val emptySlot = createItem("empty") { model(emptySlotModel) }
    }

    object Lang : ContentList("playermenu/lang") {
        val language = createItem("langitem") {
            condition(ItemBooleanProperty.customModelData(0)) { active ->
                composite {
                    model(if (active) UtilityModels.btnMiddleRoundLgActive else UtilityModels.btnMiddleRoundLg)
                    special("minecraft:item/template_skull", SpecialRender.head(HeadType.PLAYER))
                }
            }
        }

        val languagesMenuItem = createItem("lang") {
            composite {
                model(UtilityModels.btnMiddleRoundLg)
                model(createModel {
                    parent(Model.ITEM_GENERATED)
                    textures(
                        "layer0" to Texture("playermenu/lang/translate")
                    )
                    display(
                        ItemTransform.Type.GUI to scaleTransform(0.9f)
                    )
                })
            }
        }
    }

    object Ranks : ContentList("playermenu/ranks") {
        val infoButton = prototypeItem("button_info")
        val mvp = prototypeItem("mvp_icon")
        val pro = prototypeItem("pro_icon")
        val vip = prototypeItem("vip_icon")

        private fun prototypeItem(name: String) = createItem(name) {
            model(createModel {
                parent(Model.ITEM_GENERATED)
                display(
                    ItemTransform.Type.GUI to scaleTransform(0.9f)
                )
                textures(
                    "layer0" to Texture("playermenu/ranks/$name")
                )
            })
        }
    }

    object Shop : ContentList("playermenu/shop") {
        val cosmeticsButton = createTranslated(
            ResourcePackTranslation.of(
                "Cosmetics",
                "Kosmetika",
                "Cosmétique"
            )
        ) { lang, text ->
            createMultipartItem(
                "cosmetics_$lang", ButtonAutoTileSpriteSheet(
                    OverlayImageSource(
                        ResourceImageSource("utils/multipart_buttons/7x1"),
                        ResourceImageSource("playermenu/shop/menu_cosmetics"),
                        RenderedTextImageSource(
                            127, 19,
                            18, 13,
                            text
                        )
                    )
                )
            )
        }

        val ranksButton = createTranslated(
            ResourcePackTranslation.of(
                "Ranks",
                "Ränge",
                "Rangs"
            )
        ) { lang, text ->
            createMultipartItem(
                "ranks_$lang", ButtonAutoTileSpriteSheet(
                    OverlayImageSource(
                        ResourceImageSource("utils/multipart_buttons/7x1"),
                        ResourceImageSource("playermenu/shop/menu_ranks"),
                        RenderedTextImageSource(
                            127, 19,
                            18, 13,
                            text
                        )
                    )
                )
            )
        }
    }

    object Social : ContentList("playermenu/social") {
        private val sidebarIcons = object : ManualSpriteSheet(
            ResourceImageSource("playermenu/social/sidebar_icons"),
            1,
            3
        ) {
            val account = item(0, 0)
            val friends = item(0, 1)
            val fraction = item(0, 2)
        }

        val buttonAccount = socialSidebarIcons(UtilityModels.VerticalPairs.top, "account", sidebarIcons.account)
        val buttonFriends = socialSidebarIcons(UtilityModels.VerticalPairs.middle, "friends", sidebarIcons.friends)
        val buttonFraction = socialSidebarIcons(UtilityModels.VerticalPairs.bottom, "fraction", sidebarIcons.fraction)

        private fun socialSidebarIcons(
            button: Pair<ModelResource, ModelResource>,
            name: String,
            texture: Texture,
            modelSetup: ResourcePackModelDsl.() -> Unit = {}
        ): ItemResource = createItem(name) {
            composite {
                condition(ItemBooleanProperty.customModelData(0)) { active ->
                    model(if (active) button.second else button.first)
                }
                model(createModel {
                    parent(Model.ITEM_GENERATED)
                    display(
                        ItemTransform.Type.GUI to scaleTransform(0.9f)
                    )
                    textures(
                        "layer0" to texture
                    )
                    modelSetup()
                })
            }
        }
    }
}