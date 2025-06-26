package org.everbuild.asorda.resources.data.api.texture

import java.io.OutputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.pathKey
import org.everbuild.asorda.resources.data.api.resource
import org.everbuild.asorda.resources.data.api.withFileType
import team.unnamed.creative.ResourcePack

class AnimatedTexture(
    val path: String,
    override var key: Key? = null,
    private val configure: AnimatedTextureDsl.() -> Unit
) : Texture {
    init {
        if (key == null) key = path.pathKey("item/auto")
    }

    override fun getAsImageSource(): ImageSource {
        return ResourceImageSource(path)
    }

    override fun buildInto(pack: ResourcePack) {
        val dsl = AnimatedTextureDsl()
        dsl.configure()
        val mcMeta = McMeta(dsl)
        val json = jsonSerializer.encodeToString(mcMeta)

        pack.texture(key!!.withFileType("png"), resource("$path.png"))
        pack.unknownFile("assets/${key!!.namespace()}/textures/${key!!.value()}.png.mcmeta") { it.write(json.toByteArray()) }
    }

    companion object {
        private val jsonSerializer = Json {
            explicitNulls = false
            prettyPrint = true
        }
    }
}

@Serializable
data class AnimatedTextureDsl(
    @SerialName("frametime") var frameTime: Int = 300,
    var interpolate: Boolean = true,
    var frames: List<Int>? = null
)

@Serializable
data class McMeta(val animation: AnimatedTextureDsl)