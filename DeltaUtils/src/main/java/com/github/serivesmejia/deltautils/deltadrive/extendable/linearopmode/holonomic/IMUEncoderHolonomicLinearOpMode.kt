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
import com.github.serivesmejia.deltautils.deltadrive.drive.holonomic.IMUDriveHolonomic
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltautils.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltautils.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltautils.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltautils.deltamath.geometry.Twist2d


open class IMUEncoderHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode() {

    open var imuDrive: IMUDriveHolonomic? = null
    open var encoderDrive: EncoderDriveHolonomic? = null

    /**
     * IMU parameters that can be defined
     */
    var imuParameters = IMUDriveParameters()

    /**
     * Encoder parameters that can be defined
     */
    var encoderParameters = EncoderDriveParameters()

    override fun runOpMode() {
        performInit()

        imuDrive = IMUDriveHolonomic((deltaHardware as DeltaHardwareHolonomic?)!!, telemetry)

        imuDrive!!.initIMU(imuParameters)
        imuDrive!!.waitForIMUCalibration()

        encoderDrive = EncoderDriveHolonomic((deltaHardware as DeltaHardwareHolonomic?)!!, telemetry, encoderParameters)

        Thread(Runnable(){
            waitForStart()
            if (!encoderParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", DEF_ENCODER_PARAMS)
            }
            if (!imuParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", DEF_IMU_PARAMS)
            }
            telemetry.update()
        }).start()

        _runOpMode()
    }


    /**
     * Overridable void to be executed after all required variables are initialized
     */
    override fun _runOpMode() { }

    /**
     * Overridable void to define all wheel motors, and the uppercase variables
     * Define frontLeft, frontRight, backLeft and backRight DcMotor variables here!
     */
    override fun setup() { }

    fun rotate(rot: Rot2d?, power: Double, timeoutS: Double): Twist2d? {
        return imuDrive!!.rotate(rot!!, power, timeoutS)
    }

    fun forward(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.forward(inches, speed, timeOutSecs)
    }

    fun backwards(inches: Double, speed: Double, timeOutSecs: Double) {
        encoderDrive!!.backwards(inches, speed, timeOutSecs)
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