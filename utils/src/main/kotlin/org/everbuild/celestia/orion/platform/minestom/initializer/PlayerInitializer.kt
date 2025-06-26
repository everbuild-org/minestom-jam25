package org.everbuild.celestia.orion.platform.minestom.initializer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minestom.server.event.player.PlayerSkinInitEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import org.everbuild.celestia.orion.platform.minestom.util.listen
import org.slf4j.LoggerFactory

private val logger =
    LoggerFactory.getLogger("org.everbuild.celestia.orion.platform.minestom.initializer.PlayerInitializer")
private val client = OkHttpClient()

fun uuidByUsername(username: String): String {
    val request =
        Request.Builder().url("https://api.minecraftservices.com/minecraft/profile/lookup/name/$username").build()
    val response = client.newCall(request).execute().body()!!.string()
    val jsonElement = Gson().fromJson(response, object : TypeToken<Map<String, String>>() {})
    return jsonElement.getValue("id")
}

fun registerPlayerInitializer() {
    listen<PlayerSkinInitEvent> {
        val skin = SkinCache.getSkin(it.player.username)
        it.skin = skin
    }
}