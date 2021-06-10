package com.github.serivesmejia.deltadrive

import com.github.serivesmejia.deltadrive.utils.task.Task

interface DeltaHolonomicDrivebase : DeltaDrivebase {

    override fun joystickRobotCentric(forwardSpeed: Double, turnSpeed: Double, turbo: Double) =
        joystickRobotCentric(forwardSpeed, 0.0, turnSpeed, turbo)

    fun joystickRobotCentric(forwardSpeed: Double, strafeSpeed: Double, turnSpeed: Double, turbo: Double)

    fun timeStrafeLeft(power: Double, timeSecs: Double): Task<Unit>
    fun timeStrafeRight(power: Double, timeSecs: Double): Task<Unit>

}