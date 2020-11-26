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
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltautils.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltautils.deltadrive.utils.Task


open class EncoderHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode() {

    open var encoderDrive: EncoderDriveHolonomic? = null
    open var encoderParameters = EncoderDriveParameters()

    override fun runOpMode() {
        performInit()
        encoderDrive = EncoderDriveHolonomic(deltaHardware as DeltaHardwareHolonomic, telemetry, encoderParameters)

        Thread {
            waitForStart()
            if (!encoderParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", DEF_ENCODER_PARAMS)
            }
            telemetry.update()
        }.start()

        _runOpMode()
    }

    fun forward(inches: Double, speed: Double, timeOutSecs: Double): Task {
        return encoderDrive!!.forward(inches, speed, timeOutSecs)
    }

    fun backwards(inches: Double, speed: Double, timeOutSecs: Double): Task {
        return encoderDrive!!.backwards(inches, speed, timeOutSecs)
    }

    fun strafeLeft(inches: Double, speed: Double, timeOutSecs: Double): Task {
        return encoderDrive!!.strafeLeft(inches, speed, timeOutSecs)
    }

    fun strafeRight(inches: Double, speed: Double, timeOutSecs: Double): Task {
        return encoderDrive!!.strafeRight(inches, speed, timeOutSecs)
    }

    fun turnLeft(inches: Double, speed: Double, timeOutSecs: Double): Task {
        return encoderDrive!!.turnLeft(inches, speed, timeOutSecs)
    }

    fun turnRight(inches: Double, speed: Double, timeOutSecs: Double): Task{
        return encoderDrive!!.turnRight(inches, speed, timeOutSecs)
    }

}