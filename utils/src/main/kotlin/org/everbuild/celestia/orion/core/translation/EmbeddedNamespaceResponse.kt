package org.everbuild.celestia.orion.core.translation

import kotlinx.serialization.Serializable

@Serializable
data class EmbeddedNamespaceResponse(val _embedded: NamespacesResponse)
