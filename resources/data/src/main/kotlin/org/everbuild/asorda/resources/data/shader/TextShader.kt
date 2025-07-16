package org.everbuild.asorda.resources.data.shader

import net.kyori.adventure.text.format.TextColor

object TextShader {
    private fun getColor(id: Int) = "#FEFE" + id.toString(16).padStart(2, '0')

    val COLOR_TRADE_TITLE: String = getColor(0)
    val RADIAL_MENU_LABEL: TextColor = TextColor.color(254, 254, 1)
    val INVIS: String = getColor(2)

    fun radialMenu(segment: Int, anim: Int): TextColor {
        assert(segment < 5)
        assert(anim < 4)

        val mCol = (segment shl 5) or (anim shl 3)
        return TextColor.color(254, mCol, 1)
    }
}