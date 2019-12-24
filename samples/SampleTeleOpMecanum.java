package org.firstinspires.ftc.teamcode;

import com.github.deltarobotics9351.deltadrive.DeltaDriveMecanum;
import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltadrive.utils.ChassisType;
import com.github.deltarobotics9351.deltadrive.utils.OpModeStatus;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Sample DeltaDrive Mecanum TeleOp", group="Samples")
public class SampleTeleOpMecanum extends LinearOpMode {


    public DeltaHardware hdw;
    public DeltaDriveMecanum deltaDriveMecanum;

    public OpModeStatus opModeStatus = new OpModeStatus(false);

    @Override
    public void runOpMode() {

        DcMotor frontleft = hardwareMap.get(DcMotor.class, "fl"); //get all motors from hardwareMap
        DcMotor frontright = hardwareMap.get(DcMotor.class, "fr");
        DcMotor backleft = hardwareMap.get(DcMotor.class, "bl");
        DcMotor backright = hardwareMap.get(DcMotor.class, "br");

        hdw = new DeltaHardware(hardwareMap, frontleft, frontright, backleft, backright, ChassisType.mecanum); //create hardware
        deltaDriveMecanum = new DeltaDriveMecanum(hdw, telemetry, opModeStatus);

        deltaDriveMecanum.initialize(false); //initialize drive classes and imu sensor
                                             //'false' indicates that we'll not wait for the IMU sensor calibration

        telemetry.addData("[>]", "Ready!"); //send a message to driver station indicating everything's ready to run the teleop
        telemetry.update();

        waitForStart(); //wait for the driver to press [>] in the driver station

        opModeStatus.opModeIsActive = true; //tell the DeltaDrive class that the opmode has started.

        while(opModeIsActive()){
            deltaDriveMecanum.joystick(gamepad1, true, 0.7, 1); //control the wheels with the joysticks and their speed with the triggers
        }

    }

}
