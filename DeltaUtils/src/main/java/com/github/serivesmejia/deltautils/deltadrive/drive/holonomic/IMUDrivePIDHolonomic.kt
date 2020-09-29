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

import com.github.serivesmejia.deltautils.deltadrive.drive.ExtendableIMUDrivePID
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardware
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltautils.deltadrive.utils.DistanceUnit
import com.github.serivesmejia.deltautils.deltapid.PIDController
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.roundToInt

class IMUDrivePIDHolonomic(hdw: DeltaHardwareHolonomic, telemetry: Telemetry) : ExtendableIMUDrivePID(hdw, telemetry, DeltaHardware.Type.HOLONOMIC) {

    private var runtime = ElapsedTime()

    override fun encoderPIDDrive(speed: Double,
                        frontleft: Double,
                        frontright: Double,
                        backleft: Double,
                        backright: Double,
                        timeoutS: Double,
                        rightTurbo: Double,
                        leftTurbo: Double,
                        movementDescription: String) {

        encoderDriveParameters.secureParameters()

        val initialRobotHeading = imu!!.getAngle().getDegrees()

        val controller = PIDController(this.getDrivePID())

        controller.setSetpoint(initialRobotHeading).setInitialPower(abs(speed))

        var frontleft = 0.0
        var frontright = 0.0
        var backleft = 0.0
        var backright = 0.0

        val TICKS_PER_INCH = encoderDriveParameters.TICKS_PER_REV * encoderDriveParameters.DRIVE_GEAR_REDUCTION.getRatioAsDecimal() /
                (encoderDriveParameters.WHEEL_DIAMETER_INCHES * Math.PI)

        if (encoderDriveParameters.DISTANCE_UNIT === DistanceUnit.CENTIMETERS) {
            frontleft *= 0.3937014
            frontright *= 0.3937014
            backleft *= 0.3937014
            backright *= 0.3937014
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

        var frontleftpower = abs(speed) * leftTurbo
        var frontrightpower = abs(speed) * rightTurbo
        var backleftpower = abs(speed) * leftTurbo
        var backrightpower = abs(speed) * rightTurbo

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

            var powerF = controller.calculate(getRobotAngle().getDegrees())

            frontleftpower = powerF * leftTurbo
            frontrightpower = powerF * rightTurbo
            backleftpower = powerF * leftTurbo
            backrightpower = powerF * rightTurbo

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
            telemetry.addData("[PID Error]", controller.getCurrentError())
            telemetry.update()

        }

        telemetry.update()

        // Stop all motion
        hdw.setAllMotorPower(0.0, 0.0, 0.0, 0.0)

        // Turn off RUN_TO_POSITION
        hdw.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER)

    }


}