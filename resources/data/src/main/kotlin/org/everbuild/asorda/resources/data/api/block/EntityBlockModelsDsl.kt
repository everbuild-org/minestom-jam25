package org.everbuild.asorda.resources.data.api.block

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.InElementDsl
import org.everbuild.asorda.resources.data.api.block.props.PropertyValue
import org.everbuild.asorda.resources.data.api.item.ItemResource
import org.everbuild.asorda.resources.data.api.maybeAsordaKey
import org.everbuild.asorda.resources.data.api.model.ModelResource

class EntityBlockModelsDsl(private val properties: Collection<PropertyValue<*>>, private val key: Key) : ContentList("block/${key.value()}") {
    val models = hashMapOf<Map<String, String>, ItemResource>()
    var default: ItemResource? = null

    @InElementDsl
    fun on(vararg matches: BlockModelPredicatePart, default: Boolean = false, model: ItemResource) {
        val wildcardProperties = properties.filter { value -> value.name !in matches.map { it.first } }
        generatePermutations(wildcardProperties).forEach { combination ->
            matches.forEach { match ->
                combination[match.first] = match.second
            }

            models[combination] = model

            if (default) {
                this.default = model
            }
        }
    }

    infix fun <T> PropertyValue<T>.eq(value: T): BlockModelPredicatePart {
        return this.name to value.toString()
    }
}

