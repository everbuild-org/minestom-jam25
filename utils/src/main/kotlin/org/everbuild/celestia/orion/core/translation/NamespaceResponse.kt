package org.everbuild.celestia.orion.core.translation

import kotlinx.serialization.Serializable

@Serializable
data class NamespaceResponse(val id: Int, val name: String)
