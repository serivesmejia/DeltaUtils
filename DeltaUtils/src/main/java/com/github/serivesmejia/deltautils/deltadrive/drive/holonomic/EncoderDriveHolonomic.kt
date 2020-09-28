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
import com.github.serivesmejia.deltautils.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltautils.deltadrive.utils.DistanceUnit
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.roundToInt


class EncoderDriveHolonomic {

    private var hdw: DeltaHardwareHolonomic? = null

    private var telemetry: Telemetry? = null

    private val runtime = ElapsedTime()

    private var parameters: EncoderDriveParameters? = null

    /**
     * Constructor for the encoder drive class
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry The current OpMode telemetry to show movement info.
     * @param parameters Encoder parameters, in order to calculate the ticks per inch for each motor
     */
    constructor (hdw: DeltaHardwareHolonomic, telemetry: Telemetry, parameters: EncoderDriveParameters) {
        this.hdw = hdw
        this.telemetry = telemetry
        this.parameters = parameters
        hdw.setRunModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
        hdw.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER)
    }

    private fun encoderDrive(speed: Double,
                             frontleft: Double,
                             frontright: Double,
                             backleft: Double,
                             backright: Double,
                             timeoutS: Double,
                             rightTurbo: Double,
                             leftTurbo: Double,
                             movementDescription: String) {

        var frontleft = frontleft
        var frontright = frontright
        var backleft = backleft
        var backright = backright

        parameters!!.secureParameters()

        val TICKS_PER_INCH = parameters!!.TICKS_PER_REV * parameters!!.DRIVE_GEAR_REDUCTION.getRatioAsDecimal() /
                (parameters!!.WHEEL_DIAMETER_INCHES * Math.PI)

        if (parameters!!.DISTANCE_UNIT === DistanceUnit.CENTIMETERS) {
            frontleft *= 0.393701
            frontright *= 0.393701
            backleft *= 0.3937014
            backright *= 0.393701
        }

        val newFrontLeftTarget: Int
        val newFrontRightTarget: Int
        val newBackLeftTarget: Int
        val newBackRightTarget: Int

        // Determine new target position, and pass to motor controller
        newFrontLeftTarget = (hdw!!.wheelFrontLeft!!.currentPosition + (frontleft * TICKS_PER_INCH)).roundToInt()
        newFrontRightTarget = (hdw!!.wheelFrontRight!!.currentPosition + (frontright * TICKS_PER_INCH)).roundToInt()
        newBackLeftTarget = (hdw!!.wheelBackLeft!!.currentPosition + (backleft * TICKS_PER_INCH)).roundToInt()
        newBackRightTarget = (hdw!!.wheelBackRight!!.currentPosition + (backright * TICKS_PER_INCH)).roundToInt()

        hdw!!.setTargetPositions(newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget)

        // Turn On RUN_TO_POSITION
        hdw!!.setRunModes(DcMotor.RunMode.RUN_TO_POSITION)

        // reset the timeout time and start motion.
        runtime.reset()

        hdw!!.wheelFrontLeft!!.power = abs(speed) * leftTurbo
        hdw!!.wheelFrontRight!!.power = abs(speed) * rightTurbo
        hdw!!.wheelBackLeft!!.power = abs(speed) * leftTurbo
        hdw!!.wheelBackRight!!.power = abs(speed) * rightTurbo

        var travelledAverageInches = 0.0

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the repeat test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        while (runtime.seconds() < timeoutS &&
                hdw!!.wheelFrontRight!!.isBusy &&
                hdw!!.wheelFrontLeft!!.isBusy &&
                hdw!!.wheelBackLeft!!.isBusy &&
                hdw!!.wheelBackRight!!.isBusy && !Thread.interrupted()) {

            val averageCurrentTicks = (hdw!!.wheelFrontRight!!.currentPosition +
                    hdw!!.wheelFrontLeft!!.currentPosition +
                    hdw!!.wheelBackLeft!!.currentPosition +
                    hdw!!.wheelBackRight!!.currentPosition) / 4.0

            travelledAverageInches = averageCurrentTicks / TICKS_PER_INCH

            telemetry!!.addData("[Movement]", movementDescription)

            telemetry!!.addData("[Target]", "%7d : %7d : %7d : %7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget)

            telemetry!!.addData("[Current]", "%7d : %7d : %7d : %7d",
                    hdw!!.wheelFrontLeft!!.currentPosition,
                    hdw!!.wheelFrontRight!!.currentPosition,
                    hdw!!.wheelBackLeft!!.currentPosition,
                    hdw!!.wheelBackRight!!.currentPosition)

            telemetry!!.addData("[Travelled Avg Inches]", travelledAverageInches)

            telemetry!!.update()
        }

        telemetry!!.update()

        // Stop all motion
        hdw!!.setAllMotorPower(0.0, 0.0, 0.0, 0.0)

        // Turn off RUN_TO_POSITION
        hdw!!.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER)
    }

    fun forward(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, distance, distance, distance, distance, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, "forward")
    }

    fun backwards(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, -distance, -distance, -distance, -distance, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, "backwards")
    }

    fun strafeLeft(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, -distance, distance, distance, -distance, timeoutS, parameters!!.RIGHT_WHEELS_STRAFE_TURBO, parameters!!.LEFT_WHEELS_STRAFE_TURBO, "strafeLeft")
    }

    fun strafeRight(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, distance, -distance, -distance, distance, timeoutS, parameters!!.RIGHT_WHEELS_STRAFE_TURBO, parameters!!.LEFT_WHEELS_STRAFE_TURBO, "strafeRight")
    }

    fun turnRight(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, distance, -distance, distance, -distance, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, "turnRight")
    }

    fun turnLeft(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, -distance, distance, -distance, distance, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, "turnLeft")
    }

}