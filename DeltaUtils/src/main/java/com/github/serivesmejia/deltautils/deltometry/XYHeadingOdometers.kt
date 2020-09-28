package com.github.serivesmejia.deltautils.deltometry

import com.github.serivesmejia.deltautils.deltadrive.utils.Robot
import com.github.serivesmejia.deltautils.deltometry.parameters.OdometersParameters
import com.github.serivesmejia.deltautils.deltometry.position.RobotPosition
import com.qualcomm.robotcore.hardware.DcMotor

class XYHeadingOdometers(private var xOdom: Odometer, private var yOdom: Odometer, private var headingOdom: Odometer, private var params: OdometersParameters) : Odometers {

    private var robotPos = RobotPosition(this)

    val hdwMap = Robot.getCurrentOpMode().hardwareMap

    var xMotor: DcMotor = hdwMap.get(DcMotor::class.java, xOdom.deviceName)
    var yMotor: DcMotor = hdwMap.get(DcMotor::class.java, xOdom.deviceName)
    var headingMotor: DcMotor = hdwMap.get(DcMotor::class.java, headingOdom.deviceName)

    private var lastY = 0
    private var lastX = 0
    private var lastH = 0

    private var angBias = 0.0
    private var lastRawAngRad = 0.0

    private var xTrackWidth = params.X_TRACK_WIDTH
    private var yTrackWidth = params.Y_TRACK_WIDTH

    override fun update(robotPos: RobotPosition) {

        val xTicksPerInch = xOdom.parameters.TICKS_PER_REV * xOdom.parameters.GEAR_REDUCTION.getRatioAsDecimal() /
                (xOdom.parameters.WHEEL_DIAMETER_INCHES * Math.PI)
        val yTicksPerInch = xOdom.parameters.TICKS_PER_REV * xOdom.parameters.GEAR_REDUCTION.getRatioAsDecimal() /
                (xOdom.parameters.WHEEL_DIAMETER_INCHES * Math.PI)

        val encoderTicks = getEncoderTicks()

        val xEncoderDelta = encoderTicks[0] - lastX
        val yEncoderDelta = encoderTicks[1] - lastY

        val xWheelDelta = xEncoderDelta * xTicksPerInch
        val yWheelDelta = yEncoderDelta * yTicksPerInch



        lastX = xEncoderDelta
        lastY = yEncoderDelta

    }

    override fun update() = update(robotPos)

    override fun getRobotPosition(): RobotPosition = robotPos

    override fun getEncoderTicks(): IntArray {

        xMotor!!.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        yMotor!!.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        return intArrayOf(xMotor!!.currentPosition, yMotor!!.currentPosition)

    }

    override fun resetEncoderTicks() {

        xMotor!!.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        xMotor!!.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

    }

    override fun setOdometers(x: Odometer, y: Odometer, heading: Odometer) {
        xOdom = x;
        yOdom = y;
    }

    fun setOdometers(x: Odometer, y: Odometer) = setOdometers(x, y, Odometer())

}