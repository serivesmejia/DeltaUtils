package com.github.serivesmejia.deltaevent.gamepad.dsl

import com.github.serivesmejia.deltaevent.event.gamepad.SuperGamepadEvent
import com.github.serivesmejia.deltaevent.gamepad.SuperGamepad
import com.github.serivesmejia.deltaevent.gamepad.button.Buttons
import java.lang.IllegalStateException

class SuperGamepadDslBuilder(
    private val superGamepad: SuperGamepad,
    private val block: SuperGamepadDslBuilder.() -> Unit
) {

    private var pressedDsl: SuperGamepadDslButtonBuilder? = null
    private var pressingDsl: SuperGamepadDslButtonBuilder? = null
    private var releasedDsl: SuperGamepadDslButtonBuilder? = null

    fun pressed(block: SuperGamepadDslButtonBuilder.() -> Unit) {
        if(pressedDsl != null)
            throw IllegalStateException("'pressed' block has already been defined for this gamepad!")

        pressedDsl = SuperGamepadDslButtonBuilder()
        block(pressedDsl!!)
    }

    fun pressing(block: SuperGamepadDslButtonBuilder.() -> Unit) {
        if(pressingDsl != null)
            throw IllegalStateException("'pressed' block has already been defined for this gamepad!")

        pressingDsl = SuperGamepadDslButtonBuilder()
        block(pressingDsl!!)
    }

    fun released(block: SuperGamepadDslButtonBuilder.() -> Unit) {
        if(releasedDsl != null)
            throw IllegalStateException("'pressed' block has already been defined for this gamepad!")

        releasedDsl = SuperGamepadDslButtonBuilder()
        block(releasedDsl!!)
    }

    fun build() {
        block()

        superGamepad.registerEvent(object: SuperGamepadEvent() {
            override fun buttonsPressed(buttons: Buttons) {
                pressedDsl?.callbacks?.forEach {
                    if(buttons.`is`(it.key)) it.value()
                }
            }

            override fun buttonsBeingPressed(buttons: Buttons) {
                pressingDsl?.callbacks?.forEach {
                    if(buttons.`is`(it.key)) it.value()
                }
            }

            override fun buttonsReleased(buttons: Buttons) {
                releasedDsl?.callbacks?.forEach {
                    if(buttons.`is`(it.key)) it.value()
                }
            }
        })
    }

}