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

package com.github.serivesmejia.deltadrive.hardware

import com.github.serivesmejia.deltadrive.utils.Invert
import com.github.serivesmejia.deltadrive.utils.Robot
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap

abstract class DeltaHardware
/**
 * Constructor for the delta hardware holonomic class
 * Do not forget to initialize the motors with initHardware()
 * @param hdwMap The current OpMode hardware map
 * @param invert Enum specifying which side will be inverted (motors), most of the time you need to invert the right side.
 */
(val hardwareMap: HardwareMap, var invert: Invert) {

    lateinit var chassisMotorsArray: Array<DcMotorEx>
        internal set

    var type = Type.UNKNOWN
        internal set

    enum class Type {
        UNKNOWN, HOLONOMIC, HDRIVE
    }

    internal fun internalInit(brake: Boolean) {
        setBulkCachingMode(LynxModule.BulkCachingMode.AUTO)
        setMotorPowers(0.0, 0.0, 0.0, 0.0)
        setBrakes(brake)
        setRunModes(RunMode.RUN_USING_ENCODER)
    }

    fun setMotorPowers(vararg powers: Double) {
        for((index, power) in powers.withIndex()) {
            if(index < chassisMotorsArray.size) {
                chassisMotorsArray[index].power = power
            }
        }
    }

    fun setTargetPositions(vararg targetPositions: Int) {
        for((index, targetPosition) in targetPositions.withIndex()) {
            if(index < chassisMotorsArray.size) {
                chassisMotorsArray[index].targetPosition = targetPosition
            }
        }
    }

    fun setVelocities(vararg velocities: Double) {
        for((index, velocity) in velocities.withIndex()) {
            if(index < chassisMotorsArray.size) {
                chassisMotorsArray[index].velocity = velocity
            }
        }
    }

    fun setBrakes(brake: Boolean) {
        for (motor in chassisMotorsArray) {
            if (brake) {
                motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            } else {
                motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
            }
        }
    }

    fun setRunModes(runMode: RunMode) {
        for (motor in chassisMotorsArray) {
            motor.mode = runMode
        }
    }

    abstract fun updateChassisMotorsArray()

    fun setBulkCachingMode(mode: LynxModule.BulkCachingMode) {
        for(module in hardwareMap.getAll(LynxModule::class.java)) {
            module.bulkCachingMode = mode
        }
    }

    fun clearBulkCache() {
        for(module in hardwareMap.getAll(LynxModule::class.java)) {
            module.clearBulkCache()
        }
    }

}