package org.everbuild.celestia.orion.core.packs

object GlyphSizes {
    private val entries = javaClass.getResourceAsStream("/glyphs.txt")!!
        .reader()
        .readLines()
        .map { it[0] to it[1].digitToInt() }

    fun findOrNull(char: Char): Int? {
        return entries.firstOrNull { (testChar, _) -> testChar == char }?.second
    }
}