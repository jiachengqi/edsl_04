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
class Event(name: Name? = null, var guard: () -> Boolean = { true }, var effect: () -> Unit = {}): AbstractEvent(name) {

    fun name(value: String) {this.name = Name(value)}
    fun guard(value: () -> Boolean) {this.guard = value}
    fun effect(value: () -> Unit) {this.effect = value}

    override fun toString() = "e(${name?.value})"

}

/**
 * A state on the state machine.
 *
 * @param name The name of the state.
 * @param commands The set of commands sent by this state to the environment.
 */
 class State(name: Name? = null, var initial: Boolean = false, val commands: Set<Command> = setOf()): NamedEntity(name) {

    fun initial() {this.initial = true}
    fun name(value: String) {this.name = Name(value)}
    operator fun invoke(function: () -> Pair<Event, State>): Transition {}

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
 * @param counters a set of counters.
 */
data class StateMachine(var initialState: State? = null,
                        var transitions: List<Transition> = listOf()) {

    fun state(init: State.() -> Unit): State {
        val state = State()
        state.init()
        if (state.initial) {initialState = state}
        return state
    }

    fun counter(init: Counter.() -> Unit): Counter {
        val counter = Counter()
        counter.init()
        return counter
    }

    fun event(init: Event.() -> Unit): Event {
        val event = Event()
        event.init()
        return event
    }

    fun transitions(init: Transitions.() -> Unit): Transitions {
        val transitions = Transitions()
        transitions.init()
        return transitions
    }
}



/**
 * An event and State pair.
 */
class EventToState(var event: Event? = null, var target: State? = null) {
    fun to(value1: Event, value2: State){
        value2.invoke {  }

    }
}

/**
 * Represents a set of transitions.
 */
class Transitions(var transitions: MutableList<Transition> = mutableListOf<Transition>()) {
    fun transition(init: State): Transition {
        val transition = init.invoke()
        this.transitions.add(transition)
        return transition
    }
}

class Counter(name: Name? = null, var initialValue: Int = 0): NamedEntity(name) {
    var value: Int = initialValue
        get() = field
        set(value) {
            field = value
        }

    fun name(value: String) {this.name = Name(value)}
    fun initialValue(value: Int) {this.initialValue = value}
    operator fun compareTo(value: Int): Int {
        if (this.initialValue > value) return 1;
        else if (this.initialValue == value) return 0;
        else return -1
    }
    operator fun plusAssign(value: Int){this.initialValue+=value}
}