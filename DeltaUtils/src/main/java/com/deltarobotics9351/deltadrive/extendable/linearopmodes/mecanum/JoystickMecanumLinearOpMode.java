/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.mecanum;

import com.deltarobotics9351.deltadrive.drive.mecanum.JoystickDriveMecanum;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareMecanum;
import com.deltarobotics9351.deltadrive.extendable.linearopmodes.ExtendableLinearOpMode;
import com.deltarobotics9351.deltadrive.utils.Invert;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.deltarobotics9351.deltaevent.gamepad.SuperGamepad;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class JoystickMecanumLinearOpMode extends ExtendableMecanumLinearOpMode {

    public JoystickDriveMecanum joystick;
    public SuperGamepad superGamepad1;
    public SuperGamepad superGamepad2;

    @Override
    public final void runOpMode() {
        superGamepad1 = new SuperGamepad(gamepad1);
        superGamepad2 = new SuperGamepad(gamepad2);

        performInit();

        joystick = new JoystickDriveMecanum((DeltaHardwareMecanum) deltaHardware);

        _runOpMode();

        RobotHeading.stop();
    }


    /**
     * Overridable void to be executed after all required variables are initialized
     */
    @Override
    public void _runOpMode(){

    }

    /**
     * Overridable void to define all wheel motors, and the uppercase variables
     * Define frontLeft, frontRight, backLeft and backRight DcMotor variables here!
     */
    @Override
    public void setup(){

    }

    public final void joystick(Gamepad gamepad, boolean controlSpeedWithTriggers, double maxMinusPower){
        if(controlSpeedWithTriggers) {
            if (gamepad.left_trigger > 0.1) {
                joystick.joystick(gamepad, 1 - Range.clip(gamepad.left_trigger, 0, maxMinusPower));
            } else if (gamepad.right_trigger > 0.1) {
                joystick.joystick(gamepad, 1 - Range.clip(gamepad.right_trigger, 0, maxMinusPower));
            } else {
                joystick.joystick(gamepad, 1);
            }
        }else{
            joystick.joystick(gamepad, 1);
        }
    }

}
