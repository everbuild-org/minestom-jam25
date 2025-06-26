package org.everbuild.asorda.resources.data.api.font

import org.everbuild.asorda.resources.data.api.Track
import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.font.FontProvider

interface BuildableFontSegment {
    fun getSegments(resourcePack: ResourcePack, track: Track<Char>, metadata: ResourcePackMetadata): Collection<FontProvider>
}