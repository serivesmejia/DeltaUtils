package com.github.deltarobotics9351.deltadrive.drive.mecanum;

import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltadrive.parameters.EncoderDriveConstants;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class       EncoderDriveMecanum {

    public DeltaHardware hdw;

    private final Telemetry telemetry;

    private ElapsedTime runtime = new ElapsedTime();

    public EncoderDriveMecanum(DeltaHardware hdw, Telemetry telemetry){
        this.hdw = hdw;
        this.telemetry = telemetry;

        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    public void encoderDrive(double speed,
                             double frontleft,
                             double frontright,
                             double backleft,
                             double backright,
                             double timeoutS) {

        hdw.allWheelsForward();

        double COUNTS_PER_INCH = (EncoderDriveConstants.COUNTS_PER_REV * EncoderDriveConstants.DRIVE_GEAR_REDUCTION) /
                (EncoderDriveConstants.WHEEL_DIAMETER_INCHES * 3.1415);

        int newFrontLeftTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;

        // Determine new target position, and pass to motor controller
        newFrontLeftTarget = hdw.wheelFrontLeft.getCurrentPosition() + (int) (frontleft * COUNTS_PER_INCH);
        newFrontRightTarget = hdw.wheelFrontRight.getCurrentPosition() + (int) (frontright * COUNTS_PER_INCH);
        newBackLeftTarget = hdw.wheelBackLeft.getCurrentPosition() + (int) (backleft * COUNTS_PER_INCH);
        newBackRightTarget = hdw.wheelBackRight.getCurrentPosition() + (int) (backright * COUNTS_PER_INCH);

        hdw.wheelFrontLeft.setTargetPosition(newFrontLeftTarget);
        hdw.wheelFrontRight.setTargetPosition(newFrontRightTarget);
        hdw.wheelBackLeft.setTargetPosition(newBackLeftTarget);
        hdw.wheelBackRight.setTargetPosition(newBackRightTarget);

        // Turn On RUN_TO_POSITION
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        runtime.reset();
        hdw.wheelFrontLeft.setPower(Math.abs(speed));
        hdw.wheelFrontRight.setPower(Math.abs(speed));
        hdw.wheelBackLeft.setPower(Math.abs(speed));
        hdw.wheelBackRight.setPower(Math.abs(speed));

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        // However, if you require that BOTH motors have finished their moves before the robot continues
        // onto the next step, use (isBusy() || isBusy()) in the loop test.
        while ((runtime.seconds() < timeoutS) &&
                (hdw.wheelFrontRight.isBusy() &&
                        hdw.wheelFrontLeft.isBusy() &&
                        hdw.wheelBackRight.isBusy() &&
                        hdw.wheelBackLeft.isBusy())) {

            telemetry.addData("[>]", "Running to %7d :%7d : %7d :%7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget);

            telemetry.addData("[>]", "Running at %7d :%7d : %7d :%7d",
                    hdw.wheelFrontLeft.getCurrentPosition(),
                    hdw.wheelFrontRight.getCurrentPosition(),
                    hdw.wheelBackLeft.getCurrentPosition(),
                    hdw.wheelBackRight.getCurrentPosition());

            telemetry.update();
        }

        telemetry.update();

        // Stop all motion
        hdw.wheelFrontRight.setPower(0);
        hdw.wheelFrontLeft.setPower(0);
        hdw.wheelBackLeft.setPower(0);
        hdw.wheelBackRight.setPower(0);

        // Turn off RUN_TO_POSITION
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hdw.defaultWheelsDirection();
    }

    public void forward(double inches, double speed, double timeoutS) {
        encoderDrive(speed, -inches, inches, inches, inches, timeoutS);
    }

    public void backwards(double inches, double speed, double timeoutS) {
        encoderDrive(speed, inches, -inches, -inches, -inches, timeoutS);
    }

    public void strafeLeft(double inches, double speed, double timeoutS) {
        encoderDrive(speed, -inches, -inches, -inches, inches, timeoutS);
    }

    public void strafeRight(double inches, double speed, double timeoutS) {
        encoderDrive(speed, inches, inches, inches, -inches, timeoutS);
    }

    public void turnRight(double inches, double speed, double timeoutS) {
        encoderDrive(speed, -inches, -inches, inches, -inches, timeoutS);
    }

    public void turnLeft(double inches, double speed, double timeoutS) {
        encoderDrive(speed, inches, inches, -inches, inches, timeoutS);
    }

}
