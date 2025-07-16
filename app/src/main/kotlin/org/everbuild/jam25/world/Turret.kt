package org.everbuild.jam25.world

import kotlin.time.Duration.Companion.seconds
import net.hollowcube.schem.Rotation
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.Instance
import org.everbuild.celestia.orion.core.util.Cooldown

sealed interface TurretState : AnimationState {
    data class Idle(override val frame: String = "cannon_0") : TurretState
    data class Load(override val frame: String = "cannon_1") : TurretState
    data class Fire(override val frame: String = "cannon_2") : TurretState
}

sealed interface TurretEvent {
    object Fire : TurretEvent
    object Progress : TurretEvent
}

class Turret(pos: BlockVec, rotation: Rotation) : Structure<TurretState, TurretEvent>(
    pos, rotation,
    StateMachine.create {
        initialState(TurretState.Idle())

        state<TurretState.Idle> {
            on<TurretEvent.Fire> {
                transitionTo(TurretState.Load())
            }
        }

        state<TurretState.Load> {
            on<TurretEvent.Progress> {
                transitionTo(TurretState.Fire())
            }
        }

        state<TurretState.Fire> {
            on<TurretEvent.Progress> {
                transitionTo(TurretState.Idle())
            }
        }
    }
) {
    val cooldown = Cooldown(1.seconds)
    val queuedEvents = mutableListOf<TurretEvent>()

    override fun consumeEvent(instance: Instance, event: TurretEvent) {
        if (event is TurretEvent.Fire) {
            // sync with clock
            queuedEvents.add(event)
            return
        }
        super.consumeEvent(instance, event)
    }

    override fun advance(instance: Instance) {
        if (!cooldown.get()) return

        queuedEvents.removeFirstOrNull()?.let {
            super.consumeEvent(instance, it)
            return
        }

        consumeEvent(instance, TurretEvent.Progress)
    }

    override fun getBlockPosition(): BlockVec = centerPos
}