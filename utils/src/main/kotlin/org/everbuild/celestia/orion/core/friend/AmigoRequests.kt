package org.everbuild.celestia.orion.core.friend

import kotlinx.serialization.json.*
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.friend.model.BooleanResultModel
import org.everbuild.celestia.orion.core.util.httpClient
import org.http4k.core.Method
import org.http4k.core.Request

fun OrionPlayer.getIncomingFriendRequests(): List<OrionPlayer> {
    return Json.decodeFromString<Set<Int>>(
        httpClient(
            Request(
                Method.GET,
                Amigo.endpoint("/player/${id}/getIncomingRequests")
            )
        ).bodyString()
    )
        .toList()
        .mapNotNull { PlayerLoader.load(it) }
}

fun OrionPlayer.getOutgoingFriendRequests(): List<OrionPlayer> {
    return Json.decodeFromString<Set<Int>>(
        httpClient(
            Request(
                Method.GET,
                Amigo.endpoint("/player/${id}/getOutgoingRequests")
            )
        ).bodyString()
    )
        .toList()
        .mapNotNull { PlayerLoader.load(it) }
}

fun OrionPlayer.addFriendRequest(friend: OrionPlayer) {
    httpClient(Request(Method.POST, Amigo.endpoint("/player/${id}/sendRequest/${friend.id}")))
}

fun OrionPlayer.removeFriendRequest(friend: OrionPlayer) {
    httpClient(Request(Method.DELETE, Amigo.endpoint("/player/${id}/removeRequest/${friend.id}")))
}

fun OrionPlayer.acceptFriendRequest(friend: OrionPlayer) {
    httpClient(Request(Method.POST, Amigo.endpoint("/player/${id}/acceptRequest/${friend.id}")))
}

fun OrionPlayer.denyFriendRequest(friend: OrionPlayer) {
    httpClient(Request(Method.POST, Amigo.endpoint("/player/${id}/denyRequest/${friend.id}")))
}

fun OrionPlayer.isFriendRequestExpired(friend: OrionPlayer): Boolean {
    val result = httpClient(Request(Method.GET, Amigo.endpoint("/player/${id}/isExpired/${friend.id}"))).bodyString()
    return Json.decodeFromString<BooleanResultModel>(result).result
}