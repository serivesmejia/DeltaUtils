/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.utils;

import android.util.Log;

import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Static class to get the robot heading from the driver's view.
 * You need 2 IMU sensors for this, which means you need 2 Expansion Hubs.
 * This uses a separate thread to update the heading, so you will not need to update it in your opmode
 * You'kD probably want to use this for field centric drive.
 */
public class RobotHeading {

    private static volatile BNO055IMU imu1;

    private static volatile boolean hasRun = false;

    private static volatile boolean isRunning = true;

    private static volatile Rot2d heading = new Rot2d();

    private static volatile boolean isIMUCalibrated = false;

    private static volatile Orientation lastAngles = new Orientation();
    private static volatile double globalAngle;

    private static volatile Axis imuAxis = Axis.Z;

    private static Thread updateTh = new Thread(new updateThread());

    /**
     * Start the robot heading update.
     * Starting heading is only set the first time this function is used.
     * @param startingRobotHeading the heading in which the robot starts.
     * @param hardwareMap the current OpMode's hardware map
     */
    public static void start(Rot2d startingRobotHeading, HardwareMap hardwareMap){

        if(isRunning){
            return;
        }

        if(!hasRun){
            heading.rotate(startingRobotHeading);
            hasRun = true;
        }

        updateTh.start();

        calibrateIMU(hardwareMap);

        isRunning = true;
        isIMUCalibrated = imu1.isGyroCalibrated();

    }

    /**
     * Start the robot heading update from 0 or from last heading.
     * @param hardwareMap the current OpMode's hardware map
     */
    public static void start(HardwareMap hardwareMap){
        start(new Rot2d(), hardwareMap);
    }

    /**
     * Stops the robot heading update, you need to call this method at the end of your OpMode if you called start() before.
     */
    public static void stop(){

        if(!isRunning){
            return;
        }

        updateTh.interrupt(); //interrupt the thread

        isRunning = false; //set all variables to default values
        isIMUCalibrated = false;
        imu1 = null;
    }

    /**
     * @return boolean depending if RobotHeading thread is running
     */
    public static boolean isRunning(){
        return isRunning;
    }

    /**
     * Reset the heading to 0 and, if this is not running, sets this to "has never run before" mode, which adds the robotInitialHeading to heading when start() is called.
     */
    public static void reset(){
        heading = new Rot2d();
        if(!isRunning) {
            hasRun = false;
        }
    }

    /**
     * Set the axis used by the IMU to get the degrees.
     * @param axis the axis mentioned above
     */
    public static void setIMUAxis(Axis axis){
        if(axis == null) return;
        imuAxis = axis;
    }

    private static void calibrateIMU(HardwareMap hardwareMap){
        BNO055IMU.Parameters param = new BNO055IMU.Parameters();

        param.mode                = BNO055IMU.SensorMode.IMU;
        param.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        param.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        param.loggingEnabled      = false;

        imu1 = hardwareMap.get(BNO055IMU.class, "imu 1");

        imu1.initialize(param);

        while(!imu1.isGyroCalibrated());
    }

    /**
     * @return the last completly written heading.
     */
    public static Rot2d getHeading(){
        if(isWritingHeading){
            return lastHeading;
        }
        return heading;
    }

    private static volatile Rot2d setTo = null;

    /**
     * Sets the current heading in the next update cyle.
     * @param to the heading to set to.
     */
    public static void set(Rot2d to){
        setTo = to; //set the current heading value to this in the next update cyle.
    }

    private static double getAngle()
    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles;

        switch(imuAxis) {
            case X:
                angles = imu1.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
                break;
            case Y:
                angles = imu1.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES);
                break;
            default:
                angles = imu1.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                break;
        }

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    private static void resetAngle()
    {
        switch(imuAxis) {
            case X:
                lastAngles = imu1.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
                break;
            case Y:
                lastAngles = imu1.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES);
                break;
            default:
                lastAngles = imu1.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                break;
        }

        globalAngle = 0;
    }

    private static volatile Rot2d lastHeading;
    private static volatile boolean isWritingHeading = false; //this is to avoid returning a Rot2d that hasn't been completly written yet.

    private static class updateThread implements Runnable {

        @Override
        public void run() {
            Log.i("RobotHeading", "Started the RobotHeading.updateThread");
            while(!Thread.currentThread().isInterrupted()){
                lastHeading = heading;

                if(setTo != null){ heading = setTo; setTo = null; }

                if(imu1.isGyroCalibrated()){
                    isWritingHeading = true;
                    heading.rotate(Rot2d.fromDegrees(getAngle()));
                    resetAngle();
                    isWritingHeading = false;
                    sleep(20);
                }
            }
            Log.i("RobotHeading", "Stopped the RobotHeading.updateThread");
        }

        public void sleep(long millis){
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
