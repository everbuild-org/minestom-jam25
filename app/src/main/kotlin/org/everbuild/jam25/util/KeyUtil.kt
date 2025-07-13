package org.everbuild.jam25.util

import net.kyori.adventure.key.Key

fun String.toJamKey(): Key = Key.key("jam", this)