package com.github.serivesmejia.deltautils

import com.github.serivesmejia.deltaevent.gamepad.SuperGamepad
import com.github.serivesmejia.deltaevent.gamepad.button.Button
import com.qualcomm.robotcore.hardware.Gamepad

class DeltaSuperGamepadPOC {

    fun gamepad() {
        val gamepad = SuperGamepad(Gamepad())

        gamepad {
            pressed {
                Button.A {

                }
            }

            released {
                Button.B {

                }
            }
        }
    }

}