package org.everbuild.celestia.orion.core.util

fun String.ensureNoSlash(): String {
    if (!this.endsWith("/")) return this
    return this.removeSuffix("/")
}