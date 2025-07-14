package org.everbuild.asorda.resources.data

import kotlin.reflect.KSuspendFunction1
import kotlin.reflect.KSuspendFunction2
import kotlin.reflect.full.callSuspend
import org.everbuild.asorda.resources.data.api.PackBuilder
import org.everbuild.asorda.resources.data.api.lockfile.LockfileService
import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.asorda.resources.data.api.resource
import team.unnamed.creative.ResourcePack

object ResourceGenerator {
    suspend fun regenerate(step: (String) -> Unit): Pair<ResourcePack, ResourcePackMetadata> {
        PackBuilder.reset()

        val resourcePack = ResourcePack.resourcePack()
        val metadata = ResourcePackMetadata()

        suspend fun apply(block: KSuspendFunction1<ResourcePack, Unit>) {
            step(" * ${block.name}()")
            block.callSuspend(resourcePack)
        }

        suspend fun apply(block: KSuspendFunction2<ResourcePack, ResourcePackMetadata, Unit>) {
            step(" * ${block.name}()")
            block.callSuspend(resourcePack, metadata)
        }

        apply(::addMetadata)
        apply(::addLocales)
        apply(::addContainerUtils)
        apply(::addFontData)
        apply(::addItems)
        apply(::addModels)
        apply(::addBlocks)
        apply(::addShaders)

        resourcePack.unknownFile("assets/minecraft/atlases/blocks.json", resource("blocksAtlas.json"))

        LockfileService.save()
        return Pair(resourcePack, metadata)
    }
}