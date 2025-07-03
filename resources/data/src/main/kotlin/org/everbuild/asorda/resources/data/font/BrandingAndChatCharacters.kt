package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture

object BrandingAndChatCharacters : ContentList() {
    val brandingAbove = createBitmapCharacter("branding_above") {
        texture(Texture("font/branding/above"))
        height(40)
        ascent(35)
    }

    val brandingRocket = createBitmapCharacter("branding_rocket") {
        texture(Texture("font/branding/rocket"))
        height(40)
        ascent(35)
    }

    val brandingServerIcon = createBitmapCharacter("branding_server_icon") {
        texture(Texture("font/branding/server_icon"))
        height(40)
        ascent(35)
    }

    val brandingSide = createBitmapCharacter("branding_side") {
        texture(Texture("font/branding/side"))
        height(50)
        ascent(25)
    }

    val brandingText = createBitmapCharacter("branding_text") {
        texture(Texture("font/branding/text"))
        height(40)
        ascent(20)
    }

    val iconCopy = createBitmapCharacter("icon_copy") {
        texture(Texture("font/icons/copy"))
        height(8)
        ascent(7)
    }

    val iconFloppy = createBitmapCharacter("icon_floppy") {
        texture(Texture("font/icons/floppy-disk"))
        height(8)
        ascent(7)
    }

    val iconRecycle = createBitmapCharacter("icon_recycle") {
        texture(Texture("font/icons/recycle"))
        height(8)
        ascent(7)
    }

    val iconTrash = createBitmapCharacter("icon_trash") {
        texture(Texture("font/icons/trash"))
        height(8)
        ascent(7)
    }

    val iconTeam = createBitmapCharacter("icon_team") {
        texture(Texture("playermenu/travel"))
        height(8)
        ascent(7)
    }

    val cubit = createBitmapCharacter("cubit") {
        texture(Texture("font/icons/cubit"))
        height(7)
        ascent(7)
    }
}