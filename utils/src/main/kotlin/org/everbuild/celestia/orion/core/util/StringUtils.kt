package org.everbuild.celestia.orion.core.util

fun String.replacementsFrom(map: Map<String, String>): String {
    var result = this
    map.forEach { (key, value) ->
        result = result.replace("{$key}", value)
    }
    return result
}