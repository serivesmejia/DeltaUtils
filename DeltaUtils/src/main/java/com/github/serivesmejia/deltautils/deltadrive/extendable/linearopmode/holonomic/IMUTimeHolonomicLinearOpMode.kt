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

package com.github.serivesmejia.deltautils.deltadrive.extendable.linearopmode.holonomic

import com.github.serivesmejia.deltautils.deltadrive.drive.holonomic.IMUDriveHolonomic
import com.github.serivesmejia.deltautils.deltadrive.drive.holonomic.TimeDriveHolonomic
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltautils.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltautils.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltautils.deltamath.geometry.Twist2d


open class IMUTimeHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode(){

    open var imuDrive: IMUDriveHolonomic? = null
    open var timeDrive: TimeDriveHolonomic? = null

    /**
     * IMU parameters that can be defined
     */
    val imuParameters = IMUDriveParameters()

    override fun runOpMode() {
        performInit()
        imuDrive = IMUDriveHolonomic(deltaHardware as DeltaHardwareHolonomic, telemetry)
        imuDrive!!.initIMU(imuParameters)

        imuDrive!!.waitForIMUCalibration()

        timeDrive = TimeDriveHolonomic(deltaHardware as DeltaHardwareHolonomic, telemetry)

        Thread({
            waitForStart()
            if (!imuParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", DEF_IMU_PARAMS)
            }
            telemetry.update()
        }).start()

        _runOpMode()
    }

    fun rotate(rot: Rot2d, power: Double, timeoutS: Double): Twist2d {
        return imuDrive!!.rotate(rot, power, timeoutS)
    }

    fun forward(power: Double, timeSecs: Double) {
        timeDrive!!.forward(power, timeSecs)
    }

    fun backwards(power: Double, timeSecs: Double) {
        timeDrive!!.backwards(power, timeSecs)
    }

    fun strafeLeft(power: Double, timeSecs: Double) {
        timeDrive!!.strafeLeft(power, timeSecs)
    }

    fun strafeRight(power: Double, timeSecs: Double) {
        timeDrive!!.strafeRight(power, timeSecs)
    }

    fun turnLeft(power: Double, timeSecs: Double) {
        timeDrive!!.turnLeft(power, timeSecs)
    }

    fun turnRight(power: Double, timeSecs: Double) {
        timeDrive!!.strafeRight(power, timeSecs)
    }

    fun getRobotAngle(): Rot2d? {
        return imuDrive!!.getRobotAngle()
    }

}