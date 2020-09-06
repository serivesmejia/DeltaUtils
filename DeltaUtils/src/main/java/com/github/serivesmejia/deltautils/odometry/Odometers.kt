package com.github.serivesmejia.deltautils.odometry

import com.github.serivesmejia.deltautils.odometry.position.RobotPosition

interface Odometers {

    fun update(robotPos: RobotPosition)

    fun update()

    fun getRobotPosition() : RobotPosition

    fun getEncoderTicks() : IntArray

    fun resetEncoderTicks()

    fun setOdometers(x: Odometer, y: Odometer, heading: Odometer)

}