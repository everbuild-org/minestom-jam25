package org.everbuild.celestia.orion.core.translation

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.StyleBuilderApplicable
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.isOnline
import org.everbuild.celestia.orion.core.database.playerdata.t
import org.everbuild.celestia.orion.core.luckperms.takeLuckPermsSnapshot
import org.slf4j.LoggerFactory
import java.time.temporal.TemporalAccessor
import java.util.function.BiFunction
import kotlin.collections.HashMap

class TranslationContext(val player: OrionPlayer, val key: String) {
    private val replacements = HashMap<String, TagResolver>()
    private val stringReplacements = HashMap<String, String>()
    private val rawTextReplacements = HashMap<String, String>()
    private var postProcessor: (Component) -> Component = { it }

    fun replace(key: String, text: String): TranslationContext {
        replacements[key] = Placeholder.unparsed(key, text)
        stringReplacements[key] = text
        return this
    }

    fun replaceRaw(key: String, text: String): TranslationContext {
        rawTextReplacements[key] = text
        return this
    }

    fun replacePlayer(key: String, player: OrionPlayer): TranslationContext {
        if (player.isOnline) {
            replacements[key] = Placeholder.component(key, player.takeLuckPermsSnapshot().asComponent())
        } else {
            replacements[key] = Placeholder.unparsed(key, player.name)
        }
        stringReplacements[key] = player.name
        return this
    }

    fun replaceParsed(key: String, text: String): TranslationContext {
        replacements[key] = Placeholder.parsed(key, text)
        stringReplacements[key] = text
        return this
    }

    fun replace(key: String, player: OrionPlayer): TranslationContext = replacePlayer(key, player)

    fun replaceComponent(key: String, component: Component): TranslationContext {
        replacements[key] = Placeholder.component(key, component)
        return this
    }

    fun replace(key: String, value: Component): TranslationContext = replaceComponent(key, value)

    fun replaceTranslation(key: String, context: (OrionPlayer) -> TranslationContext): TranslationContext {
        replacements[key] = Placeholder.component(key, context(player).c)
        stringReplacements[key] = context(player).s
        return this
    }

    fun replaceTranslation(key: String, txKey: String, block: (TranslationContext) -> Unit = {}): TranslationContext {
        return this.replaceTranslation(key) { it.t(txKey).also(block) }
    }

    fun replaceNumber(key: String, number: Number): TranslationContext {
        replacements[key] = Formatter.number(key, number)
        return this
    }

    fun replace(key: String, value: Number): TranslationContext = replaceNumber(key, value)

    fun replaceDate(key: String, date: TemporalAccessor): TranslationContext {
        replacements[key] = Formatter.date(key, date)
        return this
    }

    fun replace(key: String, value: TemporalAccessor): TranslationContext = replaceDate(key, value)

    fun replaceStyle(key: String, style: StyleBuilderApplicable): TranslationContext {
        replacements[key] = Placeholder.styling(key, style)
        return this
    }

    fun replace(key: String, value: StyleBuilderApplicable): TranslationContext = replaceStyle(key, value)

    fun replaceChoice(key: String, choice: Number): TranslationContext {
        replacements[key] = Formatter.choice(key, choice)
        return this
    }

    fun replaceChoice(key: String, choice: Boolean): TranslationContext {
        replacements[key] = Formatter.booleanChoice(key, choice)
        return this
    }

    fun replaceComplex(key: String, complex: BiFunction<ArgumentQueue, Context, Tag>): TranslationContext {
        replacements[key] = TagResolver.resolver(key, complex)
        return this
    }

    fun postProcess(postProcessor: (Component) -> Component): TranslationContext {
        this.postProcessor = postProcessor
        return this
    }

    private fun translateSource() = Translator.translate(player.locale, key)

    val s: String
        get() {
            if (replacements.keys.size != stringReplacements.keys.size) {
                logger.warn("Only string replacements are supported with the string accessor")
            }

            var translation = translateSource()

            for ((key, value) in rawTextReplacements) {
                translation = translation.replace("{$key}", value)
            }

            for ((key, value) in stringReplacements) {
                translation = translation.replace("{$key}", value)
            }

            return translation
        }
    val c
        get() = MiniMessage
            .miniMessage()
            .deserialize(
                translateSource()
                    .let {
                        var translation = it
                        for ((key, value) in rawTextReplacements) {
                            translation = translation.replace("{$key}", value)
                        }

                        translation
                    }
                    .replace("{", "<")
                    .replace("}", ">"),
                *replacements.values.toTypedArray()
            )

    override fun toString() = s
    fun toComponent() = c

    companion object {
        private val logger = LoggerFactory.getLogger(TranslationContext::class.java)
    }
}
