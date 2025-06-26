package org.everbuild.asorda.resources.data.api.texture

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.BuildableResource
import org.everbuild.asorda.resources.data.api.pathKey
import org.everbuild.asorda.resources.data.api.resource
import org.everbuild.asorda.resources.data.api.withFileType
import team.unnamed.creative.ResourcePack

interface Texture : BuildableResource {
    val key: Key?

    fun getAsImageSource(): ImageSource

    companion object {
        class CopyingTexture(val path: String, override var key: Key? = null) : Texture {
            init {
                if (key == null) key = path.pathKey("item/auto")
            }

            override fun getAsImageSource(): ImageSource {
                return ResourceImageSource(path)
            }

            override fun buildInto(pack: ResourcePack) {
                pack.texture(key!!.withFileType("png"), resource("$path.png"))
            }
        }

        operator fun invoke(path: String, key: Key? = null): Texture = CopyingTexture(path, key)
    }
}