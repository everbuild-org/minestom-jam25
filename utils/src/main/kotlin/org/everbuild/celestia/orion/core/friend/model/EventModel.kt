package org.everbuild.celestia.orion.core.friend.model

import kotlinx.serialization.Serializable
import org.everbuild.celestia.orion.core.friend.AmigoEventType

@Serializable
data class EventModel(
    val id: Int,
    val playerId: Int,
    val type: AmigoEventType,
    val otherPlayer: Int
)
