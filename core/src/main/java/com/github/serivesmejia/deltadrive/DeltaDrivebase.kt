package com.github.serivesmejia.deltadrive

import com.github.serivesmejia.deltadrive.utils.Task
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.github.serivesmejia.deltamath.geometry.Twist2d
import com.qualcomm.robotcore.hardware.Gamepad

interface DeltaDrivebase {

    fun joystickRobotCentric(forwardSpeed: Double, turnSpeed: Double, turbo: Double = 0.8)
    fun joystickRobotCentric(gamepad: Gamepad, controlSpeedWithTriggers: Boolean, maxMinusTurbo: Double = 0.8)
    fun joystickRobotCentric(gamepad: Gamepad, turbo: Double = 1.0)

    fun rotate(angle: Rot2d, power: Double, timeout: Double = 10.0): Task<Twist2d>

}