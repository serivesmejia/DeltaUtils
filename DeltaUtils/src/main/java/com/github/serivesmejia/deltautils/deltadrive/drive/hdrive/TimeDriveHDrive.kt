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

package com.github.serivesmejia.deltautils.deltadrive.drive.hdrive

import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardwareHDrive
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

//old class, commented in spanish sorry
class TimeDriveHDrive {

    var hdw: DeltaHardwareHDrive? = null
    var telemetry: Telemetry? = null

    private val runtime = ElapsedTime()

    /**
     * Constructor for the time drive class
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry The current OpMode telemetry to show info related tnto the moveme
     */
    constructor (hdw: DeltaHardwareHDrive?, telemetry: Telemetry?) {
        this.hdw = hdw
        this.telemetry = telemetry
    }

    //se define el power de todos los motores y el tiempo en el que avanzaran a este power
    //la string es simplemente para mostrarla en la driver station con un mensaje telemetry.
    //(el tiempo es en segundos)
    private fun timeDrive(left: Double, right: Double, middle: Double, time: Double, movementDescription: String) {

        runtime.reset()

        hdw!!.setAllMotorPower(left, right, middle)

        while (runtime.seconds() <= time) {
            telemetry!!.addData("[Movement]", movementDescription)
            telemetry!!.addData("[left]", left)
            telemetry!!.addData("[right]", right)
            telemetry!!.addData("[middle]", middle)
            telemetry!!.addData("[Time]", time)
            telemetry!!.update()
        }

        hdw!!.setAllMotorPower(0.0, 0.0, 0.0, 0.0)

        telemetry!!.addData("[Movement]", movementDescription)
        telemetry!!.addData("[left]", 0)
        telemetry!!.addData("[right]", 0)
        telemetry!!.addData("[middle]", 0)

        telemetry!!.update()
    }

    //basado en esta imagen: https://i.imgur.com/R82YOwT.png
    //el movementDescription es simplemente para mostrarlo en un mensaje telemetry (driver station)

    //hacia adelante
    fun forward(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        timeDrive(power, power, 0.0, timeSecs, "forward")
    }

    //hacia atras
    fun backwards(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        timeDrive(-power, -power, 0.0, timeSecs, "backwards")
    }

    //deslizarse a la izquierda
    fun strafeRight(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        timeDrive(0.0, 0.0, power, timeSecs, "strafeLeft")
    }

    //deslizarse a la izquierda
    fun strafeLeft(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        timeDrive(0.0, 0.0, -power, timeSecs, "strafeRight")
    }

    //girar a la derecha
    fun turnRight(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        timeDrive(power, -power, 0.0, timeSecs, "turnRight")
    }

    //girar a la izquierda
    fun turnLeft(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        timeDrive(-power, power, 0.0, timeSecs, "turnLeft")
    }

    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

}