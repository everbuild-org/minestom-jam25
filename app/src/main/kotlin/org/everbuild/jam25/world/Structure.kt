package org.everbuild.jam25.world

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import net.hollowcube.schem.Rotation
import net.hollowcube.schem.reader.SpongeSchematicReader
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.batch.RelativeBlockBatch
import net.minestom.server.instance.block.Block
import org.everbuild.jam25.util.background
import org.everbuild.jam25.world.placeable.AdvanceableWorldElement
import org.everbuild.jam25.world.placeable.StatefulWorldElement

interface AnimationState {
    val frame: String
}

data class SizedSchematic(val size: Point, val schematic: RelativeBlockBatch)

abstract class Structure<S : AnimationState, E : Any>(
    val centerPos: BlockVec,
    val rotation: Rotation,
    val states: StateMachine<S, E>
) : StatefulWorldElement<E>, AdvanceableWorldElement {
    private val schematicCache = hashMapOf<String, SizedSchematic>()
    private var lastSchematic: SizedSchematic? = null
    private var lastCounterOp: AbsoluteBlockBatch? = null

    private fun getSchematic(): SizedSchematic {
        val state = states.getCurrentState().frame
        schematicCache[state]?.let { return it }

        Structure::class.java.getResourceAsStream("/$state.schem")?.use { stream ->
            val schem = loader.read(stream.readAllBytes())
            val size = if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_270) {
                Pos(schem.size().z(), schem.size().y(), schem.size().z())
            } else {
                schem.size()
            }
            val batch = schem.createBatch(rotation)
            val sized = SizedSchematic(size, batch)
            schematicCache[state] = sized
            return sized
        }

        throw Exception("Could not load schematic for state $state")
    }


    fun despawn(instance: Instance) {
        lastCounterOp?.let { it.apply(instance) {} }
    }

    suspend fun spawn(instance: Instance) {
        val schem = getSchematic()
        if (lastSchematic != null && lastSchematic != schem && lastCounterOp != null) {
            wrapCallback { cont -> lastCounterOp!!.apply(instance, cont) }
        }

        if (lastSchematic == schem) return

        val x = centerPos.x()
        val z = centerPos.z()

        val base = Pos(x, centerPos.y(), z)
        lastSchematic = schem
        wrapCallback { cont ->
            lastCounterOp = schem.schematic.apply(instance, base) { cont }
        }
    }

    override fun consumeEvent(instance: Instance, event: E) {
        states.processEvent(event)

        background { spawn(instance) }
    }

    companion object {
        private val loader = SpongeSchematicReader()

        private suspend fun wrapCallback(cb: (cont: () -> Unit) -> Unit) {
            suspendCancellableCoroutine { cont ->
                cb {
                    cont.resume(
                        Unit,
                        null as ((cause: Throwable, value: Unit, context: CoroutineContext) -> Unit)?
                    )
                }
            }
        }
    }
}