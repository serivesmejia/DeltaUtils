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

package com.github.serivesmejia.deltautils.deltadrive.drive.hdrive

import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHDrive
import com.github.serivesmejia.deltautils.deltamath.DeltaMathUtil
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.math.abs
import kotlin.math.max

/**
 * Class to control an HDrive chassis during teleop using a gamepad's joysticks.
 */
class JoystickDriveHDrive {

    //wheel motor power
    var wheelsLeftPower = 0.0
    var wheelsRightPower = 0.0
    var wheelMiddlePower = 0.0

    var turbo = 0.0

    private var hdw: DeltaHardwareHDrive? = null

    private var gamepad: Gamepad = Gamepad()

    /**
     * Constructor for the Joystick Drive
     * @param hdw The initialized hardware containing all the chassis motors
     */
    constructor (hdw: DeltaHardwareHDrive, gamepad: Gamepad) {
        this.hdw = hdw
        this.gamepad = gamepad;
    }

    fun setGamepad(gamepad: Gamepad){
        this.gamepad = gamepad;
    }

    /**
     * Control an H-Drive chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called always in the teleop repeat to update the motor powers
     * @param gamepad the gamepad used to control the chassis.
     * @param turbo the chassis % of speed, from 0 to 1
     */
    fun update(turbo: Double){

        var turbo = abs(turbo)
        turbo = DeltaMathUtil.clamp(turbo, 0.0, 1.0)

        this.turbo = turbo

        val drive = -gamepad.left_stick_y.toDouble()
        val strafe = gamepad.left_stick_x.toDouble()
        val turn = gamepad.right_stick_x.toDouble()

        wheelsRightPower = drive - turn
        wheelsLeftPower = drive + turn
        wheelMiddlePower = strafe

        val max: Double = max(abs(wheelsLeftPower), abs(wheelsRightPower))

        if (max > 1.0) {
            wheelsLeftPower /= max
            wheelsRightPower /= max
        }

        wheelsLeftPower *= turbo
        wheelsRightPower *= turbo
        wheelMiddlePower *= turbo

        hdw!!.setAllMotorPower(wheelsLeftPower, wheelsRightPower, wheelMiddlePower)
    }

}