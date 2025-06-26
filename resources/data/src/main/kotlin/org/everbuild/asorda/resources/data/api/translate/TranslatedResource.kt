package org.everbuild.asorda.resources.data.api.translate

class TranslatedResource<T>(private val translations: HashMap<String, T>) {
    fun forLanguage(language: String): T = translations[language] ?: translations["en"]!!
}
