/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.holonomic;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltadrive.drive.holonomic.IMUDrivePIDHolonomic;
import com.deltarobotics9351.deltadrive.drive.holonomic.TimeDriveHolonomic;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Twist2d;
import com.deltarobotics9351.pid.PIDCoefficients;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class IMUPIDTimeHolonomicLinearOpMode extends ExtendableHolonomicLinearOpMode {

    private IMUDrivePIDHolonomic imuDrive;

    private TimeDriveHolonomic timeDrive;

    /**
     * IMU parameters that can be defined
     */
    public final IMUDriveParameters imuParameters = new IMUDriveParameters();

    @Override
    public final void runOpMode() {
        performInit();

        imuDrive = new IMUDrivePIDHolonomic((DeltaHardwareHolonomic)deltaHardware, telemetry);
        imuDrive.initIMU(imuParameters);

        timeDrive = new TimeDriveHolonomic((DeltaHardwareHolonomic)deltaHardware, telemetry);

        while(!imuDrive.isIMUCalibrated() && !isStopRequested()){
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", imuDrive.getIMUCalibrationStatus() + "\n\nDeltaUtils v" + LibraryData.VERSION);
            telemetry.update();
        }

        _runOpMode();

        RobotHeading.stop();
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
     * Set the rotate PID coefficients
     * @param pid the PID coefficients
     */
    public final void setRotatePID(PIDCoefficients pid){
        imuDrive.setRotatePID(pid);
    }

    /**
     * @return the rotate Proportional coefficient
     */
    public final double getRotateP(){
        return imuDrive.getRotateP();
    }

    /**
     * @return the rotate Integral coefficient
     */
    public final double getRotateI(){
        return imuDrive.getRotateI();
    }

    /**
     * @return the rotate Derivative coefficient
     */
    public final double getRotateD(){
        return imuDrive.getRotateD();
    }

    /**
     * @return the current rotate PIDCoefficients object
     */
    public final PIDCoefficients getRotatePID(){ return imuDrive.getRotatePID(); }

    /**
     * Set the drive PID coefficients
     * @param pid the PID coefficients
     */
    public final void setDrivePID(PIDCoefficients pid){
        imuDrive.setDrivePID(pid);
    }

    /**
     * @return the drive Proportional coefficient
     */
    public final double getDriveP(){
        return imuDrive.getDriveP();
    }

    /**
     * @return the drive Integral coefficient
     */
    public final double getDriveI(){
        return imuDrive.getDriveI();
    }

    /**
     * @return the drive Derivative coefficient
     */
    public final double getDriveD(){
        return imuDrive.getDriveD();
    }

    /**
     * @return the current rotate PIDCoefficients object
     */
    public final PIDCoefficients getDrivePID(){ return imuDrive.getDrivePID(); }

    public final Twist2d rotate(Rot2d rot, double power, double timeoutSecs){
        return imuDrive.rotate(rot, power, timeoutSecs);
    }

    public final Rot2d getRobotAngle(){
        return imuDrive.getRobotAngle();
    }

}
