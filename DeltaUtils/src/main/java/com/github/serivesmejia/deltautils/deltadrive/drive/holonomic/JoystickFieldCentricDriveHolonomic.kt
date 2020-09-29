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
import com.github.serivesmejia.deltautils.deltamath.DeltaMathUtil
import com.github.serivesmejia.deltautils.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltautils.deltamath.geometry.Vec2d
import com.qualcomm.robotcore.hardware.Gamepad

class JoystickFieldCentricDriveHolonomic {

    //wheel motor power
    var wheelFrontRightPower = 0.0
    var wheelFrontLeftPower = 0.0
    var wheelBackRightPower = 0.0
    var wheelBackLeftPower = 0.0

    var turbo = 0.0

    private var hdw: DeltaHardwareHolonomic? = null

    private var gamepad: Gamepad = Gamepad()

    /**
     * Constructor for the Joystick Field Centric Drive
     * @param hdw The initialized hardware containing all the chassis motors
     */
    constructor (hdw: DeltaHardwareHolonomic, gamepad: Gamepad) {
        this.hdw = hdw
        this.gamepad = gamepad
    }

    fun setGamepad(gamepad: Gamepad){
        this.gamepad = gamepad;
    }

    /**
     * Drives the robot from the perspective of the driver. No matter the orientation of the
     * robot, pushing forward on the drive stick will always drive the robot away
     * from the driver.
     *
     * @param drive    the horizontal speed of the robot, derived from input
     * @param strafe    the vertical speed of the robot, derived from input
     * @param turn the turn speed of the robot, derived from input
     * @param degrees the heading of the robot, derived from the gyro
     */
    fun update(turbo: Double) {

        var degrees = 0.0

        var drive = -gamepad.left_stick_y as Double
        var strafe = gamepad.left_stick_x as Double
        var turnSpeed = gamepad.right_stick_x  as Double

        this.turbo = turbo

        drive = DeltaMathUtil.clamp(drive, -1.0, 1.0)
        strafe = DeltaMathUtil.clamp(strafe, -1.0, 1.0)

        turnSpeed = DeltaMathUtil.clamp(turnSpeed, -1.0, 1.0)

        var driveStrafeVec = Vec2d(drive, strafe)
        driveStrafeVec.rotate(Rot2d.degrees(-degrees))

        val theta = Math.atan2(strafe, drive)
        val wheelSpeeds = DoubleArray(4)

        wheelFrontLeftPower = driveStrafeVec.mag() * Math.sin(theta + Math.PI / 4) + turnSpeed
        wheelFrontRightPower = driveStrafeVec.mag() * Math.sin(theta - Math.PI / 4) - turnSpeed
        wheelBackLeftPower = driveStrafeVec.mag() * Math.sin(theta - Math.PI / 4) + turnSpeed
        wheelBackRightPower = driveStrafeVec.mag() * Math.sin(theta + Math.PI / 4) - turnSpeed

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

        hdw!!.setAllMotorPower(wheelFrontLeftPower, wheelFrontRightPower, wheelBackLeftPower, wheelBackRightPower)

    }

}