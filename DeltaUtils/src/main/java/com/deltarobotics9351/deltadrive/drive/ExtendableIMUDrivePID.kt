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

package com.deltarobotics9351.deltadrive.drive

import com.deltarobotics9351.LibraryData
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters
import com.deltarobotics9351.deltadrive.utils.Axis
import com.deltarobotics9351.deltamath.DeltaMathUtil
import com.deltarobotics9351.deltamath.geometry.Rot2d
import com.deltarobotics9351.deltamath.geometry.Twist2d
import com.deltarobotics9351.pid.PIDCoefficients

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.util.ElapsedTime

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import kotlin.math.abs

open class ExtendableIMUDrivePID {

    var imu: BNO055IMU? = null
    var hdw: DeltaHardware? = null

    var lastAngles: Orientation = Orientation()
    var globalAngle = 0.0

    var telemetry: Telemetry? = null

    private val runtime = ElapsedTime()

    private var parameters: IMUDriveParameters? = null

    private var rkP = 0.0
    private var rkI = 0.0
    private var rkD = 0.0

    private var pidCoefficientsRotate: PIDCoefficients = PIDCoefficients(0.0, 0.0, 0.0)

    private var dkP = 0.0
    private var dkI = 0.0
    private var dkD = 0.0

    private var pidCoefficientsDrive: PIDCoefficients = PIDCoefficients(0.0, 0.0, 0.0)

    private var isInitializedIMU = false
    private var isInitializedEncoders = false

    private var allowedDeltaHardwareType = DeltaHardware.Type.DEFAULT

    private var encoderDriveParameters: EncoderDriveParameters = EncoderDriveParameters()

    /**
     * Constructor for the IMU drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    constructor (hdw: DeltaHardware?, telemetry: Telemetry?, deltaHardwareType: DeltaHardware.Type) {
        this.hdw = hdw
        this.telemetry = telemetry
        this.allowedDeltaHardwareType = deltaHardwareType
    }

    fun initIMU(parameters: IMUDriveParameters?) {
        require(hdw!!.type === allowedDeltaHardwareType) { "Given DeltaHardware is not the expected" }
        this.parameters = parameters
        val param = BNO055IMU.Parameters()
        param.mode = BNO055IMU.SensorMode.IMU
        param.angleUnit = BNO055IMU.AngleUnit.DEGREES
        param.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        param.loggingEnabled = false
        imu = hdw!!.hdwMap!!.get(BNO055IMU::class.java, "imu")
        imu!!.initialize(param)
        isInitializedIMU = true
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
        rkP = Math.abs(coefficients.kP)
        rkI = Math.abs(coefficients.kI)
        rkD = Math.abs(coefficients.kD)
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
        dkP = Math.abs(coefficients.kP)
        dkI = Math.abs(coefficients.kI)
        dkD = Math.abs(coefficients.kD)
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
     * Enter in a while repeat until the IMU reports it is calibrated or until the opmode stops
     */
    fun waitForIMUCalibration() {
        while (!imu!!.isGyroCalibrated && !Thread.interrupted()) {
            telemetry!!.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...")
            telemetry!!.addData("[Status]", "${getIMUCalibrationStatus()}\n\nDeltaUtils v${LibraryData.VERSION}")
            telemetry!!.update()
        }
    }

    /**
     * @return the IMU calibration status as a String
     */
    fun getIMUCalibrationStatus(): String {
        return imu!!.calibrationStatus.toString()
    }

    fun isIMUCalibrated(): Boolean {
        return imu!!.isGyroCalibrated
    }

    private fun getAngle(): Double {
        // We have to process the angle because the imu works in euler angles so the axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.
        var angles: Orientation? = null
        angles = when (parameters!!.IMU_AXIS) {
            Axis.X -> imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES)
            Axis.Y -> imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES)
            else -> imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
        }
        var deltaAngle: Float = angles.firstAngle - lastAngles.firstAngle
        if (deltaAngle < -180) deltaAngle += 360 else if (deltaAngle > 180) deltaAngle -= 360
        globalAngle += deltaAngle
        lastAngles = angles
        return globalAngle
    }

    private fun resetAngle() {
        lastAngles = when (parameters!!.IMU_AXIS) {
            Axis.X -> imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES)
            Axis.Y -> imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES)
            else -> imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
        }
        globalAngle = 0.0
    }

    fun getRobotAngle(): Rot2d {
        return Rot2d.fromDegrees(getAngle())
    }

    /**
     * Rotate by a Rot2d with a PID repeat.
     * @param rotation The Rot2d to rotate by (use Rot2d.fromDegrees() to create a new Rot2d from degrees)
     * @param power The initial power to rotate
     * @param timeoutS The max time the rotation can take, to avoid robot getting stuck.
     * @return Twist2d containing how much the robot rotated
     */
    fun rotate(rotation: Rot2d, power: Double, timeoutS: Double): Twist2d {
        var power = power
        var timeoutS = timeoutS

        parameters!!.secureParameters()

        if (!isInitializedIMU) {
            telemetry!!.addData("[/!\\]", "Call initIMU() method before rotating.")
            telemetry!!.update()
            sleep(2000)
            return Twist2d()
        }

        if (!isIMUCalibrated()) return Twist2d()

        resetAngle()
        runtime.reset()

        var setpoint = rotation.getDegrees()
        val deadZone = parameters!!.DEAD_ZONE

        if (parameters!!.INVERT_ROTATION) setpoint = -setpoint

        if (timeoutS == 0.0) {
            timeoutS = 999999999.0 //basically infinite time.
        }

        power = Math.abs(power)

        var prevErrorDelta = 0.0
        var prevMillis = 0.0
        var prevIntegral = 0.0
        var prevHeading = -1.0

        var velocityDelta: Double = 1.0
        var errorDelta = parameters!!.ERROR_TOLERANCE + 1

        var backleftpower: Double
        var backrightpower: Double
        var frontrightpower: Double
        var frontleftpower: Double
        val maxMillis = System.currentTimeMillis() + timeoutS * 1000
        val firstLoop = true

        // rotaremos hasta que se complete la vuelta
        if (setpoint < 0) {
            while (getAngle() == 0.0 && !Thread.interrupted() && System.currentTimeMillis() < maxMillis) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry!!.addData("IMU Angle", getAngle())
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
            while (errorDelta != parameters!!.ERROR_TOLERANCE && !Thread.interrupted() && System.currentTimeMillis() < maxMillis) { //entramos en un bucle hasta que los setpoint sean los esperados

                val nowMillis = System.currentTimeMillis().toDouble()

                errorDelta = -(-getAngle() + setpoint)
                velocityDelta = errorDelta - prevErrorDelta

                val multiplyByDegs = Math.abs(setpoint / 90)

                prevIntegral += errorDelta

                val proportional = errorDelta * (rkP * multiplyByDegs)
                val integral = prevIntegral * (rkI * multiplyByDegs)
                val derivative = velocityDelta * (rkD * multiplyByDegs)

                val turbo: Double = DeltaMathUtil.clamp(proportional + integral + derivative, -1.0, 1.0)
                var powerF = power * turbo

                if (powerF > 0) {
                    powerF = DeltaMathUtil.clamp(powerF, deadZone, 1.0)
                } else if (powerF < 0) {
                    powerF = DeltaMathUtil.clamp(powerF, -1.0, -deadZone)
                }

                backleftpower = powerF
                backrightpower = -powerF
                frontleftpower = powerF
                frontrightpower = -powerF

                setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)

                telemetry!!.addData("IMU Angle", getAngle())
                telemetry!!.addData("Setpoint", setpoint)
                telemetry!!.addData("Error", errorDelta)
                telemetry!!.addData("Turbo", turbo)
                telemetry!!.addData("Power", powerF)
                telemetry!!.update()

                prevErrorDelta = errorDelta
                prevMillis = nowMillis
                prevHeading = getAngle()

                sleep(3)
            }
        } else while (errorDelta != parameters!!.ERROR_TOLERANCE && !Thread.interrupted() && System.currentTimeMillis() < maxMillis) { //entramos en un bucle hasta que los setpoint sean los esperados
            val nowMillis = System.currentTimeMillis().toDouble()

            errorDelta = setpoint - getAngle()
            velocityDelta = errorDelta - prevErrorDelta

            val multiplyBy = Math.abs(setpoint / 90)

            prevIntegral += errorDelta

            val proportional = errorDelta * (rkP * multiplyBy)
            val integral = prevIntegral * (rkI * multiplyBy)
            val derivative = velocityDelta * (rkD * multiplyBy)

            val turbo: Double = DeltaMathUtil.clamp(proportional + integral + derivative, -1.0, 1.0)
            var powerF = power * turbo

            if (powerF > 0) {
                powerF = DeltaMathUtil.clamp(powerF, deadZone, 1.0)
            } else if (powerF < 0) {
                powerF = DeltaMathUtil.clamp(powerF, -1.0, -deadZone)
            }

            backleftpower = -powerF
            backrightpower = powerF
            frontleftpower = -powerF
            frontrightpower = powerF

            setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)

            telemetry!!.addData("IMU Angle", getAngle())
            telemetry!!.addData("Setpoint", setpoint)
            telemetry!!.addData("Error", errorDelta)
            telemetry!!.addData("Turbo", turbo)
            telemetry!!.addData("Power", powerF)
            telemetry!!.update()

            prevErrorDelta = errorDelta
            prevMillis = nowMillis
            prevHeading = getAngle()

            sleep(3)
        }

        // stop the movement
        setAllMotorPower(0.0, 0.0, 0.0, 0.0)
        sleep(50)
        return Twist2d(0.0, 0.0, Rot2d.fromDegrees(getAngle()))
    }

    fun encoderPIDForward(inches: Double, speed: Double, timeoutS: Double) {
        if (isInitializedEncoders == false) return
        var speed = speed
        speed = abs(speed)
        val initialRobotHeading = getAngle()
        encoderPIDDrive(speed, inches, inches, inches, inches, timeoutS, encoderDriveParameters!!.RIGHT_WHEELS_TURBO, encoderDriveParameters!!.LEFT_WHEELS_TURBO, "PID Forward", initialRobotHeading, this, encoderDriveParameters, hdw!!, telemetry!!)
    }


    fun encoderPIDBackwards(inches: Double, speed: Double, timeoutS: Double) {
        if (isInitializedEncoders == false) return
        var speed = speed
        speed = abs(speed)
        val initialRobotHeading = getAngle()
        encoderPIDDrive(speed, -inches, -inches, -inches, -inches, timeoutS, encoderDriveParameters!!.RIGHT_WHEELS_TURBO, encoderDriveParameters!!.LEFT_WHEELS_TURBO, "PID Backwards", initialRobotHeading, this, encoderDriveParameters, hdw!!, telemetry!!)
    }

    fun timePIDForward(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        val initialRobotHeading = getAngle()
        timePIDDrive(power, power, power, power, timeSecs, initialRobotHeading, this, "PID Forward")
    }

    fun timePIDBackwards(power: Double, timeSecs: Double) {
        var power = power
        power = abs(power)
        val initialRobotHeading = getAngle()
        timePIDDrive(power, power, power, power, timeSecs, initialRobotHeading, this, "PID Backwards")
    }

    //needs to extend
    fun timePIDDrive(frontleft: Double, frontright: Double, backleft: Double, backright: Double, timeSecs: Double, initialRobotHeading: Double, imu: ExtendableIMUDrivePID?, movementDescription: String?) {}

    //needs to extend
    open fun encoderPIDDrive(speed: Double,
                        frontleft: Double,
                        frontright: Double,
                        backleft: Double,
                        backright: Double,
                        timeoutS: Double,
                        rightTurbo: Double,
                        leftTurbo: Double,
                        movementDescription: String,
                        initialRobotHeading: Double,
                        imu: ExtendableIMUDrivePID,
                        encoderDriveParameters: EncoderDriveParameters,
                        hdw: DeltaHardware,
                        telemetry: Telemetry) { }

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