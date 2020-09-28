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

package com.github.serivesmejia.deltautils.deltadrive.drive.holonomic

import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltautils.deltadrive.utils.Invert
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.Range

/**
 * Class to control an holonomic chassis during teleop using a gamepad's joysticks.
 */
class JoystickDriveHolonomic {

    //wheel motor power
    var wheelFrontRightPower = 0.0
    var wheelFrontLeftPower = 0.0
    var wheelBackRightPower = 0.0
    var wheelBackLeftPower = 0.0

    var turbo = 0.0

    private var hdw: DeltaHardwareHolonomic? = null

    private var gamepad: Gamepad = Gamepad()

    /**
     * Constructor for the Joystick Drive
     * @param hdw The initialized hardware containing all the chassis motors
     */
    constructor (hdw: DeltaHardwareHolonomic, gamepad: Gamepad) {
        this.hdw = hdw
        this.gamepad = gamepad;
    }

    fun setGamepad(gamepad: Gamepad){
        this.gamepad = gamepad;
    }

    /**
     * Control a mecanum chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called always in the teleop repeat to update the motor powers
     * @param gamepad the gamepad used to control the chassis.
     * @param turbo the chassis % of speed, from 0 to 1
     */
    fun update(turbo: Double) {
        var turbo = turbo

        turbo = Math.abs(turbo)
        turbo = Range.clip(turbo, 0.0, 1.0)

        this.turbo = turbo

        val y1 = -gamepad.left_stick_y.toDouble()
        val x1 = gamepad.left_stick_x.toDouble()
        val x2 = gamepad.right_stick_x.toDouble()

        when (hdw!!.invert) {
            Invert.RIGHT_SIDE -> {
                wheelFrontRightPower = -(y1 - x2 - x1)
                wheelBackRightPower = -(y1 - x2 + x1)
                wheelFrontLeftPower = y1 + x2 + x1
                wheelBackLeftPower = y1 + x2 - x1
            }
            Invert.LEFT_SIDE -> {
                wheelFrontRightPower = y1 - x2 - x1
                wheelBackRightPower = y1 - x2 + x1
                wheelFrontLeftPower = -(y1 + x2 + x1)
                wheelBackLeftPower = -(y1 + x2 - x1)
            }
            Invert.BOTH_SIDES -> {
                wheelFrontRightPower = -(y1 - x2 - x1)
                wheelBackRightPower = -(y1 - x2 + x1)
                wheelFrontLeftPower = -(y1 + x2 + x1)
                wheelBackLeftPower = -(y1 + x2 - x1)
            }
            Invert.NO_INVERT -> {
                wheelFrontRightPower = y1 - x2 - x1
                wheelBackRightPower = y1 - x2 + x1
                wheelFrontLeftPower = y1 + x2 + x1
                wheelBackLeftPower = y1 + x2 - x1
            }
        }

        val max = Math.max(Math.abs(wheelFrontRightPower), Math.max(Math.abs(wheelBackRightPower),
                Math.max(Math.abs(wheelFrontLeftPower), Math.abs(wheelBackLeftPower))))

        if (max > 1.0) {
            wheelFrontRightPower /= max
            wheelBackRightPower /= max
            wheelFrontLeftPower /= max
            wheelBackLeftPower /= max
        }

        wheelFrontRightPower *= turbo
        wheelBackRightPower *= turbo
        wheelFrontLeftPower *= turbo
        wheelBackLeftPower *= turbo

        hdw!!.wheelFrontRight!!.power = wheelFrontRightPower
        hdw!!.wheelFrontLeft!!.power = wheelFrontLeftPower
        hdw!!.wheelBackRight!!.power = wheelBackRightPower
        hdw!!.wheelBackLeft!!.power = wheelBackLeftPower
    }

}