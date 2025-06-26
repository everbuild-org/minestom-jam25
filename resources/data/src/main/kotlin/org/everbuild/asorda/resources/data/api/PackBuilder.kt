package org.everbuild.asorda.resources.data.api

import team.unnamed.creative.ResourcePack

object PackBuilder {
    private val instances = mutableSetOf<BuildableResource>()

    fun build(pack: ResourcePack, resource: BuildableResource) {
        if (instances.contains(resource)) return

        instances.add(resource)
        resource.buildInto(pack)
    }

    fun reset() {
        instances.clear()
    }
}