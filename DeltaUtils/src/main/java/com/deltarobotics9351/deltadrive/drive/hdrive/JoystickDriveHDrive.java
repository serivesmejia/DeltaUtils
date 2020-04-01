/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.hdrive;

import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHDrive;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

/**
 * Class to control a mecanum chassis during teleop using a gamepad's joysticks.
 */
public class JoystickDriveHDrive {

    //wheel motor power
    public double wheelsRightPower = 0;
    public double wheelsLeftPower = 0;
    public double wheelMiddlePower = 0;

    public double turbo = 0;

    private DeltaHardwareHDrive hdw;

    /**
     * Constructor for the Joystick Drive
     * @param hdw The initialized hardware containing all the chassis motors
     */
    public JoystickDriveHDrive(DeltaHardwareHDrive hdw){ this.hdw = hdw; }

    /**
     * Control an H-Drive chassis using a gamepad's joysticks.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called always in the teleop repeat to update the motor powers
     * @param gamepad the gamepad used to control the chassis.
     * @param turbo the chassis % of speed, from 0 to 1
     */
    public void joystick(Gamepad gamepad, double turbo){

        turbo = Math.abs(turbo);
        turbo = Range.clip(turbo, 0, 1);

        this.turbo = turbo;

        double drive = -gamepad.left_stick_y;
        double strafe = gamepad.left_stick_x;
        double turn = gamepad.right_stick_x;

        wheelsRightPower = drive - turn;
        wheelsLeftPower = drive + turn;
        wheelMiddlePower = strafe;

        double max = Math.max(Math.abs(wheelsLeftPower), Math.abs(wheelsRightPower));

        if (max > 1.0)
        {
            wheelsLeftPower /= max;
            wheelsRightPower  /= max;
        }

        wheelsLeftPower *= turbo;
        wheelsRightPower  *= turbo;
        wheelMiddlePower  *= turbo;

        hdw.setAllMotorPower(wheelsLeftPower, wheelsRightPower, wheelMiddlePower);
    }

}
