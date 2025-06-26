package org.everbuild.asorda.resources.data.api

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.block.AbstractResourcePackBlock
import org.everbuild.asorda.resources.data.api.item.ResourcePackItemDsl
import org.everbuild.asorda.resources.data.api.model.ResourcePackModelDsl
import org.everbuild.asorda.resources.data.api.block.BlockResource
import org.everbuild.asorda.resources.data.api.font.BitmapCharacterDsl
import org.everbuild.asorda.resources.data.api.font.BuildableFontSegment
import org.everbuild.asorda.resources.data.api.item.ItemResource
import org.everbuild.asorda.resources.data.api.meta.ResourcePackMetadata
import org.everbuild.asorda.resources.data.api.model.ModelResource
import org.everbuild.asorda.resources.data.api.multipart.MultipartItemResource
import org.everbuild.asorda.resources.data.api.spritesheet.SpriteSheet
import org.everbuild.asorda.resources.data.api.translate.ResourcePackTranslation
import org.everbuild.asorda.resources.data.api.translate.TranslatedResource
import org.everbuild.asorda.resources.data.items.SystemIcons
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.font.FontProvider

abstract class ContentList(private var namespace: String = "") : BuildableResource, BuildableFontSegment {
    private val buildables = mutableListOf<BuildableResource>()
    private val copyingModel = mutableListOf<Pair<Key, String>>()
    private val fontSegments = mutableListOf<BitmapCharacterDsl>()
    private var nexId = 0

    init {
        if (namespace == "") namespace = this::class.simpleName!!
    }

    private fun nextIdempotentName(): String = "${namespace}/element_${nexId++}".lowercase()
    private fun nextIdempotentName(descriptor: String): String = "${namespace}/${descriptor}".lowercase()

    @InElementDsl
    fun createItemWithKey(key: String, block: ResourcePackItemDsl.() -> Unit): ItemResource {
        val dsl = ResourcePackItemDsl(key.maybeAsordaKey())
        dsl.block()
        buildables.add(dsl)
        return ItemResource(dsl.parent, dsl.id)
    }

    @InElementDsl
    fun createItem(descriptor: String, block: ResourcePackItemDsl.() -> Unit): ItemResource {
        return createItemWithKey(nextIdempotentName(descriptor).pathKey().asString(), block)
    }

    @InElementDsl
    fun includeModel(key: String, path: String): ModelResource {
        val modelKey = key.maybeAsordaKey()
        copyingModel.add(Pair(modelKey, path))
        return ModelResource(modelKey)
    }

    @InElementDsl
    fun includeModel(path: String): ModelResource {
        return includeModel(path.pathKey().asString(), path)
    }

    @InElementDsl
    fun createModel(key: String, block: ResourcePackModelDsl.() -> Unit): ModelResource {
        val modelKey = key.maybeAsordaKey()
        val dsl = ResourcePackModelDsl(modelKey)
        dsl.block()
        buildables.add(dsl)
        return ModelResource(modelKey)
    }

    @InElementDsl
    fun createModel(block: ResourcePackModelDsl.() -> Unit): ModelResource {
        return createModel(nextIdempotentName().pathKey().asString(), block)
    }

    @InElementDsl
    fun <T> createBlock(block: T): BlockResource where T : BlockResource, T : BuildableResource {
        buildables.add(block)
        return block
    }

    @InElementDsl
    fun createBitmapCharacter(ident: String, block: BitmapCharacterDsl.() -> Unit): String {
        val dsl = BitmapCharacterDsl(ident)
        dsl.block()
        fontSegments.add(dsl)
        return ident
    }

    @InElementDsl
    fun <T> createTranslated(
        text: ResourcePackTranslation,
        block: (locale: String, text: String) -> T
    ): TranslatedResource<T> {
        val items = hashMapOf<String, T>()
        text.translations.forEach { (locale, translation) ->
            items[locale] = block(locale, translation)
        }
        return TranslatedResource(items)
    }

    @InElementDsl
    fun createMultipartItem(descriptor: String, spriteSheet: SpriteSheet): MultipartItemResource {
        val items = mutableListOf<ItemResource>()
        var width = 0
        var height = 0
        spriteSheet.forEach { x, y, value ->
            if (x > width) width = x
            if (y > height) height = y
            items.add(createItem("${descriptor}_${x}_${y}") {
                model(createModel {
                    parent(SystemIcons.buttonModel)
                    textures(
                        "layer0" to value
                    )
                })
            })
        }

        return MultipartItemResource(items, width + 1, height + 1)
    }

    @InElementDsl
    fun <T : BuildableResource> include(resource: T): T = resource.also { buildables.add(it) }

    private fun buildModel(pack: ResourcePack, key: Key, path: String) {
        pack.unknownFile("assets/${key.namespace()}/models/${key.value()}.json", resource("$path.json"))
    }

    override fun getSegments(resourcePack: ResourcePack, track: Track<Char>, metadata: ResourcePackMetadata): Collection<FontProvider> {
        return fontSegments.map { it.buildFont(track, resourcePack, metadata) }
    }

    override fun buildInto(pack: ResourcePack) {
        copyingModel.forEach { buildModel(pack, it.first, it.second) }
        buildables.forEach { PackBuilder.build(pack, it) }
    }
}