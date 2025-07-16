package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.resource
import team.unnamed.creative.ResourcePack

private fun ResourcePack.coreShader(id: String) {
    unknownFile("assets/minecraft/shaders/core/$id", resource("shaders/$id"))
}

suspend fun addShaders(pack: ResourcePack) {
    pack.coreShader("rendertype_text.vsh")
    pack.coreShader("rendertype_text.fsh")
    pack.coreShader("rendertype_text_background.vsh")
    pack.coreShader("rendertype_text_background.fsh")
}