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
import com.github.serivesmejia.deltautils.deltadrive.utils.Robot
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.HardwareMap

open class DeltaHardware {

    var wheelFrontLeft: DcMotor? = null
    var wheelFrontRight: DcMotor? = null
    var wheelBackLeft: DcMotor? = null
    var wheelBackRight: DcMotor? = null

    var wheelMiddle: DcMotor? = null

    var wheelsLeft: DcMotor? = null
    var wheelsRight: DcMotor? = null

    var chassisMotorsArray = arrayOf<DcMotor?>(null, null, null, null)

    enum class Type {
        DEFAULT, HOLONOMIC, HDRIVE
    }

    var type = Type.DEFAULT

    /**
     * Enum specifying the side of the chassis which will be inverted
     * Most of the time, you need to invert the right side.
     */
    var invert: Invert = Invert.RIGHT_SIDE

    var hdwMap: HardwareMap = Robot.getCurrentOpMode().hardwareMap

    /**
     * Constructor for the delta hardware holonomic class
     * Do not forget to initialize the motors with initHardware()
     * @param hdwMap The current OpMode hardware map
     * @param invert Enum specifying which side will be inverted (motors), most of the time you need to invert the right side.
     */
    constructor (invert: Invert) {
        this.invert = invert
    }

    open fun initHardware(frontleft: DcMotor, frontright: DcMotor, backleft: DcMotor, backright: DcMotor, brake: Boolean) {
        throw UnsupportedOperationException("Function initHardware() with four motors is not supported in this DeltaHardware")
    }

    open fun initHardware(left: DcMotor, right: DcMotor, hdriveMiddle: DcMotor, brake: Boolean) {
        throw UnsupportedOperationException("Function initHardware() with three motors is not supported in this DeltaHardware")
    }

    open fun initHardware(left: DcMotor, right: DcMotor, brake: Boolean) {
        throw UnsupportedOperationException("Function initHardware() with two motors is not supported in this DeltaHardware")
    }

    open fun setAllMotorPower(frontleft: Double, frontright: Double, backleft: Double, backright: Double) {
        throw UnsupportedOperationException("Function setAllMotorPower() with four motors is not supported in this DeltaHardware")
    }

    open fun setAllMotorPower(left: Double, right: Double, middle: Double) {
        throw UnsupportedOperationException("Function setAllMotorPower() with three motors is not supported in this DeltaHardware")
    }

    open fun setAllMotorPower(left: Double, right: Double) {
        throw UnsupportedOperationException("Function setAllMotorPower() with two motors is not supported in this DeltaHardware")
    }

    open fun setTargetPositions(frontleft: Int, frontright: Int, backleft: Int, backright: Int) {
        throw UnsupportedOperationException("Function setTargetPositions() with four motors is not supported in this DeltaHardware")
    }

    open fun setTargetPositions(left: Int, right: Int, middle: Int) {
        throw UnsupportedOperationException("Function setTargetPositions() with three motors is not supported in this DeltaHardware")
    }

    open fun setTargetPositions(left: Int, right: Int) {
        throw UnsupportedOperationException("Function setTargetPositions() with two motors is not supported in this DeltaHardware")
    }

    open fun setBrakes(brake: Boolean) {
        throw UnsupportedOperationException("Function setBrakes() is not supported in this DeltaHardware")
    }

    open fun setRunModes(runMode: RunMode) {
        throw UnsupportedOperationException("Function setRunModes() is not supported in this DeltaHardware")
    }

    open fun updateChassisMotorsArray() {
        throw UnsupportedOperationException("Function updateChassisMotorsArray() is not supported in this DeltaHardware")
    }

}