package org.everbuild.asorda.resources.data.api.block

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.BuildableResource
import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.InElementDsl
import org.everbuild.asorda.resources.data.api.block.props.PropertyValue
import org.everbuild.asorda.resources.data.api.item.ResourcePackItemDsl
import org.everbuild.asorda.resources.data.api.maybeAsordaKey
import team.unnamed.creative.ResourcePack

abstract class EntityResourcePackBlock(override val key: Key) : AbstractResourcePackBlock, BuildableResource {
    override val properties: HashMap<String, PropertyValue<*>> = hashMapOf()
    val stateIds: MutableMap<Map<String, String>, Key> = mutableMapOf()
    var defaultModel: String = ""
    val content = object : ContentList("entityblock/${key.value()}") {}
    private val children = mutableListOf<BuildableResource>()

    fun models(block: EntityBlockModelsDsl.() -> Unit) {
        val dsl = EntityBlockModelsDsl(properties.values, key).also(block)

        for (permutation in generatePermutations(properties.values)) {
            val nearestStateOrNull = getNearestMatchingState(permutation, dsl.models.keys)
            if (nearestStateOrNull == null) {
                stateIds[permutation] = dsl.default!!.getCustomModel().maybeAsordaKey()
                continue
            }

            stateIds[permutation] = dsl.models[nearestStateOrNull]!!.getCustomModel().maybeAsordaKey()
        }

        defaultModel = dsl.default?.getCustomModel() ?: throw IllegalStateException("No default model defined")
        children.add(dsl)
    }

    companion object {
        fun getNearestMatchingState(
            fullDefinition: Map<String, String>,
            options: Set<Map<String, String>>
        ): Map<String, String>? {
            if (options.isEmpty()) return null
            if (options.contains(fullDefinition)) return fullDefinition

            var bestMatch: Map<String, String>? = null
            var maxMatchingProperties = -1

            for (option in options) {
                var matchingProperties = 0
                for ((key, value) in option) {
                    if (fullDefinition[key] == value) {
                        matchingProperties++
                    }
                }

                if (matchingProperties > maxMatchingProperties) {
                    maxMatchingProperties = matchingProperties
                    bestMatch = option
                }
            }

            return bestMatch
        }
    }

    @InElementDsl
    fun EntityBlockModelsDsl.on(vararg matches: BlockModelPredicatePart, default: Boolean = false, model: String) {
        val desc = matches.joinToString("_") { it.first.lowercase() + "_" + it.second.lowercase() }
        on(*matches, default = default, model = createItem(desc) {
            model(model.maybeAsordaKey().asString())
        })
    }

    override fun buildInto(pack: ResourcePack) {
        children.forEach { it.buildInto(pack) }
        content.buildInto(pack)
    }
}