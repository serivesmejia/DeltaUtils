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
    constructor () {}

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

    override fun toString(): String {
        return "Vect2d(" + x() + ", " + y()
    }

    /**
     * @return the magnitude of the vector
     */
    fun mag(): Double {
        return Math.hypot(x(), y())
    }

    /**
     * Adds another Vec2d to this Vec2d
     * @param o the Vector to subtract to this vector
     */
    fun add(o: Vec2d) {
        setX(o.x() + x())
        setY(o.y() + y())
    }

    /**
     * Subtracts another Vec2d to this Vec2d
     * @param o the Vector to subtract to this vector
     */
    fun subtract(o: Vec2d) {
        setX(x() - o.x())
        setY(y() - o.y())
    }

    /**
     * Divide this Vec2d's X and Y by a value
     * @param by the value to divide by
     */
    fun divide(by: Double) {
        setX(x() / by)
        setY(y() / by)
    }

    /**
     * Rotate this Vec2d by a Rot2d
     * @param by the Rot2d to rotate by
     */
    fun rotate(by: Rot2d) {
        setX(x() * Math.cos(by.getRadians()) - y() * Math.sin(by.getRadians()))
        setY(x() * Math.sin(by.getRadians()) + y() * Math.cos(by.getRadians()))
    }

    /**
     * Inverts current Vec2d values to negative/positive
     */
    fun invert() {
        setX(-x())
        setY(-y())
    }

    /**
     * Multiply this Vec2d's X and Y by a value
     * @param by value to multiply by
     */
    fun multiply(by: Double) {
        setX(x() * by)
        setY(x() * by)
    }


}