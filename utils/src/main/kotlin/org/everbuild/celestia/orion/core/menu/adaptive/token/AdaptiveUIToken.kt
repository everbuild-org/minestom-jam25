package org.everbuild.celestia.orion.core.menu.adaptive.token

interface AdaptiveUIToken {
    fun calculateWidth(): Int
    fun render(): String
}