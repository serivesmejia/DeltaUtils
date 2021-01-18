package com.github.serivesmejia.deltadrive.drivebase

import com.github.serivesmejia.deltadrive.HolonomicDrivebase
import com.github.serivesmejia.deltadrive.drive.holonomic.JoystickDriveHolonomic
import com.github.serivesmejia.deltadrive.hardware.DeltaHardwareHolonomic
import com.github.serivesmejia.deltamath.DeltaMathUtil
import com.qualcomm.robotcore.hardware.Gamepad

class MecanumDrive(val hdw: DeltaHardwareHolonomic) : HolonomicDrivebase {

    private val joystickDriveRobotCentric = JoystickDriveHolonomic(hdw)

    override fun joystickRobotCentric(forwardSpeed: Double, strafeSpeed: Double, turnSpeed: Double, turbo: Double) {
        joystickDriveRobotCentric.update(forwardSpeed, strafeSpeed, turnSpeed, turbo, turbo)
    }

    override fun joystickRobotCentric(gamepad: Gamepad, controlSpeedWithTriggers: Boolean, maxMinusTurbo: Double) {
        joystickDriveRobotCentric.setGamepad(gamepad)

        val maxMinusTurbo = DeltaMathUtil.clamp(maxMinusTurbo, 0.0, 1.0)
        var minusTurbo = 0.0

        if(controlSpeedWithTriggers) {
            when {
                gamepad.left_trigger > 0.2 -> minusTurbo = gamepad.left_trigger * maxMinusTurbo
                gamepad.right_trigger > 0.2 -> minusTurbo = gamepad.left_trigger * maxMinusTurbo
            }
            val turbo = DeltaMathUtil.clamp(1 - minusTurbo, 0.0, 1.0)
            joystickDriveRobotCentric.update(turbo)
        } else {
            joystickDriveRobotCentric.update(1.0)
        }
    }

    override fun joystickRobotCentric(gamepad: Gamepad, turbo: Double) {
        joystickDriveRobotCentric.setGamepad(gamepad)
        joystickDriveRobotCentric.update(turbo)
    }

}