package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture

object MenuAndUtilsCharacters : ContentList() {
    private fun whitePixelIcon(id: Int): String {
        return createBitmapCharacter("white_px_$id") {
            texture(Texture("font/utils/white_0"))
            height(8)
            ascent(id)
        }
    }

    private fun menuTexture(ident: String, key: String): String {
        return createBitmapCharacter(ident) {
            texture(Texture("font/menu/$key"))
            height(170)
            ascent(48)
        }
    }

    val whitePx0 = whitePixelIcon(0)
    val whitePx1 = whitePixelIcon(1)
    val whitePx2 = whitePixelIcon(2)
    val whitePx3 = whitePixelIcon(3)
    val whitePx4 = whitePixelIcon(4)
    val whitePx5 = whitePixelIcon(5)
    val whitePx6 = whitePixelIcon(6)
    val whitePx7 = whitePixelIcon(7)

    val menuBlank1 = menuTexture("menu_blank_1", "blank_menu_rows_1")
    val menuBlank2 = menuTexture("menu_blank_2", "blank_menu_rows_2")
    val menuBlank3 = menuTexture("menu_blank_3", "blank_menu_rows_3")
    val menuBlank4 = menuTexture("menu_blank_4", "blank_menu_rows_4")
    val menuBlank5 = menuTexture("menu_blank_5", "blank_menu_rows_5")
    val menuBlank6 = menuTexture("menu_blank_6", "blank_menu_rows_6")

    val menuSciFi1 = menuTexture("menu_scifi_1", "blank_menu_scifi_rows_1")
    val menuSciFi2 = menuTexture("menu_scifi_2", "blank_menu_scifi_rows_2")
    val menuSciFi3 = menuTexture("menu_scifi_3", "blank_menu_scifi_rows_3")
    val menuSciFi4 = menuTexture("menu_scifi_4", "blank_menu_scifi_rows_4")
    val menuSciFi5 = menuTexture("menu_scifi_5", "blank_menu_scifi_rows_5")
    val menuSciFi6 = menuTexture("menu_scifi_6", "blank_menu_scifi_rows_6")

    val menuPlayerMenuBase = menuTexture("menu_playermenu_base", "menu_playermenu_base")
    val menuPlayerMenuSocial = menuTexture("menu_playermenu_social", "menu_playermenu_social")
    val menuPlayerMenuSocialList = menuTexture("menu_playermenu_social_list", "menu_playermenu_social_list")
    val menuTrade = menuTexture("menu_trade", "menu_trade")
    val gameModeMenu = menuTexture("menu_gamemodemenu", "menu_gamemodemenu")

    val textInputAnvil = createBitmapCharacter("text_input_anvil") {
        texture(Texture("font/menu/text_input_anvil"))
        height(256)
        ascent(13)
    }
}