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

package com.github.serivesmejia.deltautils.deltadrive.utils.gear

/**
 * Class representing a two gear ratio
 */
class TwoGearRatio : GearRatio {

    private val ratio = doubleArrayOf(0.0, 0.0)

    private var inputRPM = 0.0;

    /**
     * Constructor for GearRatio class
     * @param T1 Number of teeth of drive gear
     * @param T2 Number of teeth of driven gear
     * @param inputRPM The input RPM (generally the motor RPM)
     */
    constructor (T1: Double, T2: Double, inputRPM: Double = 0.0) {
        ratio[0] = T1
        ratio[1] = T2
        this.inputRPM = inputRPM
    }

    /**
     * @return Get ratio result as decimal
     */
    override fun getRatioAsDecimal(): Double {
        return ratio[0] / ratio[1]
    }

    /**
     * @return Gear ratio result as a percentage
     */
    override fun getRatioAsPercentage(): Double {
        return getRatioAsDecimal() * 100
    }

    /**
     * @return Number of teeth of driven gear
     */
    fun getT2(): Double {
        return ratio[1]
    }

    /**
     * @return Number of teeth of drive gear
     */
    fun getT1(): Double {
        return ratio[0]
    }

    override fun getOutputRPM() : Double {
        return inputRPM * getRatioAsDecimal()
    }

    override fun toString(): String {
        return "(${getT1()} : ${getT2()}), Input RPM: {$inputRPM}"
    }


}