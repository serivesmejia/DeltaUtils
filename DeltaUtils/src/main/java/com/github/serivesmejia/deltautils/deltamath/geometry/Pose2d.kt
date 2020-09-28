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

class Pose2d() {

    private var vec: Vec2d = Vec2d()
    private var heading = 0.0

    init {
        vec = Vec2d(0.0, 0.0)
        heading = 0.0
    }

    constructor(x: Double, y: Double, heading: Double): this() {
        vec = Vec2d(x, y)
        this.heading = heading
    }

    constructor(vec: Vec2d, heading: Double): this() {
        this.vec = vec
        this.heading = heading
    }

    constructor(o: Pose2d): this() {
        vec = o.getPosition()
        heading = o.heading
    }

    fun getPosition() = vec

    fun getHeading() = heading

    fun add(o: Pose2d) {
        vec = vec + o.getPosition()
        heading += o.heading
    }

    fun divide(by: Double) {
        vec = vec / Vec2d(by, by)
        heading /= by
    }

    fun invert() {
        vec.invert()
        heading = -heading
    }

    fun multiply(by: Double) {
        vec = vec * Vec2d(by, by)
        heading *= by
    }

    fun rotate(by: Double) {
        heading += by
    }

    override fun toString(): String {

        val v = vec.toString()
        val h = heading.toString()
        return "Pose2d($v, $h)"

    }

}