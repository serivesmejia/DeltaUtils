/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.holonomic;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltadrive.drive.holonomic.EncoderDriveHolonomic;
import com.deltarobotics9351.deltadrive.drive.holonomic.IMUDrivePIDHolonomic;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters;
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Twist2d;
import com.deltarobotics9351.pid.PIDCoefficients;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class IMUPIDEncoderHolonomicLinearOpMode extends ExtendableHolonomicLinearOpMode {

    private IMUDrivePIDHolonomic imuDrive;

    private EncoderDriveHolonomic encoderDrive;

    /**
     * Encoder parameters that can be defined
     */
    public EncoderDriveParameters encoderParameters = new EncoderDriveParameters();

    /**
     * IMU parameters that can be defined
     */
    public IMUDriveParameters imuParameters = new IMUDriveParameters();

    @Override
    public final void runOpMode() {
        performInit();

        imuDrive = new IMUDrivePIDHolonomic((DeltaHardwareHolonomic)deltaHardware, telemetry);
        imuDrive.initIMU(imuParameters);

        encoderDrive = new EncoderDriveHolonomic((DeltaHardwareHolonomic)deltaHardware, telemetry, encoderParameters);

        while(!imuDrive.isIMUCalibrated() && !isStopRequested()){
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", imuDrive.getIMUCalibrationStatus() + "\n\nDeltaUtils v" + LibraryData.VERSION);
            telemetry.update();
        }

        Thread t = new Thread(new ParametersCheck());

        t.start();

        _runOpMode();

        RobotHeading.stop();
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

    public final Twist2d rotate(Rot2d rot, double power, double timeoutS){
        return imuDrive.rotate(rot, power, timeoutS);
    }

    public final void forward(double inches, double speed, double timeOutSecs){
        encoderDrive.forward(inches, speed, timeOutSecs);
    }

    public final void backwards(double inches, double speed, double timeOutSecs){
        encoderDrive.backwards(inches, speed, timeOutSecs);
    }

    public final void strafeLeft(double inches, double speed, double timeOutSecs){
        encoderDrive.strafeLeft(inches, speed, timeOutSecs);
    }

    public final void strafeRight(double inches, double speed, double timeOutSecs){
        encoderDrive.strafeRight(inches, speed, timeOutSecs);
    }

    public final void turnLeft(double inches, double speed, double timeOutSecs){
        encoderDrive.turnLeft(inches, speed, timeOutSecs);
    }

    public final void turnRight(double inches, double speed, double timeOutSecs){
        encoderDrive.turnRight(inches, speed, timeOutSecs);
    }

    public final Rot2d getRobotAngle(){
        return imuDrive.getRobotAngle();
    }

    class ParametersCheck implements Runnable{

        @Override
        public void run(){
            waitForStart();
            if(!encoderParameters.haveBeenDefined()){
                telemetry.addData("[/!\\]", "Remember to define encoder constants, encoder functions will not work because parameters are 0 by default.");
            }
            telemetry.update();
        }
    }

}
