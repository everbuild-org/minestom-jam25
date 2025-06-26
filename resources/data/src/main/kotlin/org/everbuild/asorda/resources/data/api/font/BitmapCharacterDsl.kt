package org.everbuild.asorda.resources.data.api.font

import org.everbuild.asorda.resources.data.api.InElementDsl
import org.everbuild.asorda.resources.data.api.Track
import org.everbuild.asorda.resources.data.api.meta.FontMetadataEntry
import org.everbuild.asorda.resources.data.api.meta.FontSizeEntry
import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.api.withFileType
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.font.FontProvider

class BitmapCharacterDsl(private val ident: String) {
    private lateinit var texture: Texture
    private var height: Int = 0
    private var ascent: Int = 0

    @InElementDsl
    fun texture(tex: Texture) {
        texture = tex
    }

    @InElementDsl
    fun height(h: Int) {
        height = h
    }

    @InElementDsl
    fun ascent(a: Int) {
        ascent = a
    }

    fun buildFont(track: Track<Char>, resourcePack: ResourcePack, metadata: ResourcePackMetadata): FontProvider {
        texture.buildInto(resourcePack)
        val codepoint = track.getAndIncrement()
        val image = texture.getAsImageSource().load()
        metadata.font[ident] = FontMetadataEntry(
            FontSizeEntry(
                image.height,
                image.width + 1
            ),
            codepoint.toString()
        )
        return FontProvider.bitMap()
            .characters(codepoint.toString())
            .file(texture.key!!.withFileType("png"))
            .height(height)
            .ascent(ascent)
            .build()
    }
}