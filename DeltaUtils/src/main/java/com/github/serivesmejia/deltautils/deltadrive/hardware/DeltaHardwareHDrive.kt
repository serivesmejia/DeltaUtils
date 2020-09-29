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

package com.github.serivesmejia.deltautils.deltadrive.hardware

import com.github.serivesmejia.deltautils.deltadrive.utils.Invert
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction

import com.qualcomm.robotcore.hardware.DcMotor.RunMode

class DeltaHardwareHDrive(invert: Invert) : DeltaHardware(invert) {

    init {
        type = Type.HDRIVE
    }

    /**
     * Initialize motors.
     * @param frontleft The front left motor of the chassis.
     * @param frontright The front right motor of the chassis.
     * @param backleft The back left motor of the chassis.
     * @param backright The back right motor of the chassis.
     * @param brake brake the motors when their power is 0
     */
    override fun initHardware(frontleft: DcMotor, frontright: DcMotor, backleft: DcMotor, backright: DcMotor, brake: Boolean) {
        wheelFrontLeft = frontleft
        wheelFrontRight = frontright
        wheelBackLeft = backleft
        wheelBackRight = backright

        wheelFrontLeft!!.direction = Direction.FORWARD //all motors need to be ALWAYS FORWARD for the drive classes to work correctly
        wheelFrontRight!!.direction = Direction.FORWARD
        wheelBackLeft!!.direction = Direction.FORWARD
        wheelBackRight!!.direction = Direction.FORWARD

        wheelFrontRight!!.power = 0.0 //just in case.
        wheelBackRight!!.power = 0.0
        wheelFrontLeft!!.power = 0.0
        wheelBackLeft!!.power = 0.0

        updateChassisMotorsArray()

        setBrakes(brake)

        setRunModes(RunMode.RUN_WITHOUT_ENCODER)
    }

    override fun updateChassisMotorsArray() {
        chassisMotorsArray[0] = wheelFrontLeft
        chassisMotorsArray[1] = wheelFrontRight
        chassisMotorsArray[2] = wheelBackLeft
        chassisMotorsArray[3] = wheelBackRight
    }

    override fun setAllMotorPower(frontleft: Double, frontright: Double, backleft: Double, backright: Double) {
        when (invert) {
            Invert.RIGHT_SIDE -> {
                wheelFrontLeft!!.power = frontleft
                wheelFrontRight!!.power = -frontright
                wheelBackLeft!!.power = backleft
                wheelBackRight!!.power = -backright
            }
            Invert.LEFT_SIDE -> {
                wheelFrontLeft!!.power = -frontleft
                wheelFrontRight!!.power = frontright
                wheelBackLeft!!.power = -backleft
                wheelBackRight!!.power = backright
            }
            Invert.BOTH_SIDES -> {
                wheelFrontLeft!!.power = -frontleft
                wheelFrontRight!!.power = -frontright
                wheelBackLeft!!.power = -backleft
                wheelBackRight!!.power = -backright
            }
            Invert.RIGHT_SIDE_MIDDLE -> {
                wheelFrontLeft!!.power = frontleft
                wheelFrontRight!!.power = -frontright
                wheelBackLeft!!.power = backleft
                wheelBackRight!!.power = -backright
            }
            Invert.LEFT_SIDE_MIDDLE -> {
                wheelFrontLeft!!.power = -frontleft
                wheelFrontRight!!.power = frontright
                wheelBackLeft!!.power = -backleft
                wheelBackRight!!.power = backright
            }
            Invert.BOTH_SIDES_MIDDLE -> {
                wheelFrontLeft!!.power = -frontleft
                wheelFrontRight!!.power = -frontright
                wheelBackLeft!!.power = -backleft
                wheelBackRight!!.power = -backright
            }
            else -> {
                wheelFrontLeft!!.power = frontleft
                wheelFrontRight!!.power = frontright
                wheelBackLeft!!.power = backleft
                wheelBackRight!!.power = backright
            }
        }
    }


    override fun setTargetPositions(frontleft: Int, frontright: Int, backleft: Int, backright: Int) {
        when (invert) {
            Invert.RIGHT_SIDE -> {
                wheelFrontLeft!!.targetPosition = frontleft
                wheelFrontRight!!.targetPosition = -frontright
                wheelBackLeft!!.targetPosition = backleft
                wheelBackRight!!.targetPosition = -backright
            }
            Invert.LEFT_SIDE -> {
                wheelFrontLeft!!.targetPosition = -frontleft
                wheelFrontRight!!.targetPosition = frontright
                wheelBackLeft!!.targetPosition = -backleft
                wheelBackRight!!.targetPosition = backright
            }
            Invert.BOTH_SIDES -> {
                wheelFrontLeft!!.targetPosition = -frontleft
                wheelFrontRight!!.targetPosition = -frontright
                wheelBackLeft!!.targetPosition = -backleft
                wheelBackRight!!.targetPosition = -backright
            }
            Invert.RIGHT_SIDE_MIDDLE -> {
                wheelFrontLeft!!.targetPosition = frontleft
                wheelFrontRight!!.targetPosition = -frontright
                wheelBackLeft!!.targetPosition = backleft
                wheelBackRight!!.targetPosition = -backright
            }
            Invert.LEFT_SIDE_MIDDLE -> {
                wheelFrontLeft!!.targetPosition = -frontleft
                wheelFrontRight!!.targetPosition = frontright
                wheelBackLeft!!.targetPosition = -backleft
                wheelBackRight!!.targetPosition = backright
            }
            Invert.BOTH_SIDES_MIDDLE -> {
                wheelFrontLeft!!.targetPosition = -frontleft
                wheelFrontRight!!.targetPosition = -frontright
                wheelBackLeft!!.targetPosition = -backleft
                wheelBackRight!!.targetPosition = -backright
            }
            else -> {
                wheelFrontLeft!!.targetPosition = frontleft
                wheelFrontRight!!.targetPosition = frontright
                wheelBackLeft!!.targetPosition = backleft
                wheelBackRight!!.targetPosition = backright
            }
        }
    }

    override fun setBrakes(brake: Boolean) {
        updateChassisMotorsArray()
        for (motor: DcMotor? in chassisMotorsArray) {
            if (brake) {
                motor!!.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            } else motor!!.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        }
    }

     override fun setRunModes(runMode: RunMode) {
        updateChassisMotorsArray()
        for (motor in chassisMotorsArray) {
            if (motor != null) {
                motor.mode = runMode
            }
        }
    }

}