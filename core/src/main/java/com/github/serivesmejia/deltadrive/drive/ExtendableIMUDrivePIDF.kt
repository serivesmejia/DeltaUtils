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

package com.github.serivesmejia.deltadrive.drive

import com.github.serivesmejia.deltacontrol.MotorPIDFController
import com.github.serivesmejia.deltacontrol.PIDFCoefficients
import com.github.serivesmejia.deltadrive.hardware.DeltaHardware
import com.github.serivesmejia.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltadrive.utils.Task
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltamath.geometry.Twist2d
import com.github.serivesmejia.deltasimple.sensor.SimpleBNO055IMU
import com.noahbres.jotai.StateMachineBuilder
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

@Suppress("UNUSED")
abstract class ExtendableIMUDrivePIDF
/**
 * Constructor for the IMU drive class
 * (Do not forget to call initIMU() before the OpMode starts!)
 * @param hdw The initialized hardware containing all the chassis motors
 * @param telemetry Current OpMode telemetry to show movement info
 */
(private val hdw: DeltaHardware, deltaHardwareType: DeltaHardware.Type, private val telemetry: Telemetry? = null) {

    private lateinit var imu: SimpleBNO055IMU

    private val runtime = ElapsedTime()

    private var imuParameters = IMUDriveParameters()

    var pidCoefficientsRotate = PIDFCoefficients(0.0, 0.0, 0.0)

    private var allowedDeltaHardwareType = deltaHardwareType

    enum class State {
        TURN_RIGHT, TURN_LEFT, STOP, END_TASK
    }

    fun initIMU(parameters: IMUDriveParameters) {
        if(imu.initialized) return

        require(hdw.type === allowedDeltaHardwareType) {
            "Given DeltaHardware is not the expected type ($allowedDeltaHardwareType)"
        }

        this.imuParameters = parameters
        parameters.secureParameters()

        imu = SimpleBNO055IMU(hdw.hardwareMap.get(BNO055IMU::class.java, parameters.IMU_HARDWARE_NAME))
        imu.initIMU()
    }

    /**
     * Enter in a while loop until the IMU reports it is calibrated or until the opmode stops
     */
    fun waitForIMUCalibration() =
        if(telemetry == null) imu.waitForIMUCalibration()
        else imu.waitForIMUCalibration(telemetry)

    /**
     * @return the IMU calibration status as a String
     */
    val imuCalibrationStatus get() = imu.imuCalibrationStatus

    val isIMUCalibrated get() = imu.isImuCalibrated

    val robotAngle get(): Rot2d {
        imu.axis = imuParameters.IMU_AXIS
        return imu.cumulativeAngle
    }

    /**
     * Rotate by a Rot2d with a PID repeat.
     * @param rotation The Rot2d to rotate by (use Rot2d.fromDegrees() to create a new Rot2d from degrees)
     * @param power The initial power to rotate
     * @param timeoutS The max time the rotation can take, to avoid robot getting stuck.
     * @return Twist2d containing how much the robot rotated
     */
    fun rotate(rotation: Rot2d, power: Double, timeoutS: Double): Task<Twist2d> {
        var setpoint = rotation.degrees
        val deadZone = imuParameters.DEAD_ZONE

        imuParameters.secureParameters()

        if (!imu.initialized) {
            telemetry?.addData("[/!\\]", "Call initIMU() method before rotating.")
            telemetry?.update()

            return Task { end(); Twist2d() }
        }

        if (isIMUCalibrated) return Task { end(); Twist2d() }

        runtime.reset()

        val pidControllerRotate = MotorPIDFController(pidCoefficientsRotate)

        imu.axis = imuParameters.IMU_AXIS

        if (imuParameters.INVERT_ROTATION) setpoint = -setpoint

        val timeout = if (timeoutS == 0.0) 999999999.0 else timeoutS

        pidControllerRotate.setSetpoint(imu.cumulativeAngle.degrees + setpoint)
                .setDeadzone(deadZone)
                .setInitialPower(abs(power))
                .setErrorTolerance(imuParameters.ERROR_TOLERANCE)
                .setCoefficients(pidCoefficientsRotate)

        var backleftpower = 0.0
        var backrightpower = 0.0
        var frontrightpower = 0.0
        var frontleftpower = 0.0

        val maxMillis = System.currentTimeMillis() + timeout * 1000

        val builder = StateMachineBuilder<State>()
        var currentTwist = Twist2d()
        var powerF = 0.0

        if(setpoint < 0) { //rotating right
            builder.state(State.TURN_RIGHT)
                    .loop {
                        powerF = pidControllerRotate.calculate(imu.cumulativeAngle.degrees)
                        currentTwist = Twist2d(0.0, 0.0, imu.lastCumulativeAngle)

                        backleftpower = powerF
                        backrightpower = -powerF
                        frontleftpower = powerF
                        frontrightpower = -powerF
                    }
        } else { //rotating left
            builder.state(State.TURN_LEFT)
                    .loop {
                        powerF = pidControllerRotate.calculate(imu.cumulativeAngle.degrees)
                        currentTwist = Twist2d(0.0, 0.0, imu.lastCumulativeAngle)

                        backleftpower = powerF
                        backrightpower = -powerF
                        frontleftpower = powerF
                        frontrightpower = -powerF
                    }
        }

        builder.transition { pidControllerRotate.onSetpoint() && System.currentTimeMillis() > maxMillis }
                .state(State.STOP) //stopping
                .onEnter {
                    // stop the movement
                    backleftpower = 0.0
                    backrightpower = 0.0
                    frontleftpower = 0.0
                    frontrightpower = 0.0
                }

                .transitionTimed(0.2)
                .state(State.END_TASK)

        val stateMachine = builder.build()

        return Task {
            if(!stateMachine.running) stateMachine.start()

            stateMachine.update()

            setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)

            telemetry?.addData("[IMU Angle]", imu.lastCumulativeAngle.degrees)
            telemetry?.addData("[Setpoint]", setpoint)
            telemetry?.addData("[Error]", pidControllerRotate.getCurrentError())
            telemetry?.addData("[Power]", powerF)
            telemetry?.update()

            if(stateMachine.getState() == State.END_TASK) end()

            currentTwist
        }
    }

    //needs to extend
    protected abstract fun setAllMotorPower(frontleftpower: Double, frontrightpower: Double, backleftpower: Double, backrightpower: Double)

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

}