/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.holonomic;

import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Vec2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

/**
 * Class to control a mecanum chassis during teleop using a gamepad's joysticks.
 */
public class JoystickFieldCentricDriveHolonomic {

    //wheel motor power
    public double wheelFrontRightPower = 0;
    public double wheelFrontLeftPower = 0;
    public double wheelBackRightPower = 0;
    public double wheelBackLeftPower = 0;

    public double turbo = 0;

    private DeltaHardwareHolonomic hdw;

    /**
     * Constructor for the Joystick Field Centric Drive
     * @param hdw The initialized hardware containing all the chassis motors
     */
    public JoystickFieldCentricDriveHolonomic(DeltaHardwareHolonomic hdw){ this.hdw = hdw; }

    /**
     * Control a mecanum chassis using a gamepad's joysticks, field centric.
     * Field centric works like 3d games: Pushing forwards will always make the robot drive away from you, no matter the rotation.
     * Use left stick to go forward, backwards and strafe, and right stick to turn
     * This method should be called always in the teleop repeat to update the motor powers
     * @param gamepad the gamepad used to control the chassis.
     * @param turbo the chassis max speed, from 0 to 1
     * @param heading the current robot heading
     */
    public void joystick(Gamepad gamepad, double turbo, Rot2d heading){

        turbo = Math.abs(turbo);
        turbo = Range.clip(Math.abs(turbo), 0, 1);

        this.turbo = turbo;

        double y1 = -gamepad.left_stick_y;
        double x1 = gamepad.left_stick_x;
        double x2 = gamepad.right_stick_x;

        Vec2d inputvector = new Vec2d(x1, y1);
        inputvector.rotate(heading.invert());

        double theta = Math.atan2(y1, x1);

        double piconst = Math.PI / 4;

        double inputMag = inputvector.mag();

        wheelFrontLeftPower = x2 + (Math.sin(theta + piconst) * inputMag);
        wheelFrontRightPower = (Math.sin(theta - piconst) * inputMag) - x2;
        wheelBackLeftPower = x2 + (Math.sin(theta - piconst) * inputMag);
        wheelBackRightPower = (Math.sin(theta + piconst) * inputMag) - x2;

        switch(hdw.invert) {
            case RIGHT_SIDE:

                wheelFrontRightPower = -wheelFrontRightPower;
                wheelBackRightPower = -wheelBackRightPower;
                break;

            case LEFT_SIDE:
                wheelFrontLeftPower = -wheelFrontLeftPower;
                wheelBackLeftPower = -wheelBackLeftPower;
                break;

            case BOTH_SIDES:

                wheelFrontRightPower = -wheelFrontRightPower;
                wheelBackRightPower = -wheelBackRightPower;
                wheelFrontLeftPower = -wheelFrontLeftPower;
                wheelBackLeftPower = -wheelBackLeftPower;
                break;
        }

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