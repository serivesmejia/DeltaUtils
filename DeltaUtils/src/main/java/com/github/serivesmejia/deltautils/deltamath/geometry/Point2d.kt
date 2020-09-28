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

class Point2d {

    private val point = doubleArrayOf(0.0, 0.0)

    /**
     * Constructor for Point2d from x and y values
     * @param x
     * @param y
     */
    constructor (x: Double, y: Double) {
        point[0] = x
        point[1] = y
    }

    /**
     * Constructor for Vec2d
     */
    constructor ()

    /**
     * Constructor for Point2d using another Point2d
     * @param o
     */
    constructor (o: Point2d) {
        point[0] = o.x()
        point[1] = o.y()
    }

    /**
     * @param x The X value to set to this Point2d
     */
    private fun setX(x: Double) {
        point[0] = x
    }

    /**
     * @return the X value of this Point2d
     */
    fun x(): Double {
        return point[0]
    }

    /**
     * @param y the Y value to set to this Point2d
     */
    private fun setY(y: Double) {
        point[1] = y
    }

    /**
     * @return the Y value of this Point2d
     */
    fun y(): Double {
        return point[1]
    }

    override fun toString(): String {
        return "Point2d(${x()}, ${y()})"
    }

    operator fun plus(o: Point2d): Point2d {

        val newPoint = Point2d(this)

        newPoint.setX(x() + o.x())
        newPoint.setY(y() + o.y())

        return newPoint

    }

    operator fun plusAssign(o: Point2d) {

        this.setX(x() + o.x())
        this.setY(y() + o.y())

    }

    operator fun minus(o: Point2d): Point2d {

        val newPoint = Point2d(this)

        newPoint.setX(x() - o.x())
        newPoint.setY(y() - o.y())

        return newPoint

    }

    operator fun minusAssign(o: Point2d) {

        this.setX(x() - o.x())
        this.setY(y() - o.y())

    }

    operator fun div(o: Point2d): Point2d {

        val newPoint = Point2d(this)

        newPoint.setX(x() / o.x())
        newPoint.setY(y() / o.y())

        return newPoint

    }

    operator fun divAssign(o: Point2d) {

        this.setX(x() / o.x())
        this.setY(y() / o.y())

    }

    operator fun times(o: Point2d): Point2d {

        val newPoint = Point2d(this)

        newPoint.setX(x() * o.x())
        newPoint.setY(y() * o.y())

        return newPoint

    }

    operator fun timesAssign(o: Point2d) {

        this.setX(x() * o.x())
        this.setY(y() * o.y())

    }

    /**
     * Inverts current Point2d values to negative/positive
     */
    fun invert() {
        setX(-x())
        setY(-y())
    }

}