package com.github.serivesmejia.deltadrive

interface DeltaHolonomicDrivebase : DeltaDrivebase {
    override fun joystickRobotCentric(forwardSpeed: Double, turnSpeed: Double, turbo: Double) =
        joystickRobotCentric(forwardSpeed, 0.0, turnSpeed, turbo)

    fun joystickRobotCentric(forwardSpeed: Double, strafeSpeed: Double, turnSpeed: Double, turbo: Double)
}