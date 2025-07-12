package org.everbuild.jam25

import org.everbuild.celestia.orion.core.configuration.ConfigurationNamespace

object JamConfig : ConfigurationNamespace("jam") {
    val velocityEnable by boolean("velocity.enable", false)
    val velocitySecret by string("velocity.secret", "secret")
    val resourcePackUri by string("resourcepack.host", "localhost")
}