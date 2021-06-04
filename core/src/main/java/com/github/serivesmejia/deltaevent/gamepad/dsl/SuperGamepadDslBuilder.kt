package com.github.serivesmejia.deltaevent.gamepad.dsl

import com.github.serivesmejia.deltaevent.event.gamepad.SuperGamepadEvent
import com.github.serivesmejia.deltaevent.gamepad.SuperGamepad
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.github.serivesmejia.deltaevent.gamepad.button.Buttons

class SuperGamepadDslBuilder(
    private val superGamepad: SuperGamepad,
    private val block: SuperGamepadDslBuilder.() -> Unit
) {

    private val buttons = mutableMapOf<Button, SuperGamepadDslButtonBuilder>()

    operator fun Button.invoke(block: SuperGamepadDslButtonBuilder.() -> Unit) {
        buttons[this] = SuperGamepadDslButtonBuilder(block)
    }

    fun build() {
        block()

        superGamepad.registerEvent(object: SuperGamepadEvent() {
            override fun buttonsPressed(btts: Buttons) {
                buttons.forEach {
                    if(btts.`is`(it.key))
                        it.value.pressedCallback?.invoke()
                }
            }

            override fun buttonsBeingPressed(btts: Buttons) {
                buttons.forEach {
                    if(btts.`is`(it.key))
                        it.value.pressingCallback?.invoke()
                }
            }

            override fun buttonsReleased(btts: Buttons) {
                buttons.forEach {
                    if(btts.`is`(it.key))
                        it.value.releasedCallback?.invoke()
                }
            }
        })
    }

}