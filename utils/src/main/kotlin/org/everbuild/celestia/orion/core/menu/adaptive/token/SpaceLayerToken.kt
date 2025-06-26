package org.everbuild.celestia.orion.core.menu.adaptive.token

import kotlin.math.absoluteValue
import kotlin.math.sign
import org.everbuild.celestia.orion.core.packs.OrionPacks

class SpaceLayerToken(private val width: Int) : AdaptiveUIToken {
    private val character = segmentWidth(width.absoluteValue, width.sign)

    override fun calculateWidth(): Int = width
    override fun render(): String = character

    companion object {
        fun segmentWidth(width: Int, sign: Int): String {
            val spacings = mutableListOf<Int>()
            if (width == 160 || width <= 159) {
                spacings.add(width * sign)
            } else {
                var widthLeft = width

                while (widthLeft > 0) {
                    val reducible = widthLeft.coerceAtMost(160)
                    spacings += reducible * sign
                    widthLeft -= reducible
                }
            }
            return spacings.filterNot { it == 0 }.joinToString("") { OrionPacks.getCharacterCodepoint("spacing_$it") }
        }
    }
}