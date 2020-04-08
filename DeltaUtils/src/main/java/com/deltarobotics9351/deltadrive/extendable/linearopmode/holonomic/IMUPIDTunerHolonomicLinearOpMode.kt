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

package com.deltarobotics9351.deltadrive.extendable.linearopmode.holonomic

import com.deltarobotics9351.LibraryData
import com.deltarobotics9351.deltamath.geometry.Rot2d
import com.deltarobotics9351.deltamath.geometry.Twist2d
import com.deltarobotics9351.pid.PIDCoefficients


open class IMUPIDTunerHolonomicLinearOpMode : IMUPIDHolonomicLinearOpMode(){


    var P = 0.01
    var I = 0.000001
    var D = 0.000001

    override fun _runOpMode() {

        var selected = 0
        while (!isStarted) {
            if (gamepad1.x) {
                P = 0.0
                I = 0.0
                D = 0.0
            }

            if (gamepad1.a) selected++
            if (gamepad1.b) selected--
            if (selected < 0) selected = 2
            if (selected > 2) selected = 0
            if (selected == 0) {
                if (gamepad1.dpad_up) {
                    P += 0.0001
                } else if (gamepad1.dpad_down) {
                    P -= 0.0001
                }
                telemetry.addData("->P", String.format("%.10f", P))
                telemetry.addData("I", String.format("%.10f", I))
                telemetry.addData("D", """
     ${String.format("%.10f", D)}
     
     To change selection, (A) or (B)
     
     To change selected value, DPAD UP or DPAD DOWN
     To reset values back to 0, (X)
     
     DeltaUtils v${LibraryData.VERSION}
     """.trimIndent())
            } else if (selected == 1) {
                if (gamepad1.dpad_up) {
                    I += 0.0001
                } else if (gamepad1.dpad_down) {
                    I -= 0.0001
                }
                telemetry.addData("P", String.format("%.10f", P))
                telemetry.addData("->I", String.format("%.10f", I))
                telemetry.addData("D", """
     ${String.format("%.10f", D)}
     
     To change selection, (A) or (B)
     
     To change selected value, DPAD UP or DPAD DOWN
     To reset values back to 0, (X)
     
     DeltaUtils v${LibraryData.VERSION}
     """.trimIndent())
            } else if (selected == 2) {
                if (gamepad1.dpad_up) {
                    D += 0.0001
                } else if (gamepad1.dpad_down) {
                    D -= 0.0001
                }
                telemetry.addData("P", String.format("%.10f", P))
                telemetry.addData("I", String.format("%.10f", I))
                telemetry.addData("->D", """
     ${String.format("%.10f", D)}
     
     To change selection, (A) or (B)
     
     To change selected value, DPAD_UP or DPAD DOWN
     To reset values back to 0, (X)
     
     DeltaUtils v${LibraryData.VERSION}
     """.trimIndent())
            }
            telemetry.update()
            sleep(40)
        }

        if (!isStarted) return

        setRotatePID(PIDCoefficients(P, I, D))

        val twist: Twist2d = rotate(Rot2d.fromDegrees(90.0), 0.7, 5.0)

        sleep(3000)

        telemetry.addData("Final Robot Angle", getRobotAngle())
        telemetry.addData("IMU Reported Twist", twist)
        telemetry.addData("Expected Robot Angle", "90")
        telemetry.update()

        while (opModeIsActive());
    }

}