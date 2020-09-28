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

import com.github.serivesmejia.deltautils.deltadrive.extendable.linearopmode.ExtendableLinearOpMode
import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltautils.deltadrive.utils.Invert
import com.qualcomm.robotcore.hardware.DcMotor


open class ExtendableHolonomicLinearOpMode : ExtendableLinearOpMode() {

    open var frontLeft: DcMotor? = null
    open var frontRight: DcMotor? = null
    open var backLeft: DcMotor? = null
    open var backRight: DcMotor? = null

    override fun runOpMode() {}

    private var alreadyPerformedInit = false

    override fun performInit() {

        if (alreadyPerformedInit) {
            return
        }

        alreadyPerformedInit = true

        setup()

        if (frontLeft == null || frontRight == null || backLeft == null || backRight == null) {
            telemetry.addData("[/!\\]", "OpMode will not start in order to avoid Robot Controller crash:")
            telemetry.addData("frontLeft", if (frontLeft == null) "is null" else "OK")
            telemetry.addData("frontRight", if (frontRight == null) "is null" else "OK")
            telemetry.addData("backLeft", if (backLeft == null) "is null" else "OK")
            telemetry.addData("backRight", if (backRight == null) "is null" else "OK")
            telemetry.addData("POSSIBLE SOLUTION 1", "Override setup() method in your OpMode class and\ndefine the null motor variables specified above.")
            telemetry.addData("POSSIBLE SOLUTION 2", "Check that all your motors are correctly named and\nthat they are gotten from the hardwareMap")
            telemetry.update()
            while (opModeIsActive());
            return
        }

        deltaHardware = DeltaHardwareHolonomic(Invert.RIGHT_SIDE)
        (deltaHardware as DeltaHardwareHolonomic).initHardware(frontLeft!!, frontRight!!, backLeft!!, backRight!!, WHEELS_BRAKE)
    }

}