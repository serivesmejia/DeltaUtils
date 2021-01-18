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

package com.github.serivesmejia.deltamath.geometry

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class Rot2d() {

    companion object {
        /**
         * Creates a new Rot2d from degrees
         * @param degrees degrees to set to the new Rot2d
         * @return new Rot2d from degrees
         */
        fun degrees(degrees: Double): Rot2d {
            return Rot2d(Math.toRadians(degrees))
        }
    }

    var radians
        get() = i_radians
        set(value) {
            cos = cos(value)
            sin = sin(value)
            i_radians = value
        }


    var degrees: Double
        get() = Math.toDegrees(radians)
        set(value) { radians = Math.toRadians(value) }

    var cos
        get() = i_cos
        private set(value) {
            i_cos = value
        }

    var sin
        get() = i_sin
        private set(value) {
            i_sin = value
        }

    private var i_radians = 0.0
    private var i_cos = 0.0
    private var i_sin = 0.0

    /**
     * Constructor for Rot2d
     */
    init {
        radians = 0.0
        cos = 1.0
        sin = 0.0
    }

    /**
     * Constructor for Rot2d
     * @param rad Radians
     */
    constructor (rad: Double): this() {
        radians = rad
        cos = cos(radians)
        sin = sin(radians)
    }

    constructor (o : Rot2d): this() {
        radians = o.radians
        cos = cos(radians)
        sin = sin(radians)
    }

    /**
     * Constructor for Rot2d using x and y values
     * @param x
     * @param y
     */
    constructor (x: Double, y: Double): this() {
        val hy = hypot(x, y)

        if (hy > 0.00001) {
            sin = y / hy
            cos = x / hy
        } else {
            sin = 0.0
            cos = 1.0
        }

        radians = atan2(sin, cos)
    }

    /**
     * @param other Other Rot2d
     * @return the difference in radians between this and other Rot2d
     */
    fun deltaRadians(other: Rot2d): Double { return Math.toRadians(deltaDegrees(other)) }

    /**
     * @param other Other Rot2d
     * @return the difference in degrees between this and other Rot2d
     */
    fun deltaDegrees(other: Rot2d): Double {
        var deltaAngle = degrees - other.degrees
        if (deltaAngle < -180) deltaAngle += 360.0 else if (deltaAngle > 180) deltaAngle -= 360.0

        return deltaAngle
    }

    /**
     * @return the calculated tan
     */
    fun calculateTan(): Double = sin / cos

    /**
     * Rotate by another Rot2d and returns a new one
     * @param o the Rot2d to rotate by
     * @return Result Rot2d
     */
    fun rotate(o: Rot2d): Rot2d {

        val x = cos * o.cos - o.sin * o.cos
        val y = cos * o.sin + o.sin * o.cos

        val hy = hypot(x, y)

        if (hy > 0.00001) {
            sin = y / hy
            cos = x / hy
        } else {
            sin = 0.0
            cos = 1.0
        }

        radians = atan2(sin, cos)

        return Rot2d(x, y)

    }

    operator fun plus(o: Rot2d): Rot2d {

        val newRot = Rot2d(this)

        return newRot.rotate(o)

    }

    operator fun plusAssign(o: Rot2d) {
        this.rotate(o)
    }

    operator fun minus(o: Rot2d): Rot2d {
        val newRot = Rot2d(this)
        return newRot.rotate(Rot2d(o).invert())
    }

    operator fun minusAssign(o: Rot2d) {
        this.rotate(Rot2d(o).invert())
    }


    operator fun div(o: Rot2d): Rot2d {

        val newRot = Rot2d(this)

        newRot.radians /= o.radians

        return newRot

    }

    operator fun divAssign(o: Rot2d) {
        radians /= o.radians
    }

    operator fun times(o: Rot2d): Rot2d {

        val newRot = Rot2d(this)

        newRot.radians *= o.radians

        return newRot

    }

    operator fun timesAssign(o: Rot2d) {
        radians *= o.radians
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
        return "Rot2d(rad ${radians}, deg ${Math.toDegrees(radians)})"
    }

}