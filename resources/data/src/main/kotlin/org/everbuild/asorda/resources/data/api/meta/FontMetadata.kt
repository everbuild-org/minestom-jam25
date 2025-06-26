package org.everbuild.asorda.resources.data.api.meta

import kotlinx.serialization.Serializable

@Serializable
data class FontSizeEntry(val width: Int, val height: Int)

@Serializable
data class FontMetadataEntry(val size: FontSizeEntry, val codepoint: String)

@Serializable
data class FontMetadata(val entries: HashMap<String, FontMetadataEntry> = hashMapOf()) {
    operator fun set(name: String, entry: FontMetadataEntry) {
        entries[name] = entry
    }

    companion object {
        fun space(codepoint: String, width: Int) = FontMetadataEntry(
            size = FontSizeEntry(width + 1, 0),
            codepoint = codepoint
        )
    }
}