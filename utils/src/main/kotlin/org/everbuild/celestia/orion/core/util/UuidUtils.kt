package org.everbuild.celestia.orion.core.util

import java.util.*

fun String.asUuid() = UUID.fromString(this)!!