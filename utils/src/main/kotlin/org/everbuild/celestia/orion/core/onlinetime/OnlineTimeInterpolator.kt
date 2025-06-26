package org.everbuild.celestia.orion.core.onlinetime

import org.everbuild.celestia.orion.core.database.database
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.ktorm.dsl.eq
import org.ktorm.entity.first
import kotlin.math.floor

object OnlineTimeInterpolator {
    private val currentInterpolationOnlineTime = HashMap<Int, Long>() /* Millis */
    private val lastSaveMillis = HashMap<Int, Long>()
    operator fun get(player: OrionPlayer): OnlineTime {
        if (!currentInterpolationOnlineTime.containsKey(player.id)) {
            return OnlineTime(0.0, 0.0)
        }
        if (!lastSaveMillis.containsKey(player.id)) {
            lastSaveMillis[player.id] = System.currentTimeMillis()
        }
        val currentInterpolationTime =
            currentInterpolationOnlineTime[player.id]!! + System.currentTimeMillis() - lastSaveMillis[player.id]!!

        lastSaveMillis[player.id] = System.currentTimeMillis()
        currentInterpolationOnlineTime[player.id] = currentInterpolationTime

        return OnlineTime(
            floor((currentInterpolationTime / 60000f / 60f).toDouble()),
            floor((currentInterpolationTime / 60000f % 60).toDouble())
        )
    }

    fun registerJoin(player: OrionPlayer) {
            currentInterpolationOnlineTime.remove(player.id)
            lastSaveMillis.remove(player.id)
            try {
                currentInterpolationOnlineTime[player.id] =
                    database.onlineTime.first{ it.playerId eq player.id }.onlineTime
            } catch (e: Exception) {
                currentInterpolationOnlineTime[player.id] = 0L
            }
    }
}