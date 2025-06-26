package org.everbuild.asorda.resources.data.api.block

import net.kyori.adventure.key.Key
import org.everbuild.asorda.resources.data.api.InElementDsl
import org.everbuild.asorda.resources.data.api.block.props.PropertyValue
import org.everbuild.asorda.resources.data.api.maybeAsordaKey
import team.unnamed.creative.blockstate.Variant

typealias BlockModelPredicatePart = Pair<String, String>

class BlockModelsDsl(private val properties: Collection<PropertyValue<*>>, private val key: Key) {
    val models = hashMapOf<String, Variant>()
    var defaultModel: String = ""
    var default = ""

    @InElementDsl
    fun on(vararg matches: BlockModelPredicatePart, default: Boolean = false, block: BlockModelDsl.() -> Unit) {
        val model = BlockModelDsl().apply(block).createModel()

        val wildcardProperties = properties.filter { value -> value.name !in matches.map { it.first } }
        generatePermutations(wildcardProperties).forEach { combination ->
            matches.forEach { match ->
                combination[match.first] = match.second
            }

            val matchId = matches.map { it.first + "=" + it.second }.sorted().joinToString(",")
            val match = "${key.asString()}[$matchId]"
            models[match] = model

            if (default) {
                this.defaultModel = model.model().asString()
                this.default = match
            }
        }
    }

    infix fun <T> PropertyValue<T>.eq(value: T): BlockModelPredicatePart {
        return this.name to value.toString()
    }
}

class BlockModelDsl {
    private lateinit var model: Key
    private var x = Variant.DEFAULT_X_ROTATION
    private var y = Variant.DEFAULT_Y_ROTATION
    private var uvLock = Variant.DEFAULT_UV_LOCK
    private var weight = Variant.DEFAULT_WEIGHT

    fun model(model: Key) {
        this.model = model
    }

    fun model(model: String) {
        this.model = model.maybeAsordaKey()
    }

    fun rotation(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun uvLock(lock: Boolean) {
        this.uvLock = lock
    }

    fun weight(weight: Int) {
        this.weight = weight
    }

    fun createModel(): Variant {
        return Variant.builder()
            .model(model)
            .x(x)
            .y(y)
            .uvLock(uvLock)
            .weight(weight)
            .build()
    }
}
