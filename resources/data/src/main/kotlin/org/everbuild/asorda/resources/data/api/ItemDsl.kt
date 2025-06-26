package org.everbuild.asorda.resources.data.api

import team.unnamed.creative.ResourcePack

@InElementDsl
fun addContent(pack: ResourcePack, list: ContentList) {
    PackBuilder.build(pack, list)
}
