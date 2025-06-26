package org.everbuild.celestia.orion.core.translation

import kotlinx.serialization.Serializable

@Serializable
data class NamespacesResponse(val namespaces: List<NamespaceResponse>)
