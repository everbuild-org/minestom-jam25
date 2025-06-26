package org.everbuild.celestia.orion.platform.minestom.api.command

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.suggestion.Suggestion
import net.minestom.server.command.builder.suggestion.SuggestionCallback
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import org.everbuild.celestia.orion.core.database.database
import org.everbuild.celestia.orion.core.database.playerdata.orionPlayers
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.ktorm.entity.mapColumns

class OrionPlayerArgument(id: String) : ArgumentWord(id) {
    init {
        setSuggestionCallback(OrionPlayerSuggestionCallback())
    }

    companion object {
        class OrionPlayerSuggestionCallback : SuggestionCallback {
            private var allPlayers = database.orionPlayers.mapColumns { it.name }.mapNotNull { it }
            private var lastRefresh = System.currentTimeMillis()

            override fun apply(sender: CommandSender, context: CommandContext, suggestion: Suggestion) {
                refresh()

                val onlinePlayers = Mc.connection.onlinePlayers.map { it.username }
                val allPlayersButOnlineOnTop =
                    allPlayers.filter { it !in onlinePlayers } + allPlayers.filter { it in onlinePlayers }

                allPlayersButOnlineOnTop.forEach {
                    suggestion.addEntry(SuggestionEntry(it))
                }
            }

            private fun refresh() {
                if (System.currentTimeMillis() - lastRefresh > 60000) {
                    allPlayers = database.orionPlayers.mapColumns { it.name }.mapNotNull { it }
                    lastRefresh = System.currentTimeMillis()
                }
            }
        }
    }
}