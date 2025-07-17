package org.everbuild.jam25.world.crafting

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.BlockFace
import org.everbuild.celestia.orion.platform.minestom.api.utils.dropItem
import org.everbuild.jam25.state.ingame.InGameState
import org.everbuild.jam25.world.Resource
import org.everbuild.jam25.world.ResourceHologramLine
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer
import org.everbuild.jam25.world.placeable.ItemHolder
import kotlin.math.min
import kotlin.math.roundToInt

class Crafter(
    val position: BlockVec,
    val recipeIngredients: List<ItemConsumer.ItemOrOil>,
    val recipeOutput: ItemConsumer.ItemOrOil,
    val game: InGameState,
    val inputFace: BlockFace,
    val verticalHologramOffset: Double
) : AdvanceableWorldElement, ItemHolder, ItemConsumer {
    init {
        require(recipeIngredients.all { Resource.fromItemOrOil(it) != null }) { "Crafter at $position has an ingredient which is not a resouce" }
        require(Resource.fromItemOrOil(recipeOutput) != null) { "Crafter at $position has an output which is not a resource" }
    }

    private val inputItems = HashMap<Resource, Int>()
    private var outputItem: StoredResource? = null

    val hologramLines = HashMap<Resource, ResourceHologramLine>()

    override fun hasItem(item: ItemConsumer.ItemOrOil): Boolean {
        return when (item) {
            is ItemConsumer.ItemOrOil.Oil -> outputItem?.resource == Resource.OIL && (outputItem?.amount ?: 0) > 0

            is ItemConsumer.ItemOrOil.Item -> outputItem?.resource == Resource.fromItem(item.itemStack) && (outputItem?.amount ?: 0) > 0
        }
    }

    override fun removeItem(item: ItemConsumer.ItemOrOil): ItemConsumer.ItemOrOil? {
        val outputItem = outputItem ?: return null
        if (!hasItem(item)) return null
        val retrievedAmount = min(outputItem.amount, item.amount())
        outputItem.amount = (outputItem.amount - item.amount()).coerceAtLeast(0)
        if (outputItem.amount == 0) this.outputItem = null
        return item.withAmount(retrievedAmount)
    }

    override fun advance(instance: Instance) {
        if ((outputItem?.amount ?: 0) == 0) {
            for (ingredient in recipeIngredients) {
                val ingredientResource = Resource.fromItemOrOil(ingredient) ?: continue
                val storedAmount = ingredientResource.let { inputItems[it] } ?: 0
                if (storedAmount >= ingredient.amount()) continue
                game.networkController.request(ingredient.withAmount(ingredient.amount() - storedAmount), position, inputFace)
            }
            tryCraft()
        }
        updateHologram(instance)
    }

    private fun tryCraft() {
        if (recipeIngredients.all { ingredient ->
                val ingredientResource = Resource.fromItemOrOil(ingredient) ?: return@all false
                return@all (inputItems[ingredientResource] ?: 0) >= ingredient.amount()
            }
        ) {
            for (ingredient in recipeIngredients) {
                val ingredientResource = Resource.fromItemOrOil(ingredient) ?: continue
                inputItems.compute(ingredientResource) { _, amount -> (amount ?: 0) - ingredient.amount() }
                if (inputItems[ingredientResource] == 0) inputItems.remove(ingredientResource)
            }
            val outputResource = Resource.fromItemOrOil(recipeOutput) ?: return
            outputItem = StoredResource(outputResource, recipeOutput.amount())
        }
    }

    override fun consumeItem(item: ItemConsumer.ItemOrOil) {
        if (recipeIngredients.any { ingredient ->
                when (item) {
                    is ItemConsumer.ItemOrOil.Oil -> ingredient is ItemConsumer.ItemOrOil.Oil
                    is ItemConsumer.ItemOrOil.Item -> ingredient is ItemConsumer.ItemOrOil.Item && item.itemStack.isSimilar(ingredient.itemStack)
                }
            }
        ) {
            val resource = Resource.fromItemOrOil(item) ?: return
            inputItems.compute(resource) { _, amount ->
                (amount ?: 0) + item.amount()
            }
        }
    }

    private fun updateHologram(instance: Instance) {
        val allStoredResources = allStoredResources()
        if (hologramLines.keys == allStoredResources.keys) {
            hologramLines.forEach { (resource, hologram) ->
                hologram.amount = allStoredResources[resource] ?: 0
            }
            return
        }
        hologramLines.forEach { (_, hologram) -> hologram.remove() }
        hologramLines.clear()
        allStoredResources.onEachIndexed { index, (resource, amount) ->
            hologramLines[resource] = ResourceHologramLine(position, index + (2 * verticalHologramOffset).roundToInt(), resource).also { it.amount = amount }
        }
        hologramLines.forEach { (_, hologram) -> hologram.setInstance(instance) }
    }

    fun drop(instance: Instance) {
        allStoredResources()
            .filter { (_, amount) -> amount > 0 }
            .filter { (res, _) -> res.doDrop }
            .forEach { (res, amount) ->
                var remaining = amount
                while (remaining > 0) {
                    val pickUp = res.symbol.maxStackSize().coerceAtMost(remaining)
                    instance.dropItem(res.symbol.withAmount(pickUp), position.add(0.5, 0.5, 0.5))
                    remaining -= pickUp
                }
            }
    }

    private fun allStoredResources(): Map<Resource, Int> = buildMap {
        putAll(inputItems)
        outputItem?.let { outputItem ->
            compute(outputItem.resource) { _, amount ->
                (amount ?: 0) + outputItem.amount
            }
        }
    }

    override fun getBlockPosition() = position

    data class StoredResource(val resource: Resource, var amount: Int)
}