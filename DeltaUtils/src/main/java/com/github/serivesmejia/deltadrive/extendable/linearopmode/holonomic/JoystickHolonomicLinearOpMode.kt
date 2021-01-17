/*
 * Copyright (c) 2020 FTC Delta Robotics #9351 - Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.serivesmejia.deltadrive.extendable.linearopmode.holonomic

import com.github.serivesmejia.deltadrive.drive.holonomic.JoystickDriveHolonomic
import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltaevent.gamepad.SuperGamepad
import com.github.serivesmejia.deltamath.DeltaMathUtil
import com.qualcomm.robotcore.hardware.Gamepad

open class JoystickHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode() {

    open lateinit var joystick: JoystickDriveHolonomic
    open lateinit var superGamepad1: SuperGamepad
    open lateinit var superGamepad2: SuperGamepad

    override fun runOpMode() {
        superGamepad1 = SuperGamepad(gamepad1)
        superGamepad2 = SuperGamepad(gamepad2)

        performInit()

        joystick = JoystickDriveHolonomic(deltaHardware as DeltaHardwareHolonomic, gamepad1)

        _runOpMode()
    }

    fun joystick(gamepad: Gamepad, controlSpeedWithTriggers: Boolean, maxMinusPower: Double) {
        if (controlSpeedWithTriggers) {
            when {
                gamepad.left_trigger > 0.1 -> {
                    joystick.update(1.0 - DeltaMathUtil.clamp(gamepad.left_trigger.toDouble(), 0.0, maxMinusPower))
                }
                gamepad.right_trigger > 0.1 -> {
                    joystick.update(1.0 - DeltaMathUtil.clamp(gamepad.right_trigger.toDouble(), 0.0, maxMinusPower))
                }
                else -> {
                    joystick.update(1.0)
                }
            }
        } else {
            joystick.update(1.0)
        }
    }

}