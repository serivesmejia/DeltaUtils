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
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters
import com.deltarobotics9351.deltadrive.utils.Axis
import com.deltarobotics9351.deltamath.DeltaMathUtil
import com.deltarobotics9351.deltamath.geometry.Rot2d
import com.deltarobotics9351.deltamath.geometry.Twist2d

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.util.ElapsedTime

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation

//srry this is an old class, most of the comments are in spanish.
open class ExtendableIMUDrive {

    var imu: BNO055IMU? = null
    var hdw: DeltaHardware? = null

    var telemetry: Telemetry? = null

    var lastAngles: Orientation = Orientation()
    var globalAngle = 0.0

    var parameters: IMUDriveParameters? = null

    private var isInitialized = false

    private val runtime = ElapsedTime()

    private var allowedDeltaHardwareType = DeltaHardware.Type.DEFAULT

    /**
     * Constructor for the IMU drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     * @param hdw The initialized DeltaHardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    constructor (hdw: DeltaHardware, telemetry: Telemetry, deltaHardwareType: DeltaHardware.Type) {
        this.hdw = hdw
        this.telemetry = telemetry
        this.allowedDeltaHardwareType = deltaHardwareType
    }

    /**
     * Initialize the IMU sensor and set the parameters
     * (Remember to wait for the imu calibration [waitForIMUCalibration()] before the OpMode starts!)
     * @param parameters Object containing the parameters for IMU Turns
     */
    fun initIMU(parameters: IMUDriveParameters) {
        if (isInitialized) return
        require(!(hdw!!.type !== allowedDeltaHardwareType)) { "Given DeltaHardware in the constructor is not the expected type $allowedDeltaHardwareType" }

        this.parameters = parameters
        parameters.secureParameters()

        val param = BNO055IMU.Parameters()

        param.mode = BNO055IMU.SensorMode.IMU
        param.angleUnit = BNO055IMU.AngleUnit.DEGREES
        param.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        param.loggingEnabled = false

        imu = hdw!!.hdwMap!!.get(BNO055IMU::class.java, parameters.IMU_HARDWARE_NAME)
        imu!!.initialize(param)
        isInitialized = true
    }

    /**
     * Loop until the IMU sensor reports it is calibrated or until OpMode stops.
     */
    fun waitForIMUCalibration() {
        while (!imu!!.isGyroCalibrated && !Thread.interrupted()) {
            telemetry!!.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...")
            telemetry!!.addData("[Status]", "${getIMUCalibrationStatus()}\n\nDeltaUtils v${LibraryData.VERSION}")
            telemetry!!.update()
        }
    }

    /**
     * Get the IMU calibration status as an String.
     * @return the String containing the sensor calibration status.
     */
    fun getIMUCalibrationStatus(): String {
        return imu!!.calibrationStatus.toString()
    }

    /**
     * @return boolean depending if IMU sensor is calibrated.
     */
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
        lastAngles = imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
        globalAngle = 0.0
    }

    /**
     * Get the current robot angle as a Rot2d
     * WARNING: It resets back to 0 after every turn.
     * @return Rot2d with current the Robot angle.
     */
    fun getRobotAngle(): Rot2d? {
        return Rot2d.fromDegrees(getAngle())
    }

    var correctedTimes = 0

    /**
     * Rotate to a specific angle, with error correction
     * @param rotation
     * @param power Speed to rotate
     * @param timeoutS Max time (in seconds) that the rotation may take, set to 0 for infinite time.
     * @return a Twist2d representing how much the robot rotated
     */
    fun rotate(rotation: Rot2d, power: Double, timeoutS: Double): Twist2d {
        var power = power
        var timeoutS = timeoutS

        if (!isInitialized) {
            telemetry!!.addData("[/!\\]", "Call initIMU() method before rotating.")
            telemetry!!.update()
            sleep(2000)
            return Twist2d()
        }

        if (!isIMUCalibrated()) return Twist2d()

        resetAngle()

        var degrees = rotation.getDegrees()

        if (parameters!!.INVERT_ROTATION) degrees = -degrees

        if (correctedTimes == 0) {
            runtime.reset()
            if (runtime.seconds() >= timeoutS) {
                correctedTimes = 0
                return Twist2d()
            }
        }

        if (timeoutS == 0.0) {
            timeoutS = 99999999.0 //literally forever.
        }

        power = Math.abs(power)
        val backleftpower: Double
        val backrightpower: Double
        val frontrightpower: Double
        val frontleftpower: Double
        parameters!!.secureParameters()
        if (degrees < 0) //si es menor que 0 significa que el robot girara a la derecha
        {   // girar a la derecha
            backleftpower = power
            backrightpower = -power
            frontleftpower = power
            frontrightpower = -power
        } else if (degrees > 0) // si es mayor que 0 significa que el robot girara a la izquierda
        {   // girar a la izquierda
            backleftpower = -power
            backrightpower = power
            frontleftpower = -power
            frontrightpower = power
        } else return Twist2d()

        // definimos el power de los motores
        setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower)

        // rotaremos hasta que se complete la vuelta
        if (degrees < 0) {
            while (getAngle() == 0.0 && !Thread.interrupted() && runtime.seconds() < timeoutS) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry!!.addData("IMU Angle", getAngle())
                telemetry!!.addData("Targeted degrees", degrees)
                telemetry!!.update()
            }
            while (getAngle() > degrees && !Thread.interrupted() && runtime.seconds() < timeoutS) { //entramos en un bucle hasta que los degrees sean los esperados
                telemetry!!.addData("IMU Angle", getAngle())
                telemetry!!.addData("Targeted degrees", degrees)
                telemetry!!.update()
            }
        } else while (getAngle() < degrees && !Thread.interrupted() && runtime.seconds() < timeoutS) { //entramos en un bucle hasta que los degrees sean los esperados
            telemetry!!.addData("IMU Angle", getAngle())
            telemetry!!.addData("Targeted degrees", degrees)
            telemetry!!.update()
        }

        // stop the movement
        hdw!!.setAllMotorPower(0.0, 0.0, 0.0, 0.0)
        return correctRotation(degrees)
    }

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

    private fun correctRotation(expectedAngle: Double): Twist2d? {
        correctedTimes += 1

        if (correctedTimes > parameters!!.ROTATE_MAX_CORRECTION_TIMES) {
            correctedTimes = 0
            return Twist2d(0.0, 0.0, Rot2d.fromDegrees(getAngle()))
        }

        val deltaAngle: Double = DeltaMathUtil.deltaDegrees(expectedAngle, getAngle())

        telemetry!!.addData("error", deltaAngle)
        telemetry!!.update()

        rotate(Rot2d.fromDegrees(deltaAngle), parameters!!.ROTATE_CORRECTION_POWER, 0.0)

        return Twist2d(0.0, 0.0, Rot2d.fromDegrees(getAngle()))
    }

    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

}

