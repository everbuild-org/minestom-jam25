package org.everbuild.asorda.resources.data.api

import team.unnamed.creative.ResourcePack

interface BuildableResource {
    fun buildInto(pack: ResourcePack)
}