package org.everbuild.celestia.orion.core.friend

import kotlinx.serialization.json.*
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.friend.model.EventModel
import org.everbuild.celestia.orion.core.util.httpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(AmigoEvent::class.java)

enum class AmigoEventType {
    NEW_REQUEST,
    ACCEPTED,
    DENIED,
    EXPIRED,
    REMOVED
}

data class AmigoEvent(
    val id: Int,
    val player: OrionPlayer,
    val type: AmigoEventType,
    val friend: OrionPlayer,
) {
    constructor(eventModel: EventModel) : this(
        eventModel.id,
        PlayerLoader.load(eventModel.playerId)!!,
        eventModel.type,
        PlayerLoader.load(eventModel.otherPlayer)!!
    )
}

fun Amigo.allEvents(): List<AmigoEvent> {
    val result = httpClient(Request(Method.GET, endpoint("/events")))
    if (result.status.code != 200) {
        logger.warn("Can't query amigo, is the api down?")
        return emptyList()
    }
    return Json.decodeFromString<List<AmigoEvent>>(result.bodyString())
}

fun <T, R> List<T>.tryMapOrDiscard(transform: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    this.forEach {
        try {
            result.add(transform(it))
        } catch (e: Exception) {
            // Discard
            e.printStackTrace()
        }
    }
    return result
}