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

package com.deltarobotics9351.deltamath.geometry

class Rot2d {

    private var radians = 0.0
    private var cos = 0.0
    private var sin = 0.0

    /**
     * Constructor for Rot2d
     */
    constructor () {
        radians = 0
        cos = 1.0
        sin = 0.0
    }

    /**
     * Constructor for Rot2d
     * @param rad Radians
     */
    constructor (rad: Double) {
        radians = rad
        cos = Math.cos(radians)
        sin = Math.sin(radians)
    }

    constructor (o : Rot2d){
        radians = o.getRadians()
        cos = Math.cos(radians)
        sin = Math.sin(radians)
    }

    /**
     * Constructor for Rot2d using x and y values
     * @param x
     * @param y
     */
    constructor (x: Double, y: Double) {
        val hy = Math.hypot(x, y)
        if (hy > 0.00001) {
            sin = y / hy
            cos = x / hy
        } else {
            sin = 0.0
            cos = 1.0
        }
        radians = Math.atan2(sin, cos)
    }

    companion object {
        /**
         * Creates a new Rot2d from degrees
         * @param degrees degrees to set to the new Rot2d
         * @return new Rot2d from degrees
         */
        fun fromDegrees(degrees: Double): Rot2d {
            return Rot2d(Math.toRadians(degrees))
        }
    }

    /**
     * Sets the Rot2d radians from degrees and returns a new one
     * @param degrees
     * @return Result Rot2d
     */
    fun setDegrees(degrees: Double): Rot2d? {
        radians = Math.toRadians(degrees)
        cos = Math.cos(radians)
        sin = Math.sin(radians)
        return Rot2d(radians)
    }

    /**
     * Sets the radians and returns a new Rot2d
     * @param radians
     * @return Result Rot2d
     */
    fun setRadians(radians: Double): Rot2d? {
        this.radians = radians
        cos = Math.cos(radians)
        sin = Math.sin(radians)
        return Rot2d(radians)
    }

    /**
     * @return the degrees from this Rot2d
     */
    fun getDegrees(): Double {
        return Math.toDegrees(radians)
    }

    /**
     * @param other Other Rot2d
     * @return the difference in radians between this and other Rot2d
     */
    fun deltaRadians(other: Rot2d): Double {
        var deltaAngle = getDegrees() - other.getDegrees()
        if (deltaAngle < -180) deltaAngle += 360.0 else if (deltaAngle > 180) deltaAngle -= 360.0
        return Math.toRadians(deltaAngle)
    }

    /**
     * @param other Other Rot2d
     * @return the difference in degrees between this and other Rot2d
     */
    fun deltaDegrees(other: Rot2d): Double {
        var deltaAngle = getDegrees() - other.getDegrees()
        if (deltaAngle < -180) deltaAngle += 360.0 else if (deltaAngle > 180) deltaAngle -= 360.0
        return deltaAngle
    }

    /**
     * @return the calculated tan
     */
    fun calculateTan(): Double {
        return sin / cos
    }

    /**
     * @return the calculated sin
     */
    fun getSin(): Double {
        return sin
    }

    /**
     * @return the calculated cos
     */
    fun getCos(): Double {
        return cos
    }

    /**
     * @return the calculated radians
     */
    fun getRadians(): Double {
        return radians
    }

    /**
     * Rotate by another Rot2d and returns a new one
     * @param o the Rot2d to rotate by
     * @return Result Rot2d
     */
    fun rotate(o: Rot2d): Rot2d {
        val x = cos * (o.getCos()) - (o.getSin()) * (o?.getCos())
        val y = cos * (o.getSin()) + (o.getSin()) * (o?.getCos())
        val hy = Math.hypot(
                x,
                y)
        if (hy > 0.00001) {
            sin = y / hy
            cos = x / hy
        } else {
            sin = 0.0
            cos = 1.0
        }
        radians = Math.atan2(sin, cos)
        return Rot2d(x, y)
    }

    /**
     * Add another Rot2d and returns a new Rot2d with the result
     * @param o the Rot2d to add by
     * @return Result Rot2d
     */
    fun add(o: Rot2d): Rot2d {
        rotate(o)
        return Rot2d(radians)
    }

    /**
     * Subtract another Rot2d and returns a new Rot2d with the result
     * @param o the Rot2d to subtract by
     * @return Result Rot2d
     */
    fun subtract(o: Rot2d): Rot2d {
        rotate(o.invert())
        return Rot2d(radians)
    }

    /**
     * Inverts the radians and returns a new Rot2d
     * @return Result Rot2d
     */
    fun invert(): Rot2d {
        radians = -radians
        return Rot2d(radians)
    }

    override fun toString(): String {
        return "Rot2d(rad " + radians + ", deg " + Math.toDegrees(radians) + ")"
    }


}