/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.mecanum;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltadrive.drive.mecanum.IMUDriveMecanum;
import com.deltarobotics9351.deltadrive.drive.mecanum.TimeDriveMecanum;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareMecanum;
import com.deltarobotics9351.deltadrive.extendable.linearopmodes.ExtendableLinearOpMode;
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.deltarobotics9351.deltadrive.utils.Invert;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Twist2d;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class IMUTimeMecanumLinearOpMode extends ExtendableMecanumLinearOpMode {

    private IMUDriveMecanum imuDrive;
    private TimeDriveMecanum timeDrive;

    /**
     * IMU parameters that can be defined
     */
    public final IMUDriveParameters imuParameters = new IMUDriveParameters();

    @Override
    public final void runOpMode() {
        performInit();

        imuDrive = new IMUDriveMecanum((DeltaHardwareMecanum)deltaHardware, telemetry);
        imuDrive.initIMU(imuParameters);

        while(!imuDrive.isIMUCalibrated() && !isStopRequested()){
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", imuDrive.getIMUCalibrationStatus() + "\n\nDeltaUtils v" + LibraryData.VERSION);
            telemetry.update();
        }

        timeDrive = new TimeDriveMecanum(deltaHardware, telemetry);

        Thread t = new Thread(new ParametersCheck());

        t.start();

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


    /**
     * The side of the chassis which has its motors inverted
     * @param invert the wheels invert enum
     */
    public final void setWheelsInvert(Invert invert){
        deltaHardware.invert = invert;
    }

    public final Twist2d rotate(Rot2d rot, double power, double timeoutS){
        return imuDrive.rotate(rot, power, timeoutS);
    }

    public final void forward(double power, double timeSecs){
        timeDrive.forward(power, timeSecs);
    }

    public final void backwards(double power, double timeSecs){
        timeDrive.backwards(power, timeSecs);
    }

    public final void strafeLeft(double power, double timeSecs){
        timeDrive.strafeLeft(power, timeSecs);
    }

    public final void strafeRight(double power, double timeSecs){
        timeDrive.strafeRight(power, timeSecs);
    }

    public final void turnLeft(double power, double timeSecs){
        timeDrive.turnLeft(power, timeSecs);
    }

    public final void turnRight(double power, double timeSecs){
        timeDrive.strafeRight(power, timeSecs);
    }

    public final Rot2d getRobotAngle(){
        return imuDrive.getRobotAngle();
    }

    class ParametersCheck implements Runnable{

        @Override
        public void run(){
            waitForStart();
            if(!imuParameters.haveBeenDefined()){
                telemetry.addData("[/!\\]", "Remember to define IMU constants, IMU functions may not work as expected because parameters are 0 by default.");
            }
            telemetry.update();
        }
    }

}
