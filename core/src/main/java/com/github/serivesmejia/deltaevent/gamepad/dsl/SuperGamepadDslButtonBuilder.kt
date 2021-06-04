package com.github.serivesmejia.deltaevent.gamepad.dsl

import com.github.serivesmejia.deltaevent.gamepad.button.Button

class SuperGamepadDslButtonBuilder {

    internal val callbacks = mutableMapOf<Button, () -> Unit>()

    operator fun Button.invoke(callback: () -> Unit) {
        callbacks[this] = callback
    }

}