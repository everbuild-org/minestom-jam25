package org.everbuild.celestia.orion.platform.minestom

import org.everbuild.celestia.orion.core.configuration.ConfigurationNamespace

object MinestomOrionConfig : ConfigurationNamespace("minestom", true) {
    val bind by integer("bind", 25565)
    val pyroscopeServer by string("pyroscope-server", "")
}