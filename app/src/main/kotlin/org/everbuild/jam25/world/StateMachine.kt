package org.everbuild.jam25.world
import kotlin.reflect.KClass

class StateMachine<S : Any, E : Any> private constructor(
    private var currentState: S,
    private val definition: StateMachineDefinition<S, E>
) {
    var onStateChanged: ((oldState: S, newState: S) -> Unit)? = null

    fun getCurrentState(): S = currentState

    fun processEvent(event: E) {
        val transition = definition.findTransition(currentState, event)

        if (transition != null) {
            val oldState = currentState
            currentState = transition.newState
            transition.sideEffect?.invoke()
            onStateChanged?.invoke(oldState, currentState)
        }
    }

    companion object {
        fun <S : Any, E : Any> create(block: StateMachineDefinitionBuilder<S, E>.() -> Unit): StateMachine<S, E> {
            val builder = StateMachineDefinitionBuilder<S, E>().apply(block)
            val definition = builder.build()
            return StateMachine(definition.initialState, definition)
        }
    }
}

data class Transition<S : Any>(
    val newState: S,
    val sideEffect: (() -> Unit)? = null
)

class StateMachineDefinition<S : Any, E : Any>(
    val initialState: S,
    private val transitions: Map<KClass<out S>, Map<KClass<out E>, Transition<S>>>
) {
    fun findTransition(state: S, event: E): Transition<S>? {
        return transitions[state::class]?.get(event::class)
    }
}

class StateMachineDefinitionBuilder<S : Any, E : Any> {
    private lateinit var initialStateVal: S
    private val transitions = mutableMapOf<KClass<out S>, MutableMap<KClass<out E>, Transition<S>>>()

    fun initialState(state: S) {
        initialStateVal = state
    }

    fun <State : S> state(stateClass: KClass<State>, block: StateConfigurationBuilder<S, E, State>.() -> Unit) {
        val stateConfigBuilder = StateConfigurationBuilder<S, E, State>(stateClass)
        stateConfigBuilder.apply(block)
        val built = stateConfigBuilder.build()
        transitions.putAll(built)
    }

    inline fun <reified State : S> state(noinline block: StateConfigurationBuilder<S, E, State>.() -> Unit) {
        state(State::class, block)
    }

    fun build(): StateMachineDefinition<S, E> {
        if (!::initialStateVal.isInitialized) {
            throw IllegalStateException("Initial state must be defined.")
        }
        return StateMachineDefinition(initialStateVal, transitions)
    }
}

class StateConfigurationBuilder<S : Any, E : Any, CurrentState : S>(
    private val currentStateClass: KClass<CurrentState>
) {
    private val transitions = mutableMapOf<KClass<out S>, MutableMap<KClass<out E>, Transition<S>>>()

    fun <Event : E> on(eventClass: KClass<Event>, block: TransitionBuilder<S>.() -> Unit) {
        val transitionBuilder = TransitionBuilder<S>().apply(block)
        if (!transitions.containsKey(currentStateClass)) {
            transitions[currentStateClass] = mutableMapOf()
        }
        transitions[currentStateClass]!![eventClass] = transitionBuilder.build()
    }

    inline fun <reified Event : E> on(noinline block: TransitionBuilder<S>.() -> Unit) {
        on(Event::class, block)
    }

    fun build(): Map<KClass<out S>, MutableMap<KClass<out E>, Transition<S>>> = transitions
}

class TransitionBuilder<S : Any> {
    private lateinit var targetState: S
    private var sideEffect: (() -> Unit)? = null

    fun transitionTo(state: S) {
        this.targetState = state
    }

    fun withSideEffect(action: () -> Unit) {
        this.sideEffect = action
    }

    fun build(): Transition<S> {
        if (!::targetState.isInitialized) {
            throw IllegalStateException("A transition must have a target state.")
        }
        return Transition(targetState, sideEffect)
    }
}