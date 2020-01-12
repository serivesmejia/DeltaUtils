package com.github.deltarobotics9351.deltadrive.parameters;

import com.qualcomm.robotcore.util.Range;

public class EncoderDriveParameters {

    public double     COUNTS_PER_REV    = 1120;           //NeveRest 40 Motor counts per revolution
    public double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    public double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference

    public double RIGHT_WHEELS_TURBO = 1;
    public double LEFT_WHEELS_TURBO = 1;

    public EncoderDriveParameters(){ }

    public void secureParameters(){
        RIGHT_WHEELS_TURBO = Range.clip(Math.abs(RIGHT_WHEELS_TURBO), 0, 1);
        LEFT_WHEELS_TURBO = Range.clip(Math.abs(LEFT_WHEELS_TURBO), 0, 1);
    }

}
