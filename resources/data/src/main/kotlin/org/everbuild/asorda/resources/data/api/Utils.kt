package org.everbuild.asorda.resources.data.api

import net.kyori.adventure.key.Key

fun String.maybeAsordaKey(): Key {
    if (this.contains(":")) return Key.key(this)
    return asordaKey(this)
}

fun String.pathKey(kind: String = "auto", ft: String = ""): Key = Key.key("asorda:$kind/$this$ft")