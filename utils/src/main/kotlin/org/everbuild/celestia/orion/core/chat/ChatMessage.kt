package org.everbuild.celestia.orion.core.chat

import net.kyori.adventure.text.Component
import org.everbuild.celestia.orion.core.database.playerdata.*
import org.everbuild.celestia.orion.core.luckperms.LuckPermsSnapshot
import org.everbuild.celestia.orion.core.luckperms.takeLuckPermsSnapshot
import org.everbuild.celestia.orion.core.util.globalOrion

sealed interface ChatMessage {
    fun sendTo(player: OrionPlayer, prefix: Component = Component.empty(), suffix: Component = Component.empty())
    fun canDelete(player: OrionPlayer, admin: Boolean): Boolean
    fun asText(): String = ""

    class TextMessage(from: OrionPlayer, private val message: Component, private val text: String, private val targets: List<OrionPlayer>) : ChatMessage {
        private val elevation: Int = getElevation(from)
        private val playerData = from.takeLuckPermsSnapshot()
        private val texture = globalOrion.chatTextureResolver.skinComponent(from)
        override fun sendTo(player: OrionPlayer, prefix: Component, suffix: Component) {
            if (!targets.contains(player)) return
            player.sendMessage(
                prefix.append(player.t("orion.chat.message")
                    .also {
                        it.replace("player", playerData.asComponent())
                        it.replace("message", message)
                        it.replace("texture", texture)
                    }.c
                )
                    .append(suffix)
            )
        }

        override fun canDelete(player: OrionPlayer, admin: Boolean) =
            admin || getElevation(player) >= elevation

        override fun asText(): String = "[${playerData.playerName}] $text"
    }

    class PlayerStateMessage(from: OrionPlayer, private val type: Type, data: LuckPermsSnapshot?) : ChatMessage {
        private val playerData = data ?: from.takeLuckPermsSnapshot()
        private val texture = globalOrion.chatTextureResolver.skinComponent(from)
        override fun sendTo(player: OrionPlayer, prefix: Component, suffix: Component) {
            player.sendTranslated("orion.chat.playerState.${type.name.lowercase()}") {
                it.replace("player", playerData.asComponent())
                it.replace("texture", texture)
            }
        }

        override fun canDelete(player: OrionPlayer, admin: Boolean) = admin

        companion object {
            enum class Type {
                JOIN, QUIT
            }
        }

        override fun asText() = "$type ${playerData.playerName}"
    }

    companion object {
        fun getElevation(player: OrionPlayer): Int = player.takeLuckPermsSnapshot().permissionsWeight

        fun text(from: OrionPlayer, message: Component, messageUnprocessed: String, targets: List<OrionPlayer>): ChatMessage =
            TextMessage(from, message, messageUnprocessed, targets)

        fun join(from: OrionPlayer, data: LuckPermsSnapshot?): ChatMessage =
            PlayerStateMessage(from, PlayerStateMessage.Companion.Type.JOIN, data)

        fun quit(from: OrionPlayer, data: LuckPermsSnapshot?): ChatMessage =
            PlayerStateMessage(from, PlayerStateMessage.Companion.Type.QUIT, data)
    }
}