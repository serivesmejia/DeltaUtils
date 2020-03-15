/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltometry;

import com.deltarobotics9351.deltamath.geometry.Pose2d;
import com.deltarobotics9351.deltamath.geometry.Vec2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Hardware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XYOdometers {

    Odometer xOdometer;
    Odometer yOdometer;

    int xTicks;
    int yTicks;

    HardwareMap hdwMap;

    public XYOdometers(Odometer xOdometer, Odometer yOdometer, HardwareMap hdwMap){

        this.xOdometer = xOdometer;
        this.yOdometer = yOdometer;

        this.hdwMap = hdwMap;

    }

    /**
     * Get the X & Y distance in ticks
     * @return a Pose2d with the distance in ticks
     */
    public Pose2d getDistanceTicks(){
        update();
        return new Pose2d(xTicks, yTicks, 0);
    }

    /**
     * Get the X & Y distance in inches
     * @return a Pose2d with the distance in inches
     */
    public Pose2d getDistanceInches(){

        update();

        double TICKS_PER_INCH_X = (xOdometer.parameters.TICKS_PER_REV * xOdometer.parameters.GEAR_REDUCTION) /
                (xOdometer.parameters.WHEEL_DIAMETER_INCHES * Math.PI);

        double TICKS_PER_INCH_Y = (yOdometer.parameters.TICKS_PER_REV * yOdometer.parameters.GEAR_REDUCTION) /
                (yOdometer.parameters.WHEEL_DIAMETER_INCHES * Math.PI);

        double xInches = TICKS_PER_INCH_X * xTicks;
        double yInches = TICKS_PER_INCH_Y * yTicks;

        return new Pose2d(xInches, yInches, 0);

    }

    /**
     * Get the X & Y revolutions
     * @return a Pose2d with x & y revolutions
     */
    public Pose2d getRevolutions(){

        update();

        double REVOLUTIONS_X = (xOdometer.parameters.TICKS_PER_REV * xOdometer.parameters.GEAR_REDUCTION) /
                xTicks;

        double REVOLUTIONS_Y = (yOdometer.parameters.TICKS_PER_REV * yOdometer.parameters.GEAR_REDUCTION) /
                yTicks;

        return new Pose2d(REVOLUTIONS_X, REVOLUTIONS_Y, 0);

    }

    /**
     * Reset all the encoder values back to 0
     */
    public void resetCount(){
        DcMotorEx xMotor = hdwMap.get(DcMotorEx.class, xOdometer.deviceName);
        DcMotorEx yMotor = hdwMap.get(DcMotorEx.class, yOdometer.deviceName);

        xMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        yMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Update the ticks count with a bulk read.
     */
    private void update() {

        xOdometer.parameters.secureParameters();
        yOdometer.parameters.secureParameters();

        List<LynxModule> allModules = hdwMap.getAll(LynxModule.class);

        for (LynxModule lynxModule : allModules){
            lynxModule.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            lynxModule.clearBulkCache();
        }

        DcMotorEx xMotor = hdwMap.get(DcMotorEx.class, xOdometer.deviceName);
        DcMotorEx yMotor = hdwMap.get(DcMotorEx.class, yOdometer.deviceName);

        xMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        yMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        xTicks = (xOdometer.parameters.RETURNS_FLIPPED_VALUES) ? -xMotor.getCurrentPosition() : xMotor.getCurrentPosition();
        yTicks = (yOdometer.parameters.RETURNS_FLIPPED_VALUES) ? -yMotor.getCurrentPosition() : yMotor.getCurrentPosition();

        for (LynxModule lynxModule : allModules){
            lynxModule.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

    }


}
