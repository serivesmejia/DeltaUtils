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

import com.github.serivesmejia.deltadrive.drive.holonomic.IMUDriveHolonomic
import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltamath.geometry.Twist2d


open class IMUHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode() {

    lateinit var imuDrive: IMUDriveHolonomic

    /**
     * IMU parameters that can be defined
     */
    var imuParameters = IMUDriveParameters()

    override fun runOpMode() {
        performInit()

        imuDrive = IMUDriveHolonomic((deltaHardware as DeltaHardwareHolonomic?)!!, telemetry)

        imuDrive.initIMU(imuParameters)
        imuDrive.waitForIMUCalibration()

        Thread(Runnable{
            waitForStart()
            if (!imuParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", DEF_IMU_PARAMS)
            }
            telemetry.update()
        }).start()

        _runOpMode()
    }


    fun rotate(rot: Rot2d, power: Double, timeoutS: Double): Twist2d {
        return imuDrive.rotate(rot, power, timeoutS)
    }

    fun getRobotAngle(): Rot2d {
        return imuDrive.getRobotAngle()
    }

}