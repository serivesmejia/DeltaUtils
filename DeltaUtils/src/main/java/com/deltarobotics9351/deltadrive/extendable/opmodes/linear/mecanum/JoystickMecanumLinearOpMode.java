package com.deltarobotics9351.deltadrive.extendable.opmodes.linear.mecanum;

import com.deltarobotics9351.deltadrive.drive.mecanum.JoystickDriveMecanum;
import com.deltarobotics9351.deltadrive.drive.mecanum.hardware.DeltaHardwareMecanum;
import com.deltarobotics9351.deltadrive.utils.Invert;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class JoystickMecanumLinearOpMode extends LinearOpMode {

    private JoystickDriveMecanum joystick;
    private DeltaHardwareMecanum deltaHardware;

    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;

    /**
     * Enum that defines which side of the chassis will be inverted (motors)
     */
    public Invert WHEELS_INVERT = Invert.RIGHT_SIDE;

    /**
     * boolean that defines if motors brake when their power is 0
     */
    public boolean WHEELS_BRAKE = true;

    /**
     * boolean that indicates if we'll update the RobotHeading in this OpMode
     * You need 2 Expansion Hubs for this!
     */
    public boolean UPDATE_ROBOT_HEADING = false;

    /**
     * boolean that indicates if we'll reset the RobotHeading
     */
    public boolean RESET_ROBOT_HEADING = false;

    /**
     * Robot's initial heading
     *
     */
    public Rot2d ROBOT_INITIAL_HEADING = new Rot2d();

    @Override
    public final void runOpMode() {
        setup();

        if(RESET_ROBOT_HEADING){
            RobotHeading.reset();
        }

        if(UPDATE_ROBOT_HEADING){
            RobotHeading.start(ROBOT_INITIAL_HEADING, hardwareMap);
        }

        if(frontLeft == null || frontRight == null || backLeft == null || backRight == null){
            telemetry.addData("[/!\\]", "OpMode will not start in order to avoid Robot Controller crash.");
            telemetry.addData("frontLeft", (frontLeft == null) ? "is null" : "OK");
            telemetry.addData("frontRight", (frontRight == null) ? "is null" : "OK");
            telemetry.addData("backLeft", (backLeft == null) ? "is null" : "OK");
            telemetry.addData("backRight", (backRight == null) ? "is null" : "OK");
            telemetry.addData("POSSIBLE SOLUTION 1", "Override setup() method in your OpMode class and\ndefine the null motor variables specified above.");
            telemetry.addData("POSSIBLE SOLUTION 2", "Check that all your motors are correctly named and\nthat they are get from the hardwareMap");
            telemetry.update();
            while(opModeIsActive());
            return;
        }

        deltaHardware = new DeltaHardwareMecanum(hardwareMap, WHEELS_INVERT);

        deltaHardware.initHardware(frontLeft, frontRight, backLeft, backRight, WHEELS_BRAKE);

        joystick = new JoystickDriveMecanum(deltaHardware);

        _runOpMode();

        RobotHeading.stop();
    }


    /**
     * Overridable void to be executed after all required variables are initialized
     */
    public void _runOpMode(){

    }

    /**
     * Overridable void to define all wheel motors, and the uppercase variables
     * Define frontLeft, frontRight, backLeft and backRight DcMotor variables here!
     */
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
