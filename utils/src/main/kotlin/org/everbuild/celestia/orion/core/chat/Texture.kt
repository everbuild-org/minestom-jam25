package org.everbuild.celestia.orion.core.chat

import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap

data class Textures(
    val textures: HashMap<String, Texture>
) {
    companion object {
        fun de(b64: String): Textures? {
            val json = String(Base64.getDecoder().decode(b64))
            return try {
                Gson().fromJson(json, Textures::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class Texture(
    val url: String,
)