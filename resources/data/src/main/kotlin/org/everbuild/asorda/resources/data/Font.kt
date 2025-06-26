package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.asorda.resources.data.api.Track
import org.everbuild.asorda.resources.data.api.minecraftKey
import org.everbuild.asorda.resources.data.font.BrandingAndChatCharacters
import org.everbuild.asorda.resources.data.font.MenuAndUtilsCharacters
import org.everbuild.asorda.resources.data.font.createSpacingProvider
import org.everbuild.asorda.resources.data.font.InteractionMenu
import org.everbuild.asorda.resources.data.font.MiniUiCharacters
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.font.Font

suspend fun addFontData(pack: ResourcePack, metadata: ResourcePackMetadata) {
    val codepointTrack = Track.char('\ue000')

    val elements = listOf(
        createSpacingProvider(-160..160, codepointTrack, metadata),
        *BrandingAndChatCharacters.getSegments(pack, codepointTrack, metadata).toTypedArray(),
        *MenuAndUtilsCharacters.getSegments(pack, codepointTrack, metadata).toTypedArray(),
        *InteractionMenu.getSegments(pack, codepointTrack, metadata).toTypedArray(),
        *MiniUiCharacters.getSegments(pack, codepointTrack, metadata).toTypedArray(),
    )

    pack.font(Font.font(minecraftKey("default"), elements))
}
