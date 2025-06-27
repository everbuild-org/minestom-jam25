package org.everbuild.celestia.orion.platform.minestom.initializer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minestom.server.event.player.PlayerSkinInitEvent
import org.everbuild.celestia.orion.core.util.httpClient
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.http4k.core.Method
import org.http4k.core.Request
import org.slf4j.LoggerFactory

fun uuidByUsername(username: String): String {
    val response = httpClient(Request(Method.GET, "https://api.minecraftservices.com/minecraft/profile/lookup/name/$username")).bodyString()
    val jsonElement = Gson().fromJson(response, object : TypeToken<Map<String, String>>() {})
    return jsonElement.getValue("id")
}

fun registerPlayerInitializer() {
    listen<PlayerSkinInitEvent> {
        val skin = SkinCache.getSkin(it.player.username)
        it.skin = skin
    }
}