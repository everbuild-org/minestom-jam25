package org.everbuild.celestia.orion.core.translation

import com.google.gson.Gson
import kotlinx.serialization.json.Json
import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.util.ensureNoSlash
import org.everbuild.celestia.orion.core.util.httpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.slf4j.LoggerFactory

object Translator {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private var languages: List<Language>
    private var namespaces: Set<String> = hashSetOf()
    private var keysWithPrefix: MutableSet<String> = hashSetOf()

    // Language > key value
    private val keys: HashMap<String, HashMap<String, String>> = HashMap()

    init {
        languages = getLanguages().languages
        reloadTranslations()
        logger.info("Languages: ${languages.joinToString(" | ") { "${it.flagEmoji} ${it.name} (${keys[it.tag]?.size ?: 0} keys)" }}")
    }

    private fun httpGet(path: String): String? {
        val response = httpClient(
            Request(Method.GET, "${SharedPropertyConfig.tolgeeHost.ensureNoSlash()}/v2/projects/${path}")
                .header("X-API-Key", SharedPropertyConfig.tolgeeProjectKey)
                .header("Accept", "application/json"),
        )
        if (!response.status.successful) {
            return null
        }

        return response.bodyString()
    }

    fun reloadTranslations() {
        namespaces = getNamespaces()
        loadTranslations()
    }

    private fun getLanguages(): LanguageResponse {
        val json = httpGet("languages") ?: run {
            logger.warn("Could not load languages")
            return LanguageResponse(listOf())
        }

        return Gson().fromJson(json, PagedLanguageResponse::class.java)._embedded
    }

    private fun getTranslations(namespace: String, nextCursor: String?): PagedKeysResponse? {
        val json = httpGet(
            "translations?filterNamespace=$namespace${languages.joinToString("") { "&languages=" + it.tag }}".let {
                if (nextCursor != null) "$it&cursor=$nextCursor"
                else it
            }
        ) ?: return null
        return Gson().fromJson(json, PagedKeysResponse::class.java)
    }

    private fun getAllTranslations(namespace: String): List<TranslationKey> {
        var next: String? = null
        val translations = mutableListOf<TranslationKey>()
        do {
            val response = getTranslations(namespace, next) ?: break
            next = response.nextCursor
            translations.addAll(response._embedded?.keys ?: break)
        } while (next != null)

        return translations
    }

    private fun getNamespaces(): Set<String> {
        val json = httpGet("used-namespaces")
        return Json.decodeFromString<EmbeddedNamespaceResponse>(json!!)
            ._embedded
            .namespaces
            .map { it.name }
            .toSet()
    }

    private fun loadTranslations() {
        keys.clear()
        for (namespace in namespaces) {
            getAllTranslations(namespace)
                .forEach { tx ->
                    val fullyQualifiedKeyName = "${tx.keyNamespace}.${tx.keyName}"

                    if (tx.keyTags.any { it.name == "prefix" })
                        keysWithPrefix.add(fullyQualifiedKeyName)

                    tx.translations.forEach {
                        keys.getOrPut(it.key) { HashMap() }[fullyQualifiedKeyName] = it.value.text
                    }
                }
        }
    }

    private fun getLanguage(source: String, key: String): String {
        if (languages.none { it.tag == source }) return "en"
        if ((keys[source] ?: return "en")[key] == null) return "en"
        return source
    }

    fun translate(language: String, key: String): String {
        val prefix = if (keysWithPrefix.contains(key)) {
            SharedPropertyConfig.globalPrefix.replace(
                "{authenticity}",
                translate(language, "orion.system.authenticity")
            )
        } else ""

        return (prefix + (keys[getLanguage(language, key)]?.get(key) ?: key)).let {
            var value = it
            for ((k, v) in OrionPacks.data.font.entries) {
                value = value.replace(":$k:", v.codepoint)
            }
            value
        }
    }
}