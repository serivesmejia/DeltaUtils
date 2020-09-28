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

package com.github.serivesmejia.deltautils.deltometry.parameters

import com.github.serivesmejia.deltautils.deltadrive.utils.gear.GearRatio
import kotlin.math.abs

class OdometerParameters {

    /**
     * The Odometer encoder ticks per revolution
     * It is probably specified in the page you bought the encoder from.
     */
    var TICKS_PER_REV = 0.0

    /**
     * The Odometer wheel diameter, in inches
     */
    var WHEEL_DIAMETER_INCHES = 0.0

    /**
     * Set to true if the Odometer is returning inverted tick values.
     */
    var RETURNS_FLIPPED_VALUES = false

    private val EMPTY_GEAR_REDUCTION: GearRatio = GearRatio()

    /**
     * This is < 1.0 and > 0 if geared UP
     */
    var GEAR_REDUCTION: GearRatio = EMPTY_GEAR_REDUCTION

    /**
     * Make sure the values are in the correct range.
     */
    fun secureParameters() {
        WHEEL_DIAMETER_INCHES = Math.abs(WHEEL_DIAMETER_INCHES)
        TICKS_PER_REV = abs(TICKS_PER_REV)
    }

    /**
     * Checks if any value is 0.
     * @return boolean depending if all values are or are not 0
     */
    fun haveBeenDefined(): Boolean {
        return !(TICKS_PER_REV == 0.0 || GEAR_REDUCTION !== EMPTY_GEAR_REDUCTION || WHEEL_DIAMETER_INCHES == 0.0)
    }


}