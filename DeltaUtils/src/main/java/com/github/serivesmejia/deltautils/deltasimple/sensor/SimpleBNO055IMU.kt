package com.github.serivesmejia.deltautils.deltasimple.sensor

import com.github.serivesmejia.deltautils.LibraryData
import com.github.serivesmejia.deltautils.deltadrive.utils.Axis
import com.github.serivesmejia.deltautils.deltamath.geometry.Rot2d
import com.qualcomm.hardware.bosch.BNO055IMU
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation

class SimpleBNO055IMU(private val imu: BNO055IMU) {

    private var initialized = false

    private var lastAngles: Orientation = Orientation()
    private var globalAngle = 0.0

    private var lastAngle = Rot2d()

    private var axis = Axis.Z

    fun initIMU() {

        if(initialized) return

        val param = BNO055IMU.Parameters()

        param.mode = BNO055IMU.SensorMode.IMU
        param.angleUnit = BNO055IMU.AngleUnit.DEGREES
        param.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        param.loggingEnabled = false

        imu.initialize(param)
        initialized = true

    }

    /**
     * Enter in a while repeat until the IMU reports it is calibrated or until the opmode stops
     */
    fun waitForIMUCalibration(telemetry: Telemetry) {
        while (!imu!!.isGyroCalibrated && !Thread.interrupted()) {
            telemetry!!.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...")
            telemetry!!.addData("[Status]", "${getIMUCalibrationStatus()}\n\nDeltaUtils v${LibraryData.VERSION}")
            telemetry!!.update()
        }
    }

    /**
     * Enter in a while repeat until the IMU reports it is calibrated or until the opmode stops
     */
    fun waitForIMUCalibration() {
        while (!imu!!.isGyroCalibrated && !Thread.interrupted()) { }
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

    fun isInitialized() = initialized

    fun getAngle(axis: Axis): Rot2d {
        // We have to process the angle because the imu works in euler angles so the axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.
        var angles = imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)

        var deltaAngle: Float = 0F

        when (axis) {
            Axis.X -> deltaAngle = angles.thirdAngle - lastAngles.thirdAngle
            Axis.Y -> angles.secondAngle - lastAngles.secondAngle
            else -> deltaAngle = angles.firstAngle - lastAngles.firstAngle
        }

        if (deltaAngle < -180)
            deltaAngle += 360
        else if (deltaAngle > 180)
            deltaAngle -= 360

        globalAngle += deltaAngle
        lastAngles = angles

        lastAngle = Rot2d.degrees(globalAngle)
        return lastAngle

    }

    fun getAngle() : Rot2d = getAngle(axis)

    fun getLastAngle() : Rot2d = lastAngle

    fun resetAngle() {

        lastAngles =  imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)

        globalAngle = 0.0
    }

    fun setAxis(axis: Axis) {
        this.axis = axis
    }

}