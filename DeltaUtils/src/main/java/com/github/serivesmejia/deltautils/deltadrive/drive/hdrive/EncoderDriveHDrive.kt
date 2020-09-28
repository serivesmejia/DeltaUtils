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
import com.github.serivesmejia.deltautils.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltautils.deltadrive.utils.DistanceUnit
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.roundToInt


class EncoderDriveHDrive {

    private var hdw: DeltaHardwareHDrive? = null

    private var telemetry: Telemetry? = null

    private val runtime = ElapsedTime()

    private var parameters: EncoderDriveParameters? = null

    /**
     * Constructor for the encoder drive class
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry The current OpMode telemetry to show movement info.
     * @param parameters Encoder parameters, in order to calculate the ticks per inch for each motor
     */
    constructor (hdw: DeltaHardwareHDrive, telemetry: Telemetry, parameters: EncoderDriveParameters) {
        this.hdw = hdw
        this.telemetry = telemetry
        this.parameters = parameters

        hdw.setRunModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
        hdw.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER)
    }

    private fun encoderDrive(speed: Double,
                             left: Double,
                             right: Double,
                             middle: Double,
                             timeoutS: Double,
                             rightTurbo: Double,
                             leftTurbo: Double,
                             middleTurbo: Double,
                             movementDescription: String) {
        var left = left
        var right = right
        var middle = middle

        parameters!!.secureParameters()
        val TICKS_PER_INCH = parameters!!.TICKS_PER_REV * parameters!!.DRIVE_GEAR_REDUCTION.getRatioAsDecimal() /
                (parameters!!.WHEEL_DIAMETER_INCHES * Math.PI)

        if (parameters!!.DISTANCE_UNIT === DistanceUnit.CENTIMETERS) {
            left *= 0.393701
            right *= 0.393701
            middle *= 0.3937014
        }

        val newLeftTarget: Int
        val newRightTarget: Int
        val newMiddleTarget: Int

        // Determine new target position, and pass to motor controller
        newLeftTarget = (hdw!!.wheelsLeft!!.currentPosition + (left * TICKS_PER_INCH)).roundToInt()
        newRightTarget = (hdw!!.wheelsRight!!.currentPosition + (right * TICKS_PER_INCH)).roundToInt()
        newMiddleTarget = (hdw!!.wheelMiddle!!.currentPosition + (middle * TICKS_PER_INCH)).roundToInt()

        hdw!!.setTargetPositions(newLeftTarget, newRightTarget, newMiddleTarget)

        // Turn On RUN_TO_POSITION
        hdw!!.setRunModes(DcMotor.RunMode.RUN_TO_POSITION)

        // reset the timeout time and start motion.
        runtime.reset()

        hdw!!.wheelsLeft!!.power = Math.abs(speed) * leftTurbo
        hdw!!.wheelsRight!!.power = Math.abs(speed) * rightTurbo
        hdw!!.wheelMiddle!!.power = Math.abs(speed) * middleTurbo

        var travelledAverageInches = 0.0

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the repeat test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        while (runtime.seconds() < timeoutS && !Thread.interrupted()) {

            if (middle != 0.0 && !hdw!!.wheelMiddle!!.isBusy) break



            var averageCurrentTicks = if (middle == 0.0) {
                (hdw!!.wheelFrontRight!!.currentPosition +
                        hdw!!.wheelFrontLeft!!.currentPosition) / 2.0
            } else if (middle != 0.0 && left == 0.0 && right == 0.0) {
                hdw!!.wheelMiddle!!.currentPosition.toDouble()
            } else {
                (hdw!!.wheelFrontRight!!.currentPosition +
                        hdw!!.wheelFrontLeft!!.currentPosition
                        + hdw!!.wheelMiddle!!.targetPosition) / 3.0
            }

            travelledAverageInches = averageCurrentTicks / TICKS_PER_INCH

            telemetry!!.addData("[Movement]", movementDescription)

            telemetry!!.addData("[Target]", "%7d : %7d : %7d",
                    newLeftTarget,
                    newRightTarget,
                    newMiddleTarget)

            telemetry!!.addData("[Current]", "%7d : %7d : %7d",
                    hdw!!.wheelsLeft!!.currentPosition,
                    hdw!!.wheelsRight!!.currentPosition,
                    hdw!!.wheelMiddle!!.currentPosition)

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
        encoderDrive(speed, distance, distance, 0.0, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, parameters!!.HDRIVE_WHEEL_STRAFE_TURBO, "forward")
    }

    fun backwards(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, -distance, -distance, 0.0, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, parameters!!.HDRIVE_WHEEL_STRAFE_TURBO, "backwards")
    }

    fun strafeLeft(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, 0.0, 0.0, distance, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, parameters!!.HDRIVE_WHEEL_STRAFE_TURBO, "strafeLeft")
    }

    fun strafeRight(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, 0.0, 0.0, -distance, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, parameters!!.HDRIVE_WHEEL_STRAFE_TURBO, "strafeRight")
    }

    fun turnRight(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, distance, -distance, 0.0, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, parameters!!.HDRIVE_WHEEL_STRAFE_TURBO, "turnRight")
    }

    fun turnLeft(distance: Double, speed: Double, timeoutS: Double) {
        var distance = distance
        distance = Math.abs(distance)
        encoderDrive(speed, -distance, distance, 0.0, timeoutS, parameters!!.RIGHT_WHEELS_TURBO, parameters!!.LEFT_WHEELS_TURBO, parameters!!.HDRIVE_WHEEL_STRAFE_TURBO, "turnLeft")
    }

}