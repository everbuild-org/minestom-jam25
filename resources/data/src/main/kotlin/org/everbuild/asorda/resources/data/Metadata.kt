package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.resource
import team.unnamed.creative.ResourcePack

suspend fun addMetadata(pack: ResourcePack) {
    pack.packMeta(63, "asorda.net - Resources")
    pack.icon(resource("pack.png"))
}