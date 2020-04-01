/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.holonomic;

import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters;
import com.deltarobotics9351.deltadrive.utils.DistanceUnit;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Class to use encoders to move the robot precisely (in inches) during autonomous
 */
public class EncoderDriveHolonomic {

    private DeltaHardwareHolonomic hdw;

    private final Telemetry telemetry;

    private ElapsedTime runtime = new ElapsedTime();

    private EncoderDriveParameters parameters;

    /**
     * Constructor for the encoder drive class
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry The current OpMode telemetry to show movement info.
     * @param parameters Encoder parameters, in order to calculate the ticks per inch for each motor
     */
    public EncoderDriveHolonomic(DeltaHardwareHolonomic hdw, Telemetry telemetry, EncoderDriveParameters parameters){
        this.hdw = hdw;
        this.telemetry = telemetry;
        this.parameters = parameters;

        hdw.setRunModes(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hdw.setRunModes(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void encoderDrive(double speed,
                             double frontleft,
                             double frontright,
                             double backleft,
                             double backright,
                             double timeoutS,
                              double rightTurbo,
                              double leftTurbo,
                              String movementDescription) {

        parameters.secureParameters();

        double TICKS_PER_INCH = (parameters.TICKS_PER_REV * parameters.DRIVE_GEAR_REDUCTION) /
                (parameters.WHEEL_DIAMETER_INCHES * Math.PI);

        if(parameters.DISTANCE_UNIT == DistanceUnit.CENTIMETERS) {
            frontleft *= 0.393701;
            frontright *= 0.393701;
            backleft *= 0.3937014;
            backright *= 0.393701;
        }

        int newFrontLeftTarget,
        newFrontRightTarget,
        newBackLeftTarget,
        newBackRightTarget;

        // Determine new target position, and pass to motor controller
        newFrontLeftTarget = hdw.wheelFrontLeft.getCurrentPosition() + (int) (frontleft * TICKS_PER_INCH);
        newFrontRightTarget = hdw.wheelFrontRight.getCurrentPosition() + (int) (frontright * TICKS_PER_INCH);
        newBackLeftTarget = hdw.wheelBackLeft.getCurrentPosition() + (int) (backleft * TICKS_PER_INCH);
        newBackRightTarget = hdw.wheelBackRight.getCurrentPosition() + (int) (backright * TICKS_PER_INCH);

        hdw.setTargetPositions(newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget);

        // Turn On RUN_TO_POSITION
        hdw.setRunModes(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        runtime.reset();
        hdw.wheelFrontLeft.setPower(Math.abs(speed) * leftTurbo);
        hdw.wheelFrontRight.setPower(Math.abs(speed) * rightTurbo);
        hdw.wheelBackLeft.setPower(Math.abs(speed) * leftTurbo);
        hdw.wheelBackRight.setPower(Math.abs(speed) * rightTurbo);

        double travelledAverageInches = 0;

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the repeat test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        while ((runtime.seconds() < timeoutS) &&
                (hdw.wheelFrontRight.isBusy() &&
                        hdw.wheelFrontLeft.isBusy() &&
                        hdw.wheelBackLeft.isBusy() &&
                        hdw.wheelBackRight.isBusy()) && !Thread.interrupted()) {

            double averageCurrentTicks = (hdw.wheelFrontRight.getCurrentPosition() +
                                          hdw.wheelFrontLeft.getCurrentPosition() +
                                          hdw.wheelBackLeft.getCurrentPosition() +
                                          hdw.wheelBackRight.getCurrentPosition()) / 4;

            travelledAverageInches =  averageCurrentTicks / TICKS_PER_INCH;

            telemetry.addData("[Movement]", movementDescription);

            telemetry.addData("[Target]", "%7d : %7d : %7d : %7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget);

            telemetry.addData("[Current]", "%7d : %7d : %7d : %7d",
                    hdw.wheelFrontLeft.getCurrentPosition(),
                    hdw.wheelFrontRight.getCurrentPosition(),
                    hdw.wheelBackLeft.getCurrentPosition(),
                    hdw.wheelBackRight.getCurrentPosition());

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
        encoderDrive(speed, distance, distance, distance, distance, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,  "forward");
    }

    public void backwards(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, -distance, -distance, -distance, -distance, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO, "backwards");
    }

    public void strafeLeft(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, -distance, distance, distance, -distance, timeoutS, parameters.RIGHT_WHEELS_STRAFE_TURBO, parameters.LEFT_WHEELS_STRAFE_TURBO,  "strafeLeft");
    }

    public void strafeRight(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, distance, -distance, -distance, distance, timeoutS, parameters.RIGHT_WHEELS_STRAFE_TURBO, parameters.LEFT_WHEELS_STRAFE_TURBO,  "strafeRight");
    }

    public void turnRight(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, distance, -distance, distance, -distance, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,  "turnRight");
    }

    public void turnLeft(double distance, double speed, double timeoutS) {
        distance = Math.abs(distance);
        encoderDrive(speed, -distance, distance, -distance, distance, timeoutS, parameters.RIGHT_WHEELS_TURBO, parameters.LEFT_WHEELS_TURBO,  "turnLeft");
    }

}
