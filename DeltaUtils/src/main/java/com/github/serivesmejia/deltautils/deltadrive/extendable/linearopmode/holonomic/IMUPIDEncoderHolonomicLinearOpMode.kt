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

import com.github.serivesmejia.deltautils.deltadrive.drive.holonomic.EncoderDriveHolonomic
import com.github.serivesmejia.deltautils.deltadrive.drive.holonomic.IMUDrivePIDHolonomic
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltautils.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltautils.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltautils.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltautils.deltamath.geometry.Twist2d
import com.github.serivesmejia.deltautils.deltapid.PIDCoefficients


open class IMUPIDEncoderHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode() {

    open var imuDrive: IMUDrivePIDHolonomic? = null

    open var encoderDrive: EncoderDriveHolonomic? = null

    /**
     * Encoder parameters that can be defined
     */
    var encoderParameters = EncoderDriveParameters()

    /**
     * IMU parameters that can be defined
     */
    var imuParameters = IMUDriveParameters()

    override fun runOpMode() {
        performInit()

        imuDrive = IMUDrivePIDHolonomic((deltaHardware as DeltaHardwareHolonomic?)!!, telemetry)
        imuDrive!!.initIMU(imuParameters)

        encoderDrive = EncoderDriveHolonomic((deltaHardware as DeltaHardwareHolonomic?)!!, telemetry, encoderParameters)

        imuDrive!!.waitForIMUCalibration()

        Thread(Runnable{
            waitForStart()
            if (!encoderParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", DEF_ENCODER_PARAMS)
            }
            telemetry.update()
        }).start()

        _runOpMode()
    }

    /**
     * Set the rotate PID coefficients
     * @param pid the PID coefficients
     */
    fun setRotatePID(pid: PIDCoefficients) {
        imuDrive!!.setRotatePID(pid)
    }

    /**
     * @return the rotate Proportional coefficient
     */
    fun getRotateP(): Double {
        return imuDrive!!.getRotateP()
    }

    /**
     * @return the rotate Integral coefficient
     */
    fun getRotateI(): Double {
        return imuDrive!!.getRotateI()
    }

    /**
     * @return the rotate Derivative coefficient
     */
    fun getRotateD(): Double {
        return imuDrive!!.getRotateD()
    }

    /**
     * @return the current rotate PIDCoefficients object
     */
    fun getRotatePID(): PIDCoefficients {
        return imuDrive!!.getRotatePID()
    }

    /**
     * Set the drive PID coefficients
     * @param pid the PID coefficients
     */
    fun setDrivePID(pid: PIDCoefficients) {
        imuDrive!!.setDrivePID(pid)
    }

    /**
     * @return the drive Proportional coefficient
     */
    fun getDriveP(): Double {
        return imuDrive!!.getDriveP()
    }

    /**
     * @return the drive Integral coefficient
     */
    fun getDriveI(): Double {
        return imuDrive!!.getDriveI()
    }

    /**
     * @return the drive Derivative coefficient
     */
    fun getDriveD(): Double {
        return imuDrive!!.getDriveD()
    }

    /**
     * @return the current rotate PIDCoefficients object
     */
    fun getDrivePID(): PIDCoefficients {
        return imuDrive!!.getDrivePID()!!
    }

    fun rotate(rot: Rot2d, power: Double, timeoutS: Double): Twist2d {
        return imuDrive!!.rotate(rot, power, timeoutS)
    }

    fun forward(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.forward(inches, speed, timeOutSecs)
    }

    fun pidForward(inches: Double, speed: Double, timeOutSecs: Double) {
        imuDrive!!.encoderPIDForward(inches, speed, timeOutSecs)
    }

    fun backwards(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.backwards(inches, speed, timeOutSecs)
    }

    fun pidBackwards(inches: Double, speed: Double, timeOutSecs: Double) {
        imuDrive!!.encoderPIDBackwards(inches, speed, timeOutSecs)
    }

    fun strafeLeft(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.strafeLeft(inches, speed, timeOutSecs)
    }

    fun strafeRight(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.strafeRight(inches, speed, timeOutSecs)
    }

    fun turnLeft(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.turnLeft(inches, speed, timeOutSecs)
    }

    fun turnRight(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.turnRight(inches, speed, timeOutSecs)
    }

    fun getRobotAngle(): Rot2d {
        return imuDrive!!.getRobotAngle()
    }

}