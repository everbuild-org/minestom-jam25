package org.everbuild.jam25.block.impl.pipe

import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import org.everbuild.jam25.block.api.BlockController
import org.everbuild.jam25.block.impl.pipe.PipeBlock.faceCanConnectTag
import org.everbuild.jam25.block.impl.pipe.path.AStarSearch
import org.everbuild.jam25.block.impl.pipe.path.ResourcePath
import org.everbuild.jam25.state.ingame.InGameState
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.ItemConsumer
import org.everbuild.jam25.world.placeable.ItemHolder

class PipeNetworkController(val game: InGameState) {
    val instance = game.world.instance
    val requests = mutableListOf<ResourcePath>()

    fun request(resource: ItemConsumer.ItemOrOil, to: BlockVec, blockFace: BlockFace) {
        if (requests.any { it.target == to }) return // already running

        val source = searchNodes(to)
            .mapNotNull { game.getAdvanceable<ItemHolder>(it) }
            .filter { it.hasItem(resource) }
            .nearest(to) ?: return

        val astar = AStarSearch(
            startNode = source.position,
            endNode = to.relative(blockFace),
            getNeighbors = { node -> neighbouringPipes(node, instance) },
            heuristic = { n1, n2 -> n1.distance(n2) },
            getCost = { n1, n2 -> n1.distance(n2) },
        )

        val path = astar.findPath() ?: return
        val realResource = source.holder.removeItem(resource) ?: return
        if (path.isEmpty()) {
            game.getAdvanceable<ItemConsumer>(to)?.consumeItem(realResource)
            return
        }

        ResourcePath(realResource, path.toMutableList(), instance, game, to) {
            game.getAdvanceable<ItemConsumer>(to)?.consumeItem(realResource)
        }
    }

    fun searchNodes(block: BlockVec): List<BlockVec> {
        val visited = mutableSetOf<BlockVec>()
        val toVisit = mutableListOf(block)
        val endNodes = mutableSetOf<BlockVec>()

        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeAt(0)
            val pipes = neighbouringPipes(current, instance)
            val ends = neighbouringEnds(current, instance)
            visited.add(current)
            endNodes.addAll(ends)
            for (pipe in pipes) {
                if (pipe !in visited) {
                    toVisit.add(pipe)
                }
            }
        }

        return endNodes.toList()
    }

    private fun neighbouringPipes(block: BlockVec, instance: Instance): List<BlockVec> {
        return BlockFace.entries.map { block.relative(it) }.filter { pos ->
            val block = instance.getBlock(pos)
            return@filter (block.getTag(BlockController.typeTag) == PipeBlock.key().toString())
        }
    }

    private fun neighbouringEnds(block: BlockVec, instance: Instance): List<BlockVec> {
        return BlockFace.entries.filter { dir ->
            val pos = block.relative(dir)
            return@filter instance.getBlock(pos).getTag(faceCanConnectTag)?.split("|")?.contains(dir.oppositeFace.name) ?: false
        }.map { block.relative(it) }
    }

    private fun List<ItemHolder>.nearest(to: BlockVec): PositionedItemHolder? {
        val holder = this.minByOrNull { (it as AdvanceableWorldElement).getBlockPosition().distance(to) } ?: return null
        return PositionedItemHolder(
            position = holder.let { (it as AdvanceableWorldElement).getBlockPosition() },
            holder = holder
        )
    }

    data class PositionedItemHolder(val position: BlockVec, val holder: ItemHolder)
}
