package com.deltarobotics9351.deltometry

import com.deltarobotics9351.deltometry.position.RobotPosition

interface Odometers {

    fun update(robotPos: RobotPosition)

    fun update()

    fun getRobotPosition() : RobotPosition

    fun getEncoderTicks() : IntArray

    fun resetEncoderTicks()

    fun setOdometers(x: Odometer, y: Odometer, heading: Odometer)

}