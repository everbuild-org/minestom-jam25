package org.everbuild.celestia.orion.core.translation

data class Language(
    val base: Boolean,
    val flagEmoji: String,
    val id: Int,
    val name: String,
    val originalName: String,
    val tag: String
)