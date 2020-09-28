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

package com.github.serivesmejia.deltautils.deltamath.geometry

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class Vec2d {

    private val vec = doubleArrayOf(0.0, 0.0)

    /**
     * Constructor for Vec2d from x and y values
     * @param x
     * @param y
     */
    constructor (x: Double, y: Double) {
        vec[0] = x
        vec[1] = y
    }

    /**
     * Constructor for Vec2d
     */
    constructor ()

    /**
     * Constructor for Vec2d using another Vec2d
     * @param o
     */
    constructor (o: Vec2d) {
        vec[0] = o.x()
        vec[1] = o.y()
    }

    /**
     * @param x The X value to set to this Vec2d
     */
    fun setX(x: Double) {
        vec[0] = x
    }

    /**
     * @return the X value of this Vec2d
     */
    fun x(): Double {
        return vec[0]
    }

    /**
     * @param y the Y value to set to this Vec2d
     */
    fun setY(y: Double) {
        vec[1] = y
    }

    /**
     * @return the Y value of this Vec2d
     */
    fun y(): Double {
        return vec[1]
    }

    /**
     * @return the magnitude of the vector
     */
    fun mag(): Double {
        return hypot(x(), y())
    }

    operator fun plus(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.setX(x() + o.x())
        newVec.setY(y() + o.y())

        return newVec

    }

    operator fun plusAssign(o: Vec2d) {

        this.setX(o.x() + x())
        this.setY(o.y() + y())

    }

    operator fun minus(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.setX(x() - o.x())
        newVec.setY(y() - o.y())

        return newVec

    }

    operator fun minusAssign(o: Vec2d) {

        this.setX(x() - o.x())
        this.setY(y() - o.y())

    }

    operator fun div(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.setX(x() / o.x())
        newVec.setY(y() / o.y())

        return newVec

    }

    operator fun divAssign(o: Vec2d) {

        this.setX(o.x() * x())
        this.setY(o.y() * y())

    }

    operator fun times(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.setX(o.x() * x())
        newVec.setY(o.y() * y())

        return newVec

    }

    operator fun timesAssign(o: Vec2d) {

        this.setX(o.x() * x())
        this.setY(o.y() * y())

    }

    /**
     * Rotate this Vec2d by a Rot2d
     * @param by the Rot2d to rotate by
     */
    fun rotate(by: Rot2d) {
        setX(x() * cos(by.getRadians()) - y() * sin(by.getRadians()))
        setY(x() * sin(by.getRadians()) + y() * cos(by.getRadians()))
    }

    /**
     * Inverts current Vec2d values to negative/positive
     */
    fun invert() {
        setX(-x())
        setY(-y())
    }

    override fun toString(): String {
        return "Vec2d(${x()}, ${y()})"
    }

}