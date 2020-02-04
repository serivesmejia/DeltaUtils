package com.github.deltarobotics9351.deltadrive.drive.mecanum;

import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

public class JoystickDriveMecanum {


    //wheel motor power
    public double wheelFrontRightPower = 0;
    public double wheelFrontLeftPower = 0;
    public double wheelBackRightPower = 0;
    public double wheelBackLeftPower = 0;

    public double turbo = 0;

    private DeltaHardware hdw;

    public JoystickDriveMecanum(DeltaHardware hdw){ this.hdw = hdw; }

    public void joystick(Gamepad gamepad, double turbo){

        turbo = Math.abs(turbo);

        this.turbo = turbo;

        turbo = Range.clip(Math.abs(turbo), 0, 1);

        double y1 = -gamepad.left_stick_y;
        double x1 = gamepad.left_stick_x;
        double x2 = gamepad.right_stick_x;
        wheelFrontRightPower = -(y1 - x2 - x1);
        wheelBackRightPower = -(y1 - x2 + x1);
        wheelFrontLeftPower = (y1 + x2 + x1);
        wheelBackLeftPower = (y1 + x2 - x1);

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
