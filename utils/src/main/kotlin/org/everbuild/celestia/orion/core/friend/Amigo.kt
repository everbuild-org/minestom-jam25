package org.everbuild.celestia.orion.core.friend

import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig

object Amigo {
    fun endpoint(endpoint: String) = "http://" + SharedPropertyConfig.amigoServer + endpoint
}