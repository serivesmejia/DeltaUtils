/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.extendable.linearopmodes.holonomic;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltadrive.drive.holonomic.IMUDrivePIDHolonomic;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.deltarobotics9351.deltadrive.utils.RobotHeading;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Twist2d;
import com.deltarobotics9351.pid.PIDCoefficients;

/**
 * Remember to override setup() and define the 4 DcMotor variables in there!
 */
public class IMUPIDHolonomicLinearOpMode extends ExtendableHolonomicLinearOpMode {

    private IMUDrivePIDHolonomic imuDrive;

    /**
     * IMU parameters that can be defined
     */
    public final IMUDriveParameters imuParameters = new IMUDriveParameters();

    @Override
    public final void runOpMode() {
        performInit();

        imuDrive = new IMUDrivePIDHolonomic((DeltaHardwareHolonomic) deltaHardware, telemetry);
        imuDrive.initIMU(imuParameters);

        while(!imuDrive.isIMUCalibrated() && !isStopRequested()){
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", imuDrive.getIMUCalibrationStatus() + "\n\nDeltaUtils v" + LibraryData.VERSION);
            telemetry.update();
        }

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
     * Set the PID coefficients
     * @param pid the PID coefficients
     */
    public final void setPID(PIDCoefficients pid){
        imuDrive.setRotatePID(pid);
    }

    /**
     * @return the P coefficient
     */
    public final double getP(){
        return imuDrive.getRkP();
    }

    /**
     * @return the I coefficient
     */
    public final double getI(){
        return imuDrive.getI();
    }

    /**
     * @return the D coefficient
     */
    public final double getD(){
        return imuDrive.getRkD();
    }

    /**
     * @return the current PIDCoefficients object
     */
    public final PIDCoefficients getPID(){ return imuDrive.getPID(); }

    public final Twist2d rotate(Rot2d rot, double power, double timeoutS){
        return imuDrive.rotate(rot, power, timeoutS);
    }

    public final Rot2d getRobotAngle(){
        return imuDrive.getRobotAngle();
    }


}
