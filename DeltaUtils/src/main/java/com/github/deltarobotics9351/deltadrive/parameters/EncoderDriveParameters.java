package com.github.deltarobotics9351.deltadrive.parameters;

import com.qualcomm.robotcore.util.Range;

public class EncoderDriveParameters {

    public double     COUNTS_PER_REV    = 0;           //NeveRest 40 Motor counts per revolution
    public double     DRIVE_GEAR_REDUCTION    = 0 ;     // This is < 1.0 if geared UP
    public double     WHEEL_DIAMETER_INCHES   = 0 ;     // For figuring circumference

    public double RIGHT_WHEELS_TURBO = 1;
    public double LEFT_WHEELS_TURBO = 1;

    public EncoderDriveParameters(){ }

    public void secureParameters(){
        RIGHT_WHEELS_TURBO = Range.clip(Math.abs(RIGHT_WHEELS_TURBO), 0, 1);
        LEFT_WHEELS_TURBO = Range.clip(Math.abs(LEFT_WHEELS_TURBO), 0, 1);
        WHEEL_DIAMETER_INCHES = Math.abs(WHEEL_DIAMETER_INCHES);
    }

    public boolean haveBeenDefined(){
        if(COUNTS_PER_REV == 0 || DRIVE_GEAR_REDUCTION == 0 || WHEEL_DIAMETER_INCHES == 0){
            return false;
        }
        return true;
    }

}
