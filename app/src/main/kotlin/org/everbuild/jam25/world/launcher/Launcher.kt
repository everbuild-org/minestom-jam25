package org.everbuild.jam25.world.launcher

import kotlin.time.Duration.Companion.seconds
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.utils.Direction
import org.everbuild.celestia.orion.core.util.Cooldown
import org.everbuild.celestia.orion.platform.minestom.api.utils.dropItem
import org.everbuild.jam25.Jam
import org.everbuild.jam25.block.api.Highlighter
import org.everbuild.jam25.block.impl.launcher.MissileLauncherBlock
import org.everbuild.jam25.block.impl.missile1.Missile1Block
import org.everbuild.jam25.item.api.AbstractItem
import org.everbuild.jam25.item.impl.Missile1Item
import org.everbuild.jam25.state.ingame.GameTeam
import org.everbuild.jam25.world.ItemStackHologramLine
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer

class Launcher(val pos: BlockVec, val team: GameTeam) : AdvanceableWorldElement, ItemConsumer {
    val items = HashMap<AbstractItem, Int>()
    val hologramLines = HashMap<AbstractItem, ItemStackHologramLine>()
    val spawnCd = Cooldown(1.seconds)
    var isRunning = false
    var hl: Highlighter? = null

    override fun advance(instance: Instance) {
        updateHologram(instance)
        if (spawnCd.get()) {
            team.game.networkController.also { net ->
                net.neighbouringPipes(pos, instance).forEach { pipe ->
                    val dirVec = pos.sub(pipe).asVec().normalize()
                    val dir = BlockFace.fromDirection(Direction.entries.toTypedArray().minBy { it.vec().dot(dirVec) })
                    net.request(ItemConsumer.ItemOrOil.Item(Missile1Item.createItem()), pos, dir) }
                }
            if (!isRunning) trySpawn(instance)
        }
    }

    fun drop(instance: Instance) {
        items.forEach { (item, amount) ->
            instance.dropItem(item.createNewStack(amount), pos.add(0.5, 0.5, 0.5))
        }
        hologramLines.forEach { (_, hologram) -> hologram.remove() }
        hologramLines.clear()
        items.clear()
        hl?.remove()
    }

    override fun consumeItem(item: ItemConsumer.ItemOrOil) {
        val itemVal = item as? ItemConsumer.ItemOrOil.Item ?: return
        if (Missile1Item.createItem().isSimilar(itemVal.itemStack)) {
            items[Missile1Item] = (items[Missile1Item] ?: 0) + itemVal.itemStack.amount()
        }
    }

    private fun updateHologram(instance: Instance) {
        if (hologramLines.keys == items.keys) {
            hologramLines.forEach { (resource, hologram) -> hologram.amount = items[resource] ?: 0 }
            return
        }
        hologramLines.forEach { (_, hologram) -> hologram.remove() }
        hologramLines.clear()
        items.onEachIndexed { index, (resource, amount) ->
            hologramLines[resource] =
                ItemStackHologramLine(pos, index + 4, resource.createItem()).also { it.amount = amount }
        }
        hologramLines.forEach { (_, hologram) -> hologram.setInstance(instance) }
    }

    private fun trySpawn(instance: Instance) {
        val missiles = items[Missile1Item] ?: return
        if (missiles == 0) return

        val spawnPos = pos.add(2.0, 0.0, 0.0)
        if (team.missileTracker.any { it.entity.position.distanceSquared(spawnPos) < 1.0 }) {
            return
        }

        if (!instance.getBlock(spawnPos).registry().isReplaceable) {
            if (hl == null) {
                team.sendMiniMessage("${Jam.PREFIX} <red>Missile launcher blocked by a block!")
                hl = Highlighter(instance, BlockVec(spawnPos), 0xff0000, Block.RED_STAINED_GLASS, team)
            }

            return
        }

        hl?.remove()
        hl = null

        isRunning = true
        val thenMissiles = missiles - 1
        if (thenMissiles > 0) {
            items[Missile1Item] = thenMissiles
        } else {
          items.remove(Missile1Item)
        }

        MissileLauncherBlock.trySpawn(instance, pos, BlockVec(spawnPos), Missile1Block, team) {
            isRunning = false
        }
    }

    override fun getBlockPosition(): BlockVec = pos
}