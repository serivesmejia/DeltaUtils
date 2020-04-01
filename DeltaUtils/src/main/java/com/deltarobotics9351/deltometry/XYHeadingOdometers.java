/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltometry;

import com.deltarobotics9351.deltamath.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class XYHeadingOdometers {

    Odometer xOdometer;
    Odometer yOdometer;
    Odometer headingOdometer;

    int xTicks;
    int yTicks;
    int headingTicks;

    HardwareMap hdwMap;

    public XYHeadingOdometers(Odometer xOdometer, Odometer yOdometer, Odometer headingOdometer, HardwareMap hdwMap){

        this.xOdometer = xOdometer;
        this.yOdometer = yOdometer;
        this.headingOdometer = headingOdometer;

        this.hdwMap = hdwMap;

    }

    /**
     * Get the X, Y and Heading distance in ticks
     * @return a Pose2d with the distance in ticks
     */
    public Pose2d getDistanceTicks(){
        update();
        return new Pose2d(xTicks, yTicks, headingTicks);
    }

    /**
     * Get the X, Y and Heading revolutions
     * @return a Pose2d with x, y & heading revolutions
     */
    public Pose2d getRevolutions(){

        update();

        double REVOLUTIONS_X = (xOdometer.parameters.TICKS_PER_REV * xOdometer.parameters.GEAR_REDUCTION) /
                xTicks;

        double REVOLUTIONS_Y = (yOdometer.parameters.TICKS_PER_REV * yOdometer.parameters.GEAR_REDUCTION) /
                yTicks;

        double REVOLUTIONS_HEADING = (headingOdometer.parameters.TICKS_PER_REV * headingOdometer.parameters.GEAR_REDUCTION) /
                headingTicks;

        return new Pose2d(REVOLUTIONS_X, REVOLUTIONS_Y, REVOLUTIONS_HEADING);

    }

    /**
     * Get the X, Y and Heading distance in inches
     * @return a Pose2d with the distance in inches
     */
    public Pose2d getDistanceInches(){

        update();

        double TICKS_PER_INCH_X = (xOdometer.parameters.TICKS_PER_REV * xOdometer.parameters.GEAR_REDUCTION) /
                (xOdometer.parameters.WHEEL_DIAMETER_INCHES * Math.PI);

        double TICKS_PER_INCH_Y = (yOdometer.parameters.TICKS_PER_REV * yOdometer.parameters.GEAR_REDUCTION) /
                (yOdometer.parameters.WHEEL_DIAMETER_INCHES * Math.PI);

        double TICKS_PER_INCH_HEADING = (headingOdometer.parameters.TICKS_PER_REV * headingOdometer.parameters.GEAR_REDUCTION) /
                (headingOdometer.parameters.WHEEL_DIAMETER_INCHES * Math.PI);

        double xInches = TICKS_PER_INCH_X * xTicks;
        double yInches = TICKS_PER_INCH_Y * yTicks;
        double headingInches = TICKS_PER_INCH_HEADING * headingTicks;

        return new Pose2d(xInches, yInches, headingInches);

    }

    /**
     * Reset all the encoder values back to 0
     */
    public void resetCount(){
        DcMotorEx xMotor = hdwMap.get(DcMotorEx.class, xOdometer.deviceName);
        DcMotorEx yMotor = hdwMap.get(DcMotorEx.class, yOdometer.deviceName);
        DcMotorEx headingMotor = hdwMap.get(DcMotorEx.class, headingOdometer.deviceName);

        xMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        yMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        headingMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * Update the ticks count with a bulk read.
     */
    private void update(){

        xOdometer.parameters.secureParameters();
        yOdometer.parameters.secureParameters();
        headingOdometer.parameters.secureParameters();

        List<LynxModule> allModules = hdwMap.getAll(LynxModule.class);

        for (LynxModule lynxModule : allModules){
            lynxModule.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
            lynxModule.clearBulkCache();
        }

        DcMotorEx xMotor = hdwMap.get(DcMotorEx.class, xOdometer.deviceName);
        DcMotorEx yMotor = hdwMap.get(DcMotorEx.class, yOdometer.deviceName);
        DcMotorEx headingMotor = hdwMap.get(DcMotorEx.class, headingOdometer.deviceName);

        xMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        yMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        headingMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        xTicks = (xOdometer.parameters.RETURNS_FLIPPED_VALUES) ? -xMotor.getCurrentPosition() : xMotor.getCurrentPosition();
        yTicks = (yOdometer.parameters.RETURNS_FLIPPED_VALUES) ? -yMotor.getCurrentPosition() : yMotor.getCurrentPosition();
        headingTicks = (headingOdometer.parameters.RETURNS_FLIPPED_VALUES) ? -headingMotor.getCurrentPosition() : headingMotor.getCurrentPosition();

    }


}
