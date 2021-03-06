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

class Pose2d (
        val vec: Vec2d,
        val th: Rot2d
) {

    val x
        get() = vec.x
    val y
        get() = vec.y

    val theta
        get() = th.radians

    constructor (x: Double, y: Double, theta: Rot2d) : this(Vec2d(x, y), theta)

    constructor (other: Pose2d) : this(Vec2d(other.vec), Rot2d(other.th))

    operator fun plus(o: Pose2d): Pose2d {

        val newPose = Pose2d(this)

        newPose.vec += o.vec
        newPose.th += o.th

        return newPose

    }

    operator fun plusAssign(o: Pose2d) {
        vec += o.vec
        th += o.th
    }

    operator fun minus(o: Pose2d): Pose2d {

        val newPose = Pose2d(this)

        newPose.vec -= o.vec
        newPose.th -= o.th

        return newPose

    }

    operator fun minusAssign(o: Pose2d) {
        vec -= o.vec
        th -= o.th
    }

    operator fun div(o: Pose2d): Pose2d {

        val newPose = Pose2d(this)

        newPose.vec /= o.vec
        newPose.th /= o.th

        return newPose

    }

    operator fun divAssign(o: Pose2d) {
        vec /= o.vec
        th /= o.th
    }

    operator fun times(o: Pose2d): Pose2d {

        val newPose = Pose2d(this)

        newPose.vec *= o.vec
        newPose.th *= o.th

        return newPose

    }

    operator fun timesAssign(o: Pose2d) {
        vec *= o.vec
        th *= o.th
    }


}