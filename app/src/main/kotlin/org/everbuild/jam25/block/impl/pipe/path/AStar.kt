package org.everbuild.jam25.block.impl.pipe.path

import java.util.PriorityQueue

class AStarSearch<T>(
    private val startNode: T,
    private val endNode: T,
    private val getNeighbors: (T) -> List<T>,
    private val heuristic: (T, T) -> Double,
    private val getCost: (T, T) -> Double
) {

    /**
     * Finds the shortest path from the startNode to the endNode using the A* algorithm.
     *
     * @return A list of nodes representing the path from start to end, or null if no path is found.
     */
    fun findPath(): List<T>? {
        val fScore = mutableMapOf<T, Double>().withDefault { Double.POSITIVE_INFINITY }
        val openSet = PriorityQueue<T>(compareBy { node -> fScore.getOrDefault(node, Double.POSITIVE_INFINITY) })
        openSet.add(startNode)
        val cameFrom = mutableMapOf<T, T>()
        val gScore = mutableMapOf<T, Double>().withDefault { Double.POSITIVE_INFINITY }
        gScore[startNode] = 0.0
        fScore[startNode] = heuristic(startNode, endNode)

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()
            if (current == endNode) {
                return reconstructPath(cameFrom, current)
            }

            val neighbors = getNeighbors(current)

            for (neighbor in neighbors) {
                val tentativeGScore = gScore.getValue(current) + getCost(current, neighbor)

                if (tentativeGScore < gScore.getValue(neighbor)) {
                    cameFrom[neighbor] = current
                    gScore[neighbor] = tentativeGScore
                    fScore[neighbor] = tentativeGScore + heuristic(neighbor, endNode)

                    if (neighbor !in openSet) {
                        openSet.add(neighbor)
                    }
                }
            }
        }

        return null
    }

    private fun reconstructPath(cameFrom: Map<T, T>, current: T): List<T> {
        val totalPath = mutableListOf<T>()
        var tempCurrent: T? = current
        while (tempCurrent != null) {
            totalPath.add(tempCurrent)
            tempCurrent = cameFrom[tempCurrent]
        }
        return totalPath.reversed()
    }
}