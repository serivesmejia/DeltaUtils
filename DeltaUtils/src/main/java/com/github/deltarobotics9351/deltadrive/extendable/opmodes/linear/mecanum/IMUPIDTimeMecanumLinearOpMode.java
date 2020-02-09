package com.github.deltarobotics9351.deltadrive.extendable.opmodes.linear.mecanum;

import com.github.deltarobotics9351.deltadrive.drive.mecanum.IMUDrivePIDMecanum;
import com.github.deltarobotics9351.deltadrive.drive.mecanum.TimeDriveMecanum;
import com.github.deltarobotics9351.deltadrive.drive.mecanum.hardware.DeltaHardwareMecanum;
import com.github.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.github.deltarobotics9351.deltadrive.utils.Invert;
import com.github.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.github.deltarobotics9351.deltamath.geometry.Rot2d;
import com.github.deltarobotics9351.pid.PIDConstants;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class IMUPIDTimeMecanumLinearOpMode extends LinearOpMode {

    private IMUDrivePIDMecanum imuDrive;
    private PIDConstants pidConstants = new PIDConstants(0, 0, 0);

    private TimeDriveMecanum timeDrive;

    private DeltaHardwareMecanum deltaHardware;

    /**
     * IMU parameters that can be defined
     */
    public IMUDriveParameters imuParameters = new IMUDriveParameters();

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

        imuDrive = new IMUDrivePIDMecanum(deltaHardware, this);
        imuDrive.initIMU(imuParameters, pidConstants);

        while(!imuDrive.isIMUCalibrated() && !isStopRequested()){
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", imuDrive.getIMUCalibrationStatus());
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
    public void _runOpMode(){

    }

    /**
     * Overridable void to define all wheel motors, and the uppercase variables
     * Define frontLeft, frontRight, backLeft and backRight DcMotor variables here!
     */
    public void setup(){

    }

    public final void rotate(Rot2d rot, double power, double timeoutSecs){
        imuDrive.rotate(rot, power, timeoutSecs);
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
