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

class Twist2d {

    var tw : Vec2d = Vec2d()
    var th : Rot2d = Rot2d()

    constructor (x: Double, y: Double, theta: Rot2d) {
        this.tw = Vec2d(x, y)
        this.th = Rot2d(theta)
    }

    constructor (vec: Vec2d, theta : Rot2d){
        this.tw = Vec2d(vec)
        this.th = Rot2d(theta)
    }

    constructor (o: Twist2d){
        this.tw = o.vec()
        this.th = o.rot();
    }

    constructor()

    fun vec() : Vec2d { return tw }

    fun rot() : Rot2d { return th; }

    fun x() : Double { return tw.x() }

    fun y() : Double { return tw.y() }

    fun theta() : Double { return th.getRadians() }

}