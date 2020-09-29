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

package com.github.serivesmejia.deltautils.deltadrive.drive

import com.github.serivesmejia.deltautils.deltadrive.hardware.DeltaHardware
import com.github.serivesmejia.deltautils.deltadrive.parameters.EncoderDriveParameters
import com.github.serivesmejia.deltautils.deltadrive.parameters.IMUDriveParameters
import com.github.serivesmejia.deltautils.deltasimple.sensor.SimpleBNO055IMU
import com.github.serivesmejia.deltautils.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltautils.deltamath.geometry.Twist2d
import com.github.serivesmejia.deltautils.deltapid.PIDCoefficients
import com.github.serivesmejia.deltautils.deltapid.PIDController

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.util.ElapsedTime

import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs

open class ExtendableIMUDrivePID {

    protected lateinit var imu: SimpleBNO055IMU
    protected val hdw: DeltaHardware

    protected val telemetry: Telemetry

    private val runtime = ElapsedTime()

    protected var imuParameters: IMUDriveParameters = IMUDriveParameters()

    private var rkP = 0.0
    private var rkI = 0.0
    private var rkD = 0.0

    protected var pidCoefficientsRotate: PIDCoefficients = PIDCoefficients(0.0, 0.0, 0.0)

    private var dkP = 0.0
    private var dkI = 0.0
    private var dkD = 0.0

    protected var pidCoefficientsDrive: PIDCoefficients = PIDCoefficients(0.0, 0.0, 0.0)

    private var isInitializedEncoders = false

    protected var allowedDeltaHardwareType = DeltaHardware.Type.DEFAULT

    protected var encoderDriveParameters: EncoderDriveParameters = EncoderDriveParameters()

    protected var pidControllerRotate = PIDController(pidCoefficientsRotate)

    /**
     * Constructor for the IMU drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    constructor (hdw: DeltaHardware, telemetry: Telemetry, deltaHardwareType: DeltaHardware.Type) {
        this.hdw = hdw
        this.telemetry = telemetry
        this.allowedDeltaHardwareType = deltaHardwareType
    }

    fun initIMU(parameters: IMUDriveParameters) {

        if(imu.isInitialized()) return

        require(hdw.type === allowedDeltaHardwareType) { "Given DeltaHardware is not the expected type ($allowedDeltaHardwareType)" }

        this.imuParameters = parameters
        parameters.secureParameters()

        imu = SimpleBNO055IMU(hdw.hdwMap!!.get(BNO055IMU::class.java, parameters.IMU_HARDWARE_NAME))
        imu.initIMU()

    }

    fun initEncoders(parameters: EncoderDriveParameters) {
        if(isInitializedEncoders) return
        isInitializedEncoders = true
        encoderDriveParameters = parameters
    }

    /**
     * @param coefficients the rotate PID coefficients, in a DeltaUtils PIDCoefficients object
     */
    fun setRotatePID(coefficients: PIDCoefficients) {
        rkP = abs(coefficients.kP)
        rkI = abs(coefficients.kI)
        rkD = abs(coefficients.kD)
        pidCoefficientsRotate = coefficients
    }

    fun getRotatePID(): PIDCoefficients {
        return pidCoefficientsRotate
    }

    fun getRotateP(): Double {
        return rkP
    }

    fun getRotateI(): Double {
        return rkI
    }

    fun getRotateD(): Double {
        return rkD
    }

    /**
     * @param coefficients the drive (forward & backwards) PID coefficients, in a DeltaUtils PIDCoefficients object
     */
    fun setDrivePID(coefficients: PIDCoefficients) {
        dkP = abs(coefficients.kP)
        dkI = abs(coefficients.kI)
        dkD = abs(coefficients.kD)
        pidCoefficientsDrive = coefficients
    }

    fun getDrivePID(): PIDCoefficients {
        return pidCoefficientsDrive
    }

    fun getDriveP(): Double {
        return dkP
    }

    fun getDriveI(): Double {
        return dkI
    }

    fun getDriveD(): Double {
        return dkD
    }

    /**
     * Enter in a while loop until the IMU reports it is calibrated or until the opmode stops
     */
    fun waitForIMUCalibration() {
        imu!!.waitForIMUCalibration(telemetry!!)
    }

    /**
     * @return the IMU calibration status as a String
     */
    fun getIMUCalibrationStatus(): String {
        return imu!!.getIMUCalibrationStatus()
    }

    fun isIMUCalibrated(): Boolean {
        return imu!!.isIMUCalibrated()
    }

    fun getRobotAngle(): Rot2d {
        imu!!.setAxis(imuParameters.IMU_AXIS)
        return imu!!.getAngle()
    }

    /**
     * Rotate by a Rot2d with a PID repeat.
     * @param rotation The Rot2d to rotate by (use Rot2d.fromDegrees() to create a new Rot2d from degrees)
     * @param power The initial power to rotate
     * @param timeoutS The max time the rotation can take, to avoid robot getting stuck.
     * @return Twist2d containing how much the robot rotated
     */
    fun rotate(rotation: Rot2d, power: Double, timeoutS: Double): Twist2d {

        val power = power
        var timeoutS = timeoutS

        var setpoint = rotation.getDegrees()
        val deadZone = imuParameters.DEAD_ZONE

        imuParameters.secureParameters()

        if (!imu!!.isInitialized()) {
            telemetry!!.addData("[/!\\]", "Call initIMU() method before rotating.")
            telemetry!!.update()
            sleep(2000)
            return Twist2d()
        }

        if (!isIMUCalibrated()) return Twist2d()

        imu!!.resetAngle() //reset everything
        runtime.reset()
        pidControllerRotate.reset()

        imu!!.setAxis(imuParameters.IMU_AXIS)

        if (imuParameters!!.INVERT_ROTATION) setpoint = -setpoint

        if (timeoutS == 0.0) {
            timeoutS = 999999999.0 //basically infinite time.
        }

        pidControllerRotate.setSetpoint(setpoint)
                           .setDeadzone(deadZone)
                           .setInitialPower(abs(power))
                           .setErrorTolerance(imuParameters!!.ERROR_TOLERANCE)

        var backleftpower: Double
        var backrightpower: Double
        var frontrightpower: Double
        var frontleftpower: Double
        val maxMillis = System.currentTimeMillis() + timeoutS * 1000
        val firstLoop = true

        // rotaremos hasta que se complete la vuelta
        if (setpoint < 0) {
            pidControllerRotate.setErrorInverted()

            while (imu!!.getAngle().getDegrees() == 0.0 && !Thread.interrupted() && System.currentTimeMillis() < maxMillis) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry!!.addData("IMU Angle", imu!!.getLastAngle().getDegrees())
                telemetry!!.addData("Setpoint", setpoint)
                telemetry!!.addData("Delta", "Not calculated yet")
                telemetry!!.addData("Power", power)
                telemetry!!.update()

                backleftpower = power
                backrightpower = -power
                frontleftpower = power
                frontrightpower = -power

                setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)
            }

            while (!pidControllerRotate.onSetpoint() && !Thread.interrupted() && System.currentTimeMillis() < maxMillis) { //entramos en un bucle hasta que los setpoint sean los esperados

                val nowMillis = System.currentTimeMillis().toDouble()

                pidControllerRotate.setPIDMultiplier(Math.abs(setpoint / 90))

                var powerF = pidControllerRotate.calculate(imu!!.getAngle().getDegrees())

                backleftpower = powerF
                backrightpower = -powerF
                frontleftpower = powerF
                frontrightpower = -powerF

                setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)

                telemetry!!.addData("IMU Angle", imu!!.getLastAngle().getDegrees())
                telemetry!!.addData("Setpoint", setpoint)
                telemetry!!.addData("Error", pidControllerRotate.getCurrentError())
                telemetry!!.addData("Power", powerF)
                telemetry!!.update()

                sleep(3)
            }

        } else while (!pidControllerRotate.onSetpoint() && !Thread.interrupted() && System.currentTimeMillis() < maxMillis) {

            val nowMillis = System.currentTimeMillis().toDouble()

            pidControllerRotate.setPIDMultiplier(Math.abs(setpoint / 90))

            var powerF = pidControllerRotate.calculate(imu!!.getAngle().getDegrees())

            backleftpower = -powerF
            backrightpower = powerF
            frontleftpower = -powerF
            frontrightpower = powerF

            setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)

            telemetry!!.addData("IMU Angle", imu!!.getAngle().getDegrees())
            telemetry!!.addData("Setpoint", setpoint)
            telemetry!!.addData("Error", pidControllerRotate.getCurrentError())
            telemetry!!.addData("Power", powerF)
            telemetry!!.update()

            sleep(3)

        }

        // stop the movement
        setAllMotorPower(0.0, 0.0, 0.0, 0.0)
        sleep(20)

        return Twist2d(0.0, 0.0, imu!!.getAngle())

    }

    fun encoderPIDForward(inches: Double, speed: Double, timeoutS: Double) {
        if (!isInitializedEncoders) return
        var speed = speed
        speed = abs(speed)
        val initialRobotHeading = imu!!.getAngle().getDegrees()
        encoderPIDDrive(speed, inches, inches, inches, inches, timeoutS, encoderDriveParameters!!.RIGHT_WHEELS_TURBO, encoderDriveParameters!!.LEFT_WHEELS_TURBO, "PID Backwards")
    }


    fun encoderPIDBackwards(inches: Double, speed: Double, timeoutS: Double) {
        if (!isInitializedEncoders) return
        var speed = abs(speed)
        encoderPIDDrive(speed, -inches, -inches, -inches, -inches, timeoutS, encoderDriveParameters!!.RIGHT_WHEELS_TURBO, encoderDriveParameters!!.LEFT_WHEELS_TURBO, "PID Backwards")
    }

    fun timePIDForward(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        val initialRobotHeading = imu!!.getAngle().getDegrees()
        timePIDDrive(power, power, power, power, timeSecs, initialRobotHeading, this, "PID Forward")
    }

    fun timePIDBackwards(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        val initialRobotHeading = imu!!.getAngle().getDegrees()
        timePIDDrive(power, power, power, power, timeSecs, initialRobotHeading, this, "PID Backwards")
    }

    //needs to extend
    fun timePIDDrive(frontleft: Double, frontright: Double, backleft: Double, backright: Double, timeSecs: Double, initialRobotHeading: Double, imu: ExtendableIMUDrivePID, movementDescription: String) {}

    //needs to extend
    open fun encoderPIDDrive(speed: Double,
                        frontleft: Double,
                        frontright: Double,
                        backleft: Double,
                        backright: Double,
                        timeoutS: Double,
                        rightTurbo: Double,
                        leftTurbo: Double,
                        movementDescription: String) { }

    private fun setAllMotorPower(frontleftpower: Double, frontrightpower: Double, backleftpower: Double, backrightpower: Double) {
        when (hdw!!.type) {
            DeltaHardware.Type.HOLONOMIC -> hdw!!.setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)
            DeltaHardware.Type.HDRIVE -> {
                val averageLeft = (frontleftpower + backleftpower) / 2
                val averageRight = (frontrightpower + backrightpower) / 2
                hdw!!.setAllMotorPower(averageLeft, averageRight, 0.0)
            }
        }
    }

    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

}