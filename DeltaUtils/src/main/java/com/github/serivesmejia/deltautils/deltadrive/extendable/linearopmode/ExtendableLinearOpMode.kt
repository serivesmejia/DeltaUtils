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

package com.github.serivesmejia.deltautils.deltadrive.extendable.linearopmode

import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardware
import com.github.serivesmejia.deltautils.deltadrive.utils.Invert
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode


open class ExtendableLinearOpMode : LinearOpMode() {

    /**
     * boolean that indicates if motors brake when their power is 0
     * this will only take effect if it is changed in the setup() overriden method
     */
    var WHEELS_BRAKE = true

    val DEF_IMU_PARAMS = "Remember to define IMU constants, IMU functions may not work as expected because parameters are 0 by default."
    val DEF_ENCODER_PARAMS = "Remember to define encoder constants, encoder functions will not work because parameters are 0 by default."

    var deltaHardware: DeltaHardware? = null

    /**
     * Overridable void to be executed after all required variables are initialized
     * (Remember to override setup() and define the 4 DcMotor variables in there!)
     */
    open fun _runOpMode() {}

    /**
     * Overridable void to define all wheel motors, and the uppercase variables
     * Define frontLeft, frontRight, backLeft and backRight DcMotor variables here!
     */
    open fun setup() {}

    open fun performInit() {}

    /**
     * The side of the chassis which has its motors inverted
     * @param invert the wheels invert enum
     */
    fun setWheelsInvert(invert: Invert) {
        deltaHardware!!.invert = invert
    }

    override fun runOpMode() { }

}