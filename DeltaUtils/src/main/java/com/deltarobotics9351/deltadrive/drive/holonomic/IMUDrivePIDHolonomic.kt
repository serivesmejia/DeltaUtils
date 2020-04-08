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

package com.deltarobotics9351.deltadrive.drive.holonomic

import com.deltarobotics9351.deltadrive.drive.ExtendableIMUDrivePID
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters
import com.deltarobotics9351.deltadrive.utils.DistanceUnit
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import java.lang.Math.round
import kotlin.math.round
import kotlin.math.roundToInt


class IMUDrivePIDHolonomic(hdw: DeltaHardwareHolonomic, telemetry: Telemetry) : ExtendableIMUDrivePID(hdw, telemetry, DeltaHardware.Type.HOLONOMIC) {

    var runtime = ElapsedTime()

    override fun encoderPIDDrive(speed: Double,
                        frontleft: Double,
                        frontright: Double,
                        backleft: Double,
                        backright: Double,
                        timeoutS: Double,
                        rightTurbo: Double,
                        leftTurbo: Double,
                        movementDescription: String,
                        initialRobotHeading: Double,
                        imu: ExtendableIMUDrivePID,
                        encoderParameters: EncoderDriveParameters,
                        hdw: DeltaHardware,
                        telemetry: Telemetry) {

        encoderParameters.secureParameters()

        var frontleft = 0.0
        var frontright = 0.0
        var backleft = 0.0
        var backright = 0.0

        val TICKS_PER_INCH = encoderParameters.TICKS_PER_REV * encoderParameters.DRIVE_GEAR_REDUCTION /
                (encoderParameters.WHEEL_DIAMETER_INCHES * Math.PI)

        if (encoderParameters.DISTANCE_UNIT === DistanceUnit.CENTIMETERS) {
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
        newFrontLeftTarget = (hdw.wheelFrontLeft!!.currentPosition + (frontleft * TICKS_PER_INCH)).roundToInt()
        newFrontRightTarget = (hdw.wheelFrontRight!!.currentPosition + (frontright * TICKS_PER_INCH)).roundToInt()
        newBackLeftTarget = (hdw.wheelBackLeft!!.currentPosition + (backleft * TICKS_PER_INCH)).roundToInt()
        newBackRightTarget = (hdw.wheelBackRight!!.currentPosition + (backright * TICKS_PER_INCH)).roundToInt()

        hdw.setTargetPositions(newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget)

        // Turn On RUN_TO_POSITION
        hdw.setRunModes(DcMotor.RunMode.RUN_TO_POSITION)

        // reset the timeout time and start motion.
        runtime.reset()

        var frontleftpower = Math.abs(speed) * leftTurbo
        var frontrightpower = Math.abs(speed) * rightTurbo
        var backleftpower = Math.abs(speed) * leftTurbo
        var backrightpower = Math.abs(speed) * rightTurbo

        hdw.setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)

        var travelledAverageInches = 0.0
        var error: Double

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the repeat test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        while (runtime.seconds() < timeoutS &&
                hdw.wheelFrontRight!!.isBusy &&
                hdw.wheelFrontLeft!!.isBusy &&
                hdw.wheelBackLeft!!.isBusy &&
                hdw.wheelBackRight!!.isBusy && !Thread.interrupted()) {

            val averageCurrentTicks =
                    (hdw.wheelFrontRight!!.currentPosition +
                    hdw.wheelFrontLeft!!.currentPosition +
                    hdw.wheelBackLeft!!.currentPosition +
                    hdw.wheelBackRight!!.currentPosition) / 4.0

            travelledAverageInches = averageCurrentTicks / TICKS_PER_INCH

            //----- PID CODE START -----
            error = initialRobotHeading - imu.getRobotAngle()!!.getDegrees()


            //----- PID CODE END -----
            telemetry.addData("[Movement]", movementDescription)
            telemetry.addData("[Target]", "%7d : %7d : %7d : %7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget)
            telemetry.addData("[Current]", "%7d : %7d : %7d : %7d",
                    hdw.wheelFrontLeft!!.currentPosition,
                    hdw.wheelFrontRight!!.currentPosition,
                    hdw.wheelBackLeft!!.currentPosition,
                    hdw.wheelBackRight!!.currentPosition)
            telemetry.addData("[Travelled Avg Inches]", travelledAverageInches)
            telemetry.update()
        }
        telemetry.update()

        // Stop all motion
        hdw.setAllMotorPower(0.0, 0.0, 0.0, 0.0)

        // Turn off RUN_TO_POSITION
        hdw.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER)
    }


}