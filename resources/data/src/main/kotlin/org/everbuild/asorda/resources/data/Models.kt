package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.addContent
import org.everbuild.asorda.resources.data.models.UtilityModels
import team.unnamed.creative.ResourcePack

suspend fun addModels(pack: ResourcePack) {
    addContent(pack, UtilityModels)
}