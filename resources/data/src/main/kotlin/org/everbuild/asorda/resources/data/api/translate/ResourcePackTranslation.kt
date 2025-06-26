package org.everbuild.asorda.resources.data.api.translate

@JvmInline
value class ResourcePackTranslation private constructor(val translations: HashMap<String, String>) {
    companion object {
        fun of(english: String, german: String, french: String) = ResourcePackTranslation(
            hashMapOf(
                "en" to english,
                "de" to german,
                "fr" to french
            )
        )
    }
}
