package org.everbuild.celestia.orion.platform.minestom.api.utils

import net.kyori.adventure.text.logger.slf4j.ComponentLogger

val Any.logger: ComponentLogger get() = ComponentLogger.logger(this::class.java)