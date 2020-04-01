/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.hdrive;

import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHDrive;
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters;
import com.deltarobotics9351.deltadrive.utils.DistanceUnit;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Class to use encoders to move the robot precisely (in inches) during autonomous
 */
public class EncoderDriveHDrive {

    private DeltaHardwareHDrive hdw;

    private final Telemetry telemetry;

    private ElapsedTime runtime = new ElapsedTime();

    private EncoderDriveParameters parameters;

    /**
     * Constructor for the encoder drive class
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry The current OpMode telemetry to show movement info.
     * @param parameters Encoder parameters, in order to calculate the ticks per inch for each motor
     */
    public EncoderDriveHDrive(DeltaHardwareHDrive hdw, Telemetry telemetry, EncoderDriveParameters parameters){
        this.hdw = hdw;
        this.telemetry = telemetry;
        this.parameters = parameters;

        hdw.setRunModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hdw.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void encoderDrive(double speed,
                             double left,
                             double right,
                             double middle,
                             double timeoutS,
                              double rightTurbo,
                              double leftTurbo,
                              double middleTurbo,
                              String movementDescription) {

        parameters.secureParameters();

        double TICKS_PER_INCH = (parameters.TICKS_PER_REV * parameters.DRIVE_GEAR_REDUCTION) /
                (parameters.WHEEL_DIAMETER_INCHES * Math.PI);

        if(parameters.DISTANCE_UNIT == DistanceUnit.CENTIMETERS) {
            left *= 0.393701;
            right *= 0.393701;
            middle *= 0.3937014;
        }

        int newLeftTarget,
        newRightTarget,
        newMiddleTarget;

        // Determine new target position, and pass to motor controller
        newLeftTarget = hdw.wheelsLeft.getCurrentPosition() + (int) (left * TICKS_PER_INCH);
        newRightTarget = hdw.wheelsRight.getCurrentPosition() + (int) (right * TICKS_PER_INCH);
        newMiddleTarget = hdw.wheelMiddle.getCurrentPosition() + (int) (middle * TICKS_PER_INCH);

        hdw.setTargetPositions(newLeftTarget, newRightTarget, newMiddleTarget);

        // Turn On RUN_TO_POSITION
        hdw.setRunModes(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        runtime.reset();
        hdw.wheelsLeft.setPower(Math.abs(speed) * leftTurbo);
        hdw.wheelsRight.setPower(Math.abs(speed) * rightTurbo);
        hdw.wheelMiddle.setPower(Math.abs(speed) * middleTurbo);

        double travelledAverageInches = 0;

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the repeat test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        while ((runtime.seconds() < timeoutS) &&
                (hdw.wheelsLeft.isBusy() &&
                hdw.wheelsRight.isBusy())
                && !Thread.interrupted()) {

            if(middle != 0 && !hdw.wheelMiddle.isBusy()) return;

            double averageCurrentTicks = 0;

            if(middle == 0){

                averageCurrentTicks = (hdw.wheelFrontRight.getCurrentPosition() +
                        hdw.wheelFrontLeft.getCurrentPosition()) / 2;

            }else if(middle != 0 && left == 0 && right == 0){

                averageCurrentTicks = (hdw.wheelMiddle.getCurrentPosition());

            }else{

                averageCurrentTicks = (hdw.wheelFrontRight.getCurrentPosition() +
                        hdw.wheelFrontLeft.getCurrentPosition()
                        + hdw.wheelMiddle.getTargetPosition()) / 3;
            }

            travelledAverageInches = averageCurrentTicks / TICKS_PER_INCH;

            telemetry.addData("[Movement]", movementDescription);

            telemetry.addData("[Target]", "%7d : %7d : %7d",
                    newLeftTarget,
                    newRightTarget,
                    newMiddleTarget
            );

            telemetry.addData("[Current]", "%7d : %7d : %7d",
                    hdw.wheelsLeft.getCurrentPosition(),
                    hdw.wheelsRight.getCurrentPosition(),
                    hdw.wheelMiddle.getCurrentPosition());

            telemetry.addData("[Travelled Avg Inches]", travelledAverageInches);

            telemetry.update();
        }

        telemetry.update();

        // Stop all motion
        hdw.setAllMotorPower(0, 0, 0, 0);

        // Turn off RUN_TO_POSITION
        hdw.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void forward(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, distance, distance, 0, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO, parameters.HDRIVE_WHEEL_STRAFE_TURBO,  "forward");
    }

    public void backwards(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, -distance, -distance, 0, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO, parameters.HDRIVE_WHEEL_STRAFE_TURBO,  "backwards");
    }

    public void strafeLeft(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, 0, 0, distance, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO, parameters.HDRIVE_WHEEL_STRAFE_TURBO,  "strafeLeft");
    }

    public void strafeRight(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, 0, 0, -distance, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO, parameters.HDRIVE_WHEEL_STRAFE_TURBO,  "strafeRight");
    }

    public void turnRight(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, distance, -distance, 0, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO, parameters.HDRIVE_WHEEL_STRAFE_TURBO,  "turnRight");
    }

    public void turnLeft(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, -distance, distance, 0, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO, parameters.HDRIVE_WHEEL_STRAFE_TURBO,  "turnLeft");
    }

}