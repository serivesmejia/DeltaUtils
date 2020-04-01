/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.holonomic;

import com.deltarobotics9351.deltadrive.extendable.linearopmodes.ExtendableLinearOpMode;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltadrive.utils.Invert;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.qualcomm.robotcore.hardware.DcMotor;

public class ExtendableHolonomicLinearOpMode extends ExtendableLinearOpMode {

    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backLeft = null;
    public DcMotor backRight = null;

    @Override
    public void runOpMode(){ }

    private boolean alreadyPerformedInit = false;

    public final void performInit(){
        if(alreadyPerformedInit){ return; }

        alreadyPerformedInit = true;

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

        deltaHardware = new DeltaHardwareHolonomic(hardwareMap, Invert.RIGHT_SIDE);

        deltaHardware.initHardware(frontLeft, frontRight, backLeft, backRight, WHEELS_BRAKE);

    }

}
