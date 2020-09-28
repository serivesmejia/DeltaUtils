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

import com.github.serivesmejia.deltautils.deltadrive.drive.holonomic.TimeDriveHolonomic
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic


open class TimeHolonomicLinearOpMode : ExtendableHolonomicLinearOpMode() {

    open var timeDrive: TimeDriveHolonomic? = null

    override fun runOpMode() {
        performInit()
        timeDrive = TimeDriveHolonomic((deltaHardware as DeltaHardwareHolonomic?)!!, telemetry)
        _runOpMode()
    }

    /**
     * Overridable void to be executed after all required variables are initialized
     */
    override fun _runOpMode() {}

    /**
     * Overridable void to define all wheel motors, and the uppercase variables
     * Define frontLeft, frontRight, backLeft and backRight DcMotor variables here!
     */
    override fun setup() {}

    fun forward(power: Double, timeSecs: Double) {
        timeDrive!!.forward(power, timeSecs)
    }

    fun backwards(power: Double, timeSecs: Double) {
        timeDrive!!.backwards(power, timeSecs)
    }

    fun strafeLeft(power: Double, timeSecs: Double) {
        timeDrive!!.strafeLeft(power, timeSecs)
    }

    fun strafeRight(power: Double, timeSecs: Double) {
        timeDrive!!.strafeRight(power, timeSecs)
    }

    fun turnLeft(power: Double, timeSecs: Double) {
        timeDrive!!.turnLeft(power, timeSecs)
    }

    fun turnRight(power: Double, timeSecs: Double) {
        timeDrive!!.strafeRight(power, timeSecs)
    }

}