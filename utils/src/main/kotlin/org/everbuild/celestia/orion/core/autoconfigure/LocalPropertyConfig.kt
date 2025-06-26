package org.everbuild.celestia.orion.core.autoconfigure

import org.everbuild.celestia.orion.core.configuration.ConfigurationNamespace

object LocalPropertyConfig : ConfigurationNamespace("locals", true) {
    val kubernetesNamespace by string("kubernetes.namespace", "celestis")
    val serverName by string("server.name", System.getenv("HOSTNAME") ?: "unknown-dev")
}