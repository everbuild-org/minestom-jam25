package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.texture.Texture

object MiniUiCharacters : ContentList("mini_ui") {
    val arrowP1 = miniUi("arrow_p1")
    val arrowP2 = miniUi("arrow_p2")
    val arrowP3 = miniUi("arrow_p3")
    val arrowP4 = miniUi("arrow_p4")
    val cancel = miniUi("cancel")
    val exclamation = miniUi("exclamation")
    val notifyBell = miniUi("notify_bell")
    val notifyClock = miniUi("notify_clock")
    val notifyMailClosed = miniUi("notify_mail_closed")
    val notifyPin = miniUi("notify_pin")
    val notifyPlus = miniUi("notify_plus")
    val notifyUsers = miniUi("notify_users")

    fun miniUi(tex: String) = createBitmapCharacter("miniui_$tex") {
        texture(Texture("font/icons/miniui/mini_ui_$tex"))
        height(7)
        ascent(7)
    }
}