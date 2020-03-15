/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.parameters;

import com.deltarobotics9351.deltadrive.utils.DistanceUnit;
import com.deltarobotics9351.deltamath.MathUtil;

/**
 * Class containing public variables for correct movement of the chassis using encoders.
 */
public class EncoderDriveParameters {

    /**
     * Ticks per revolution of the chassis motors.
     * You usually find this value in the page you bought the motors from.
     */
    public double TICKS_PER_REV = 0;
    /**
     * This is < 1.0 and > 0 if geared UP
     */
    public double     DRIVE_GEAR_REDUCTION    = 1;
    /**
     * The wheels diameter, in inches
     */
    public double     WHEEL_DIAMETER_INCHES   = 0;
    /**
     * The right side % of speed, from -1 to 1
     */
    public double RIGHT_WHEELS_TURBO = 1;
    /**
     * The left side % of speed, from -1 to 1
     */
    public double LEFT_WHEELS_TURBO = 1;

    /**
     * The right side % of speed while strafing, from -1 to 1
     */
    public double RIGHT_WHEELS_STRAFE_TURBO = 1;

    /**
     * The left side % of speed while strafing, from -1 to 1
     */
    public double LEFT_WHEELS_STRAFE_TURBO = 1;

    /**
     * The middle H-drive wheel % of speed while strafing, from -1 to 1
     */
    public double HDRIVE_WHEEL_STRAFE_TURBO = 1;

    /**
     * The unit which will be used in the movement methods.
     */
    public DistanceUnit DISTANCE_UNIT = DistanceUnit.INCHES;

    public EncoderDriveParameters(){ }

    /**
     * Make sure the values are in the correct range (0 to 1 or positive).
     */
    public void secureParameters(){
        RIGHT_WHEELS_TURBO = MathUtil.clamp(RIGHT_WHEELS_TURBO, -1, 1);
        LEFT_WHEELS_TURBO = MathUtil.clamp(LEFT_WHEELS_TURBO, -1, 1);

        RIGHT_WHEELS_STRAFE_TURBO = MathUtil.clamp(RIGHT_WHEELS_STRAFE_TURBO, -1, 1);
        LEFT_WHEELS_STRAFE_TURBO = MathUtil.clamp(LEFT_WHEELS_STRAFE_TURBO, -1, 1);

        HDRIVE_WHEEL_STRAFE_TURBO = MathUtil.clamp(HDRIVE_WHEEL_STRAFE_TURBO, -1, 1);

        WHEEL_DIAMETER_INCHES = Math.abs(WHEEL_DIAMETER_INCHES);
        TICKS_PER_REV = Math.abs(TICKS_PER_REV);
        DRIVE_GEAR_REDUCTION = Math.abs(MathUtil.clamp(DRIVE_GEAR_REDUCTION, 0, 1));
    }

    /**
     * Checks if any value is 0.
     * @return boolean depending if all values are or are not 0
     */
    public boolean haveBeenDefined(){
        if(TICKS_PER_REV == 0 || DRIVE_GEAR_REDUCTION == 0 || WHEEL_DIAMETER_INCHES == 0){
            return false;
        }
        return true;
    }

}
