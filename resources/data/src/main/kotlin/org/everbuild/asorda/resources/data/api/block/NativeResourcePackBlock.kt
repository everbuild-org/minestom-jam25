package org.everbuild.asorda.resources.data.api.block

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.BuildableResource
import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.block.props.PropertyValue
import org.everbuild.asorda.resources.data.api.item.ResourcePackItemDsl
import org.everbuild.asorda.resources.data.api.lockfile.LockedBlockState
import org.everbuild.asorda.resources.data.api.lockfile.LockfileService
import org.everbuild.asorda.resources.data.api.model.ResourcePackModelDsl
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.blockstate.BlockState
import team.unnamed.creative.blockstate.MultiVariant
import team.unnamed.creative.blockstate.Variant

abstract class NativeResourcePackBlock(key: Key) : AbstractResourcePackBlock, BuildableResource, BlockResource(key, hashMapOf()) {
    lateinit var blockModels: HashMap<String, Variant>
    override val properties: HashMap<String, PropertyValue<*>> = hashMapOf()

    val defaultItemModel = Key.key("asorda", "auto/native/${key.value()}/default_item")
    val content = object : ContentList("native/${key.value()}") {}

    private fun getOrCreateBlockState(pack: ResourcePack, native: LockedBlockState): BlockState {
        val current = pack.blockState(Key.key(native.key))
        if (current != null) return current

        val state = BlockState.of(
            Key.key(native.key), hashMapOf(
                "_" to MultiVariant.of(Variant.builder().model(Key.key("_")).build())
            )
        )
        pack.blockState(state)
        return state
    }

    fun models(block: BlockModelsDsl.() -> Unit) {
        for (permutation in generatePermutations(properties.values)) {
            val props = permutation.map { (name, value) -> "${name}=${value}" }.sorted().joinToString(",")
            val data = "${key.asString()}[$props]"
            val native = LockfileService.lockfile.basicBlockStates.retrieveOrLock(data)
            stateIds[data] = BlockStateIdentifier.ForeignStateBlockStateIdentifier(Key.key(native.key), native.properties)
        }

        val dsl = BlockModelsDsl(properties.values, key).also(block)
        blockModels = dsl.models
        defaultStateId = dsl.default
        defaultModel = dsl.defaultModel
    }

    open fun ResourcePackItemDsl.createItemModel() {
        model(defaultModel)
        handAnimationOnSwap = false
    }

    override fun buildInto(pack: ResourcePack) {
        content.createItem("default_item") { createItemModel() }
        content.buildInto(pack)
        for ((mapped, nativeUncast) in stateIds) {
            val native = nativeUncast as BlockStateIdentifier.ForeignStateBlockStateIdentifier
            val state = getOrCreateBlockState(pack, LockedBlockState(native.key.asString(), native.properties))
            val model = blockModels[mapped] ?: throw IllegalStateException("No model for $mapped")
            val variantKey = native.properties.map { it.key + "=" + it.value }.joinToString(",")
            val variants = state.variants()
            if (state.variants().containsKey(variantKey)) {
                variants[variantKey]!!.variants().add(model)
            } else {
                variants[variantKey] = MultiVariant.of(model)
            }
        }
    }
}