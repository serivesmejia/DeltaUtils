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

import kotlin.math.hypot

class Vec2d {

    var x
        get() = vec[0]
        set(value) { vec[0] = value }

    var y
        get() = vec[1]
        set(value) { vec[1] = value }

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
        vec[0] = o.x
        vec[1] = o.y
    }

    /**
     * @return the magnitude of the vector
     */
    fun mag(): Double {
        return hypot(x, y)
    }

    operator fun plus(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.x += o.x
        newVec.y += o.y

        return newVec

    }

    operator fun plusAssign(o: Vec2d) {
        x += o.x
        y += o.y
    }

    operator fun minus(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.x -= o.x
        newVec.y -= o.y

        return newVec

    }

    operator fun minusAssign(o: Vec2d) {
        x -= o.x
        y -= o.y
    }

    operator fun div(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.x /= o.x
        newVec.y /= o.y

        return newVec

    }

    operator fun divAssign(o: Vec2d) {
        this.x /= o.x
        this.y /= o.y
    }

    operator fun times(o: Vec2d): Vec2d {

        val newVec = Vec2d(this)

        newVec.x *= o.x
        newVec.y *= o.y

        return newVec

    }

    operator fun timesAssign(o: Vec2d) {
        this.x *= o.x
        this.y *= o.y
    }

    /**
     * Rotate this Vec2d by a Rot2d
     * @param by the Rot2d to rotate by
     */
    fun rotate(by: Rot2d) {
        x = x * by.cos - y * by.sin
        y = x * by.sin + y * by.cos
    }

    /**
     * Inverts current Vec2d values to negative/positive
     */
    fun invert() {
        x = -x
        y = -y
    }

    override fun toString(): String {
        return "Vec2d($x, $y)"
    }

}