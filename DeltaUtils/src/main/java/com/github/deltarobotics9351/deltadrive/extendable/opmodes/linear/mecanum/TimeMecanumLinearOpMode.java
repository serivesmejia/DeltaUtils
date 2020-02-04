package com.github.deltarobotics9351.deltadrive.extendable.opmodes.linear.mecanum;

import com.github.deltarobotics9351.deltadrive.drive.mecanum.TimeDriveMecanum;
import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltadrive.utils.ChassisType;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class TimeMecanumLinearOpMode extends LinearOpMode {

    private TimeDriveMecanum timeDrive;

    private DeltaHardware deltaHardware;

    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;

    @Override
    public final void runOpMode(){
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

        timeDrive = new TimeDriveMecanum(deltaHardware, telemetry);

        _runOpMode();
    }


    public void _runOpMode(){

    }

    public void defineHardware(){

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

}
