package org.everbuild.celestia.orion.core.translation

import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.slf4j.LoggerFactory
import java.util.*

object Translator {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private var languages: List<Language> = listOf(
        Language(
            true,
            "\uD83C\uDDEC\uD83C\uDDE7",
            0,
            "English",
            "English",
            "en"
        ),
        Language(
            true,
            "\uD83C\uDDE9\uD83C\uDDEA",
            0,
            "German",
            "Deutsch",
            "de"
        ),
        Language(
            true,
            "\uD83C\uDDEB\uD83C\uDDF7",
            0,
            "French",
            "Franc,ais",
            "fr"
        )
    )
    private var keysWithPrefix: MutableSet<String> = hashSetOf()

    // Language > key value
    private val keys: HashMap<String, HashMap<String, String>> = HashMap()

    init {
        reloadTranslations()
        logger.info("Languages: ${languages.joinToString(" | ") { "${it.flagEmoji} ${it.name} (${keys[it.tag]?.size ?: 0} keys)" }}")
    }

    fun reloadTranslations() {
        loadTranslations()
    }

    private fun loadTranslations() {
        keys.clear()
        val enBundle = ResourceBundle.getBundle("translations/asorda", Locale.ENGLISH)
        val deBundle = ResourceBundle.getBundle("translations/asorda", Locale.GERMAN)
        val frBundle = ResourceBundle.getBundle("translations/asorda", Locale.FRENCH)
        val en = hashMapOf<String, String>()
        val de = hashMapOf<String, String>()
        val fr = hashMapOf<String, String>()
        for (key in enBundle.keys) {
            val enString = enBundle[key]!!
            val deString = deBundle[key] ?: enString
            val frString = frBundle[key] ?: deString

            en[key] = enString
            de[key] = deString
            fr[key] = frString
        }

        keys["en"] = en
        keys["de"] = de
        keys["fr"] = fr
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

    operator fun ResourceBundle.get(key: String): String? {
        return try {
            getString(key)
        } catch (_: Exception) {
            null
        }
    }
}