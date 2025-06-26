package org.everbuild.asorda.resources.data.api.meta

import kotlinx.serialization.Serializable

@Serializable
data class ResourcePackMetadata(
    val font: FontMetadata = FontMetadata()
)
