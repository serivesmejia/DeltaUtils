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

package com.github.serivesmejia.deltadrive.drive.holonomic

import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltadrive.utils.DistanceUnit
import com.github.serivesmejia.deltadrive.utils.task.Task
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("UNUSED")
class EncoderDriveHolonomic
/**
 * Constructor for the encoder drive class
 * @param hdw The initialized hardware containing all the chassis motors
 * @param telemetry The current OpMode telemetry to show movement info.
 * @param parameters Encoder parameters, in order to calculate the ticks per inch for each motor
 */
(private val hdw: DeltaHardwareHolonomic,
 private var parameters: EncoderDriveParameters,
 private val telemetry: Telemetry? = null) {

    private val runtime = ElapsedTime()

    init {
        hdw.runMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        hdw.runMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    private fun encoderDrive(speed: Double,
                             frontleft: Double,
                             frontright: Double,
                             backleft: Double,
                             backright: Double,
                             timeoutS: Double,
                             rightTurbo: Double,
                             leftTurbo: Double,
                             movementDescription: String) : Task<Unit> {

        var fl = frontleft
        var fr = frontright
        var bl = backleft
        var br = backright

        parameters.secureParameters()

        val ticksPerInch = parameters.TICKS_PER_REV * parameters.DRIVE_GEAR_REDUCTION.ratioAsDecimal /
                             (parameters.WHEEL_DIAMETER_INCHES * Math.PI)

        if (parameters.DISTANCE_UNIT === DistanceUnit.CENTIMETERS) {
            fl *= 0.393701
            fr *= 0.393701
            bl *= 0.393701
            br *= 0.393701
        }

        // Determine new target position, and pass to motor controller
        val newFrontLeftTarget = (hdw.wheelFrontLeft.currentPosition + (fl * ticksPerInch)).roundToInt()
        val newFrontRightTarget = (hdw.wheelFrontRight.currentPosition + (fr * ticksPerInch)).roundToInt()
        val newBackLeftTarget = (hdw.wheelBackLeft.currentPosition + (bl * ticksPerInch)).roundToInt()
        val newBackRightTarget = (hdw.wheelBackRight.currentPosition + (br * ticksPerInch)).roundToInt()

        hdw.setTargetPositions(newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget)

        val beforeRunMode = hdw.runMode

        // Turn On RUN_TO_POSITION
        hdw.runMode = DcMotor.RunMode.RUN_TO_POSITION

        // reset the timeout time and start motion.
        runtime.reset()

        val leftPower = abs(speed) * leftTurbo
        val rightPower = abs(speed) * rightTurbo

        return Task(parameters.TASK_COMMAND_REQUIREMENTS) {
            first {
                hdw.setMotorPowers(leftPower, rightPower, leftPower, rightPower)
            }

            if(parameters.SHOW_CURRENT_DISTANCE) {
                telemetry?.addData("[Movement]", movementDescription)

                telemetry?.addData("[Current]", "%7d : %7d : %7d : %7d",
                    hdw.wheelFrontLeft.currentPosition,
                    hdw.wheelFrontRight.currentPosition,
                    hdw.wheelBackLeft.currentPosition,
                    hdw.wheelBackRight.currentPosition)

                telemetry?.addData("[Target]", "%7d : %7d : %7d : %7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget)
            }

            telemetry?.update()

            // finish task until there's is no time left or no motors are running.
            // Note: We use (isBusy() && isBusy()) in the repeat test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            if(runtime.seconds() < timeoutS &&
                hdw.wheelFrontRight.isBusy &&
                hdw.wheelFrontLeft.isBusy &&
                hdw.wheelBackLeft.isBusy &&
                hdw.wheelBackRight.isBusy &&
                !Thread.currentThread().isInterrupted
            ) { //when it's finished
                telemetry?.update() //clear telemetry

                // Stop all motion
                hdw.setMotorPowers(0.0, 0.0, 0.0, 0.0)
                // Turn off RUN_TO_POSITION
                hdw.runMode = beforeRunMode

                end() //end the task
            }

        }

    }

    fun forward(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)

        return encoderDrive(
            speed, d, d, d, d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "forward"
        )
    }

    fun backwards(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)

        return encoderDrive(
            speed, -d, -d, -d, -d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "backwards"
        )
    }

    fun strafeLeft(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)

        return encoderDrive(
            speed, -d, d, d, -d,
            timeoutS,
            parameters.RIGHT_WHEELS_STRAFE_TURBO, parameters.LEFT_WHEELS_STRAFE_TURBO,
            "strafeLeft"
        )
    }

    fun strafeRight(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
            speed, d, -d, -d, d,
            timeoutS, parameters.RIGHT_WHEELS_STRAFE_TURBO, parameters.LEFT_WHEELS_STRAFE_TURBO, "strafeRight")
    }

    fun turnRight(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
            speed, d, -d, d, -d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "turnRight")
    }

    fun turnLeft(distance: Double, speed: Double, timeoutS: Double): Task<Unit> {
        val d = abs(distance)
        return encoderDrive(
            speed, -d, d, -d, d,
            timeoutS,
            parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,
            "turnLeft"
        )
    }

}