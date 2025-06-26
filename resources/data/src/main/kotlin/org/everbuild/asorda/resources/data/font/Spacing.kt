package org.everbuild.asorda.resources.data.font

import org.everbuild.asorda.resources.data.api.meta.FontMetadata
import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.asorda.resources.data.api.Track
import team.unnamed.creative.font.FontProvider

fun createSpacingProvider(range: IntRange, track: Track<Char>, metadata: ResourcePackMetadata): FontProvider {
    var provider = FontProvider.space()
    for (i in range) {
        val codepoint = track.getAndIncrement().toString()
        metadata.font["spacing_$i"] = FontMetadata.space(codepoint, i)
        provider = provider.advance(codepoint, i)
    }

    return provider.build()
}
