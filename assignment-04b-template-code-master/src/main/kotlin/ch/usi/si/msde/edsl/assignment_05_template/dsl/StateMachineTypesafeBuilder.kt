package ch.usi.si.msde.edsl.lecture12.dsl

import ch.usi.si.msde.edsl.assignment_05_template.model.StateMachine

fun stateMachine(init: StateMachine.() -> Unit): StateMachine {
    val stateMachine = StateMachine()
    stateMachine.init()
    return stateMachine
}

fun main(vararg args: String) {

    val stateMachine = stateMachine {

        val idle = state {
            initial()
            name("idle")
        }

        val activeState = state { name("activeState") }
        val waitingForLightState = state { name("waitingForLightState") }
        val waitingForDrawerState = state { name("waitingForDrawerState") }
        val unlockedPanelState = state { name("unlockedPanelState") }

        val toggles = counter {
            initialValue(0)
            name("toggles")
        }

        val doorClosed = event { name("doorClosed") }
        val drawerOpened = event { name("drawerOpened") }
        val toggleLight = event {
            name("toggleLight")
            guard {
                toggles < 3
            }
            effect {
                toggles += 1
            }
        }
        val lightsOn = event {
            name("lightsOn")
            guard {
                toggles >= 3
            }
        }

        val panelClosed = event { name("panelClosed") }

        transitions {
            idle { doorClosed to activeState }
            activeState {
                drawerOpened to waitingForLightState
            }
            activeState { lightsOn .. waitingForDrawerState }
            waitingForLightState { lightsOn to unlockedPanelState }
            waitingForDrawerState { drawerOpened to unlockedPanelState }
            unlockedPanelState { panelClosed to idle }
        }
    }

    print(stateMachine)

}





