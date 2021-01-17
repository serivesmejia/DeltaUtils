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

import com.github.serivesmejia.deltadrive.drive.holonomic.IMUDrivePIDHolonomic
import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltamath.geometry.Twist2d
import com.github.serivesmejia.deltapid.PIDCoefficients


open class IMUPIDHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode() {

    lateinit var imuDrive: IMUDrivePIDHolonomic

    /**
     * IMU parameters that can be defined
     */
    val imuParameters = IMUDriveParameters()

    override fun runOpMode() {
        performInit()
        imuDrive = IMUDrivePIDHolonomic(deltaHardware as DeltaHardwareHolonomic, telemetry)
        imuDrive.initIMU(imuParameters)

        imuDrive.waitForIMUCalibration()

        Thread {
            waitForStart()
            if (!imuParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", DEF_IMU_PARAMS)
            }
            telemetry.update()
        }.start()

        _runOpMode()
    }

    /**
     * Set the rotate PID coefficients
     * @param pid the PID coefficients
     */
    fun setRotatePID(pid: PIDCoefficients) {
        imuDrive.setRotatePID(pid)
    }

    /**
     * @return the rotate Proportional coefficient
     */
    fun getRotateP(): Double {
        return imuDrive.getRotateP()
    }

    /**
     * @return the rotate Integral coefficient
     */
    fun getRotateI(): Double {
        return imuDrive.getRotateI()
    }

    /**
     * @return the rotate Derivative coefficient
     */
    fun getRotateD(): Double {
        return imuDrive.getRotateD()
    }

    /**
     * @return the current rotate PIDCoefficients object
     */
    fun getRotatePID(): PIDCoefficients {
        return imuDrive.getRotatePID()
    }

    fun rotate(rot: Rot2d, power: Double, timeoutS: Double): Twist2d {
        return imuDrive.rotate(rot, power, timeoutS)
    }

    fun getRobotAngle(): Rot2d {
        return imuDrive.getRobotAngle()
    }

}