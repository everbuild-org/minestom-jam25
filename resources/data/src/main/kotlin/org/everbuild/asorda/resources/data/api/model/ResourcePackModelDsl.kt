package org.everbuild.asorda.resources.data.api.model

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.BuildableResource
import org.everbuild.asorda.resources.data.api.PackBuilder
import org.everbuild.asorda.resources.data.api.texture.Texture
import org.everbuild.asorda.resources.data.api.maybeAsordaKey
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.base.Vector3Float
import team.unnamed.creative.model.Element
import team.unnamed.creative.model.ItemOverride
import team.unnamed.creative.model.ItemTransform
import team.unnamed.creative.model.Model
import team.unnamed.creative.model.ModelTexture
import team.unnamed.creative.model.ModelTextures

class ResourcePackModelDsl(modelKey: Key) : Model.Builder, BuildableResource {
    private val model = Model.model()
        .key(modelKey)
    private val children = mutableListOf<BuildableResource>()

    override fun buildInto(pack: ResourcePack) {
        children.forEach { PackBuilder.build(pack, it) }
        pack.model(model.build())
    }

    fun scaleTransform(x: Float, y: Float, z: Float): ItemTransform = ItemTransform.transform()
        .scale(Vector3Float(x, y, z))
        .build()

    fun scaleTransform(scale: Float): ItemTransform = scaleTransform(scale, scale, scale)

    fun hiddenTransform(): ItemTransform = ItemTransform.transform()
        .translation(Vector3Float(0f, 0f, 2.5f))
        .scale(Vector3Float(0f, 0f, 0f))
        .build()

    override fun key(key: Key): Model.Builder = model.key(key)
    override fun parent(parent: Key?): Model.Builder = model.parent(parent)
    fun parent(parent: String): Model.Builder = model.parent(parent.maybeAsordaKey())
    fun parent(parent: ModelResource): Model.Builder = model.parent(parent.key)
    override fun ambientOcclusion(ambientOcclusion: Boolean): Model.Builder = model.ambientOcclusion(ambientOcclusion)
    override fun display(display: Map<ItemTransform.Type, ItemTransform>): Model.Builder = model.display(display)
    fun display(vararg display: Pair<ItemTransform.Type, ItemTransform>): Model.Builder = model.display(display.toMap())
    override fun textures(textures: ModelTextures): Model.Builder = model.textures(textures)
    fun textures(textures: Map<String, Texture>): Model.Builder =
        model.textures(
            ModelTextures.of(
                listOf(),
                null,
                textures.mapValues {
                    children.add(it.value)
                    ModelTexture.ofKey(it.value.key)
                }
            ))

    fun textures(vararg textures: Pair<String, Texture>): Model.Builder = textures(textures.toMap())
    override fun guiLight(guiLight: Model.GuiLight?): Model.Builder = model.guiLight(guiLight)
    override fun elements(elements: MutableList<Element>): Model.Builder = model.elements(elements)
    override fun addElement(element: Element): Model.Builder = model.addElement(element)

    @Deprecated("Unsupported operation in modern version")
    override fun overrides(overrides: MutableList<ItemOverride>): Model.Builder = model.overrides(overrides)

    @Deprecated("Unsupported operation in modern version")
    override fun addOverride(override: ItemOverride): Model.Builder = model.addOverride(override)
    override fun build(): Model = model.build()
}