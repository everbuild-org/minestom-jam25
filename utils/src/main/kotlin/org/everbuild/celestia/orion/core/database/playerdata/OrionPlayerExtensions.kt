package org.everbuild.celestia.orion.core.database.playerdata

import net.kyori.adventure.text.Component
import org.everbuild.celestia.orion.core.remote.asRemote
import org.everbuild.celestia.orion.core.translation.TranslationContext
import org.everbuild.celestia.orion.core.util.globalOrion

fun OrionPlayer.t(key: String): TranslationContext = TranslationContext(this, key)
fun OrionPlayer.c(key: String): Component = t(key).c

fun OrionPlayer.sendMessage(component: Component) {
    if (this.isOnline) {
        globalOrion.platform.getAdventureAudience(this).sendMessage(component)
    } else {
        this.asRemote().sendMessage(component)
    }
}

fun OrionPlayer.sendTranslated(key: String, resolver: (TranslationContext) -> Unit = {}) {
    sendMessage(t(key).also(resolver).c)
}

val OrionPlayer.isOnline get() = globalOrion.platform.isOnline(this)