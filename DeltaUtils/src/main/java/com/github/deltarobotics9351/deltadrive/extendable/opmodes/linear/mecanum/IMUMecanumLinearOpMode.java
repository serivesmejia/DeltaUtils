package com.github.deltarobotics9351.deltadrive.extendable.opmodes.linear.mecanum;

import com.github.deltarobotics9351.deltadrive.drive.mecanum.IMUDriveMecanum;
import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.github.deltarobotics9351.deltadrive.utils.ChassisType;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class IMUMecanumLinearOpMode extends LinearOpMode {

    private IMUDriveMecanum imuDrive;

    private DeltaHardware deltaHardware;

    public IMUDriveParameters imuParameters = new IMUDriveParameters();

    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;

    @Override
    public final void runOpMode() {
        defineHardware();

        if(frontLeft == null || frontRight == null || backLeft == null || backRight == null){
            telemetry.addData("[/!\\]", "OpMode will not start in order to avoid Robot Controller crash.");
            telemetry.addData("frontLeft", (frontLeft == null) ? "is null" : "OK");
            telemetry.addData("frontRight", (frontRight == null) ? "is null" : "OK");
            telemetry.addData("backLeft", (backLeft == null) ? "is null" : "OK");
            telemetry.addData("backRight", (backRight == null) ? "is null" : "OK");
            telemetry.addData("POSSIBLE SOLUTION 1", "Override defineHardware() method in your OpMode class and\ndefine the null motor variables specified above.");
            telemetry.addData("POSSIBLE SOLUTION 2", "Check that all your motors are correctly named and\nthat they are get from the hardwareMap");
            telemetry.update();
            while(opModeIsActive());
        }
        deltaHardware = new DeltaHardware(hardwareMap, frontLeft, frontRight, backLeft, backRight, ChassisType.mecanum);

        imuDrive = new IMUDriveMecanum(deltaHardware, this);
        imuDrive.initIMU(imuParameters);

        while (!imuDrive.isIMUCalibrated() && !isStopRequested()) {
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", imuDrive.getIMUCalibrationStatus());
            telemetry.update();
        }

        Thread t = new Thread(new ParametersCheck());

        t.start();

        _runOpMode();
    }


    public void _runOpMode(){

    }

    public void defineHardware(){

    }

    public final void rotate(double degrees, double power){
        imuDrive.rotate(degrees, power);
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
