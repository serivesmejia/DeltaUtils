/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive.holonomic;

import com.deltarobotics9351.deltadrive.drive.ExtendableIMUDrivePID;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters;
import com.deltarobotics9351.deltadrive.utils.DistanceUnit;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Turning using the IMU sensor integrated in the Expansion Hub and a PID repeat, which slows the motors speed the more closer the robot is to the target.
 */
public class IMUDrivePIDHolonomic extends com.deltarobotics9351.deltadrive.drive.ExtendableIMUDrivePID {

    ElapsedTime runtime = new ElapsedTime();

    /**
     * Constructor for the IMU PID Mecanum drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     *
     * @param hdw       The initialized hardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    public IMUDrivePIDHolonomic(DeltaHardwareHolonomic hdw, Telemetry telemetry) {
        super(hdw, telemetry);
        setAllowedDeltaHardwareType(DeltaHardware.Type.HOLONOMIC);
    }

    public void encoderPIDDrive(double speed,
                                double frontleft,
                                double frontright,
                                double backleft,
                                double backright,
                                double timeoutS,
                                double rightTurbo,
                                double leftTurbo,
                                String movementDescription,
                                double initialRobotHeading,
                                ExtendableIMUDrivePID imu,
                                EncoderDriveParameters encoderParameters,
                                DeltaHardware hdw,
                                Telemetry telemetry) {

        encoderParameters.secureParameters();

        double frontleftpower, frontrightpower, backleftpower, backrightpower;

        double TICKS_PER_INCH = (encoderParameters.TICKS_PER_REV * encoderParameters.DRIVE_GEAR_REDUCTION) /
                (encoderParameters.WHEEL_DIAMETER_INCHES * Math.PI);

        if(encoderParameters.DISTANCE_UNIT == DistanceUnit.CENTIMETERS) {
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

        frontleftpower = Math.abs(speed) * leftTurbo;
        frontrightpower = Math.abs(speed) * rightTurbo;
        backleftpower = Math.abs(speed) * leftTurbo;
        backrightpower = Math.abs(speed) * rightTurbo;

        hdw.setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower);

        double travelledAverageInches = 0;

        double error;

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

            //----- PID CODE START -----
            error = initialRobotHeading - imu.getRobotAngle().getDegrees();



            //----- PID CODE END -----


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

}