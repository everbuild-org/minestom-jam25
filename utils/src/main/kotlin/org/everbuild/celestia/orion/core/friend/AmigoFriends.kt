package org.everbuild.celestia.orion.core.friend

import kotlinx.serialization.json.Json
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.core.friend.model.BooleanResultModel
import org.everbuild.celestia.orion.core.util.httpClient
import org.http4k.core.Method
import org.http4k.core.Request

fun OrionPlayer.getFriends(): List<OrionPlayer> {
    return Json.decodeFromString<Set<Int>>(
        httpClient(
            Request(
                Method.GET,
                Amigo.endpoint("/player/${id}/friends")
            )
        ).bodyString()
    ).toList().mapNotNull { PlayerLoader.load(it) }
}

fun OrionPlayer.addFriend(friend: OrionPlayer) {
    httpClient(Request(Method.POST, Amigo.endpoint("/player/${id}/addFriend/${friend.id}")))
}

fun OrionPlayer.removeFriend(friend: OrionPlayer) {
    httpClient(Request(Method.DELETE, Amigo.endpoint("/player/${id}/removeFriend/${friend.id}")))
}

fun OrionPlayer.isFriend(friend: OrionPlayer): Boolean {
    return getFriends().any { it.id == friend.id }
}

fun OrionPlayer.blockPlayer(toBlock: OrionPlayer) {
    httpClient(Request(Method.POST, Amigo.endpoint("/player/${id}/block/${toBlock.id}")))
}

fun OrionPlayer.unblockPlayer(toUnblock: OrionPlayer) {
    httpClient(Request(Method.POST, Amigo.endpoint("/player/${id}/unblock/${toUnblock.id}")))
}

fun OrionPlayer.isBlocked(toBlock: OrionPlayer): Boolean {
    val result = httpClient(Request(Method.GET, Amigo.endpoint("/player/${id}/isBlocking/${toBlock.id}"))).bodyString()
    return Json.decodeFromString<BooleanResultModel>(result).result
}

fun OrionPlayer.disallowsRequests(player: OrionPlayer): Boolean {
    val result = httpClient(Request(Method.GET, Amigo.endpoint("/player/${id}/disallowRequests"))).bodyString()
    return Json.decodeFromString<BooleanResultModel>(result).result
}