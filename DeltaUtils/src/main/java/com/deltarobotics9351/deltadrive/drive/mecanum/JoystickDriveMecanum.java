package com.deltarobotics9351.deltadrive.drive.mecanum;

import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.qualcomm.robotcore.hardware.Gamepad;

public class JoystickDriveMecanum {


    //wheel motor power
    public double wheelFrontRightPower = 0;
    public double wheelFrontLeftPower = 0;
    public double wheelBackRightPower = 0;
    public double wheelBackLeftPower = 0;

    public double turbo = 0;

    public DeltaHardware hdw;

    public JoystickDriveMecanum(DeltaHardware hdw){ this.hdw = hdw; }

    public void joystick(Gamepad gamepad1, double turbo){

        this.turbo = turbo;

        double y1 = gamepad1.left_stick_y;
        double x1 = -gamepad1.left_stick_x;
        double x2 = -gamepad1.right_stick_x;
        wheelFrontRightPower = y1 - x2 - x1;
        wheelBackRightPower = y1 - x2 + x1;
        wheelFrontLeftPower = y1 + x2 + x1;
        wheelBackLeftPower = y1 + x2 - x1;

        double max = Math.max(Math.abs(wheelFrontRightPower), Math.max(Math.abs(wheelBackRightPower),
                Math.max(Math.abs(wheelFrontLeftPower), Math.abs(wheelBackLeftPower))));

        if (max > 1.0)
        {
            wheelFrontRightPower /= max;
            wheelBackRightPower  /= max;
            wheelFrontLeftPower  /= max;
            wheelBackLeftPower   /= max;
        }

        wheelFrontRightPower *= turbo;
        wheelBackRightPower  *= turbo;
        wheelFrontLeftPower  *= turbo;
        wheelBackLeftPower   *= turbo;

        hdw.wheelFrontRight.setPower(wheelFrontRightPower);
        hdw.wheelFrontLeft.setPower(wheelFrontLeftPower);
        hdw.wheelBackRight.setPower(wheelBackRightPower);
        hdw.wheelBackLeft.setPower(wheelBackLeftPower);
    }

}
