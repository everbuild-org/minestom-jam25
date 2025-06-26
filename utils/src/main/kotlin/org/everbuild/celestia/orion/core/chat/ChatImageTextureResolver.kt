package org.everbuild.celestia.orion.core.chat

import net.kyori.adventure.text.Component
import org.everbuild.celestia.orion.core.OrionCore
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer

class ChatImageTextureResolver(private val orionCore: OrionCore<*>) {
    private val cache = mutableMapOf<String, CachedChatTexture>()

    fun getSkin(orionPlayer: OrionPlayer): CachedChatTexture? {
        return cache.getOrPut(orionPlayer.uuid) { CachedChatTexture(
            orionCore.platform.texture(orionPlayer) ?: return null,
            listOf(
                ChatTexViewBox(8, 8),
                ChatTexViewBox(40, 8),
            )
        ) }
    }

    fun skinComponent(orionPlayer: OrionPlayer): Component {
        val skin = getSkin(orionPlayer) ?: return Component.empty()
        return skin.chatText
    }
}