package com.github.serivesmejia.deltadrive

import com.qualcomm.robotcore.hardware.Gamepad

interface Drivebase {

    fun joystickRobotCentric(forwardSpeed: Double, turnSpeed: Double, turbo: Double = 0.8)
    fun joystickRobotCentric(gamepad: Gamepad, controlSpeedWithTriggers: Boolean, maxMinusTurbo: Double = 0.8)
    fun joystickRobotCentric(gamepad: Gamepad, turbo: Double = 1.0)

}