/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.mecanum;

import com.deltarobotics9351.deltadrive.drive.mecanum.EncoderDriveMecanum;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareMecanum;
import com.deltarobotics9351.deltadrive.extendable.linearopmodes.ExtendableLinearOpMode;
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters;
import com.deltarobotics9351.deltadrive.utils.Invert;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class EncoderMecanumLinearOpMode extends ExtendableMecanumLinearOpMode {

    private EncoderDriveMecanum encoderDrive;
    public EncoderDriveParameters encoderParameters = new EncoderDriveParameters();

    @Override
    public final void runOpMode() {

        performInit();

        encoderDrive = new EncoderDriveMecanum((DeltaHardwareMecanum) deltaHardware, telemetry, encoderParameters);

        Thread t = new Thread(new ParametersCheck());

        t.start();

        _runOpMode();

        RobotHeading.stop();
    }


    /**
     * Overridable void to be executed after all required variables are initialized
     * (Remember to override setup() and define the 4 DcMotor variables in there!)
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

    public final void forward(double inches, double speed, double timeOutSecs){
        encoderDrive.forward(inches, speed, timeOutSecs);
    }

    public final void backwards(double inches, double speed, double timeOutSecs){
        encoderDrive.backwards(inches, speed, timeOutSecs);
    }

    public final void strafeLeft(double inches, double speed, double timeOutSecs){
        encoderDrive.strafeLeft(inches, speed, timeOutSecs);
    }

    public final void strafeRight(double inches, double speed, double timeOutSecs){
        encoderDrive.strafeRight(inches, speed, timeOutSecs);
    }

    public final void turnLeft(double inches, double speed, double timeOutSecs){
        encoderDrive.turnLeft(inches, speed, timeOutSecs);
    }

    public final void turnRight(double inches, double speed, double timeOutSecs){
        encoderDrive.turnRight(inches, speed, timeOutSecs);
    }


    class ParametersCheck implements Runnable{

        @Override
        public void run(){
            waitForStart();
            if(!encoderParameters.haveBeenDefined()) {
                telemetry.addData("[/!\\]", "Remember to define encoder constants, encoder functions will not work because parameters are 0 by default. ");
            }
            telemetry.update();
        }
    }

}
