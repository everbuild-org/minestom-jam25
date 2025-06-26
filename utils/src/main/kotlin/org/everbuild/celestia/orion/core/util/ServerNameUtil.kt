package org.everbuild.celestia.orion.core.util

val serverName: String get() = System.getenv("HOSTNAME") ?: "localhost"