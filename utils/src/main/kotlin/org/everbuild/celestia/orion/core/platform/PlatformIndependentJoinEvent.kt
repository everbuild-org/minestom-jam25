package org.everbuild.celestia.orion.core.platform

import java.util.UUID

data class PlatformIndependentJoinEvent(
    val playerName: String,
    val playerUUID: UUID,
    val playerLocale: String
)
