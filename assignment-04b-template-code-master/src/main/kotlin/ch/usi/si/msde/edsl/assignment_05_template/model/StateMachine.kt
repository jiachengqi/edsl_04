package ch.usi.si.msde.edsl.assignment_05_template.model


/**
 * A name for events, commands, and states.
 *
 * Name representations are case insensitive.
 * @param value the string representing a name.
 */
data class Name(val value: String)

/**
 * A language entity that has a name.
 */
abstract class NamedEntity(var name: Name? = null) {

    fun name(nameString: String){
        this.name = Name(nameString)
    }

}

abstract class AbstractEvent(name: Name? = null): NamedEntity(name)

/**
 * This is a named command that can be sent to the environment by states.
 *
 * @param name The name of the command.
 */
class Command(name: Name? = null): AbstractEvent(name) {

    override fun toString() = "c(${name?.value})"

}

/**
 * Named environment event that triggers a transition.
 *
 * @param name The name of the event.
 * @param guard The optional guard on counters that activates the event.
 * @param effect the optional effect on counters of this transition.
 */
class Event(name: Name? = null, private var guard: () -> Boolean = { true },
            private var effect: () -> Unit = {}): AbstractEvent(name) {

    fun guard(value: () -> Boolean) { this.guard = value }
    fun effect(value: () -> Unit) { this.effect = value }
    override fun toString() = "e(${name?.value})"

}

/**
 * A state on the state machine.
 *
 * @param name The name of the state.
 * @param commands The set of commands sent by this state to the environment.
 */
 class State(name: Name? = null, var initial: Boolean = false,
             private val commands: Set<Command> = setOf()): NamedEntity(name) {

    fun initial() { this.initial = true }
    override fun toString() = "s(${name?.value},cs(${commands}))"

}

/**
 * A transition between to states, triggered by an event.
 *
 * @param source the source state.
 * @param trigger the events that triggers the transition from the source state.
 * @param target the target state.
 */
data class Transition(val source: State, val trigger: Event, val target: State) {

    override fun toString() = "$source -${trigger}-> $target"

}

/**
 * A state machine.
 *
 * @param initialState the initial state of the machine.
 * @param transitions a list of transitions.
 */
data class StateMachine(var initialState: State? = null,
                        var transitions: List<Transition> = listOf()) {

    fun state(init: State.() -> Unit): State = State().apply(init).apply { if (initial) initialState = this }
    fun counter(init: Counter.() -> Unit): Counter = Counter().apply(init)
    fun event(init: Event.() -> Unit): Event = Event().apply(init)
    fun transitions(init: Transitions.() -> Unit): List<Transition> =
        Transitions().apply(init).apply { this@StateMachine.transitions = transitions }.transitions

}

/**
 * An event and State pair.
 */
class EventToState(var event: Event? = null, var target: State? = null)

/**
 * Represents a set of event and state pairs.
 */
class EventToStateSet(var eventToStateSet: MutableList<EventToState> = mutableListOf()) {

    operator fun Event.rangeTo(state: State) = eventToStateSet.add(EventToState(this, state))
    infix fun Event.to(state: State) = rangeTo(state)

}

/**
 * Represents a set of transitions.
 */
class Transitions(var transitions: MutableList<Transition> = mutableListOf()) {

    operator fun State.invoke(init: EventToStateSet.() -> Unit): List<Transition> {
        EventToStateSet().apply(init).apply { eventToStateSet .forEach {
                transitions.add(Transition(this@invoke, it.event!!, it.target!!)) } }
        return transitions
    }

}

class Counter(name: Name? = null, private var initialValue: Int = 0): NamedEntity(name) {

    fun initialValue(value: Int) { this.initialValue = value }
    operator fun compareTo(value: Int): Int = initialValue.compareTo(value)
    operator fun plusAssign(value: Int) { this.initialValue += value }

}