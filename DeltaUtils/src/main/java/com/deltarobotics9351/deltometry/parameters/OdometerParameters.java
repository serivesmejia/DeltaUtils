/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltometry.parameters;

import com.deltarobotics9351.deltamath.MathUtil;

public class OdometerParameters {

    /**
     * The Odometer encoder ticks per revolution
     * It is probably specified in the page you bought the encoder from.
     */
    public double TICKS_PER_REV = 0;

    /**
     * The Odometer wheel diameter, in inches
     */
    public double WHEEL_DIAMETER_INCHES = 0;

    /**
     * Set to true if the Odometer is returning inverted tick values.
     */
    public boolean RETURNS_FLIPPED_VALUES = false;

    /**
     * This is < 1.0 and > 0 if geared UP
     */
    public int GEAR_REDUCTION = 1;

    /**
     * Make sure the values are in the correct range.
     */
    public void secureParameters(){
        WHEEL_DIAMETER_INCHES = Math.abs(WHEEL_DIAMETER_INCHES);
        TICKS_PER_REV = Math.abs(TICKS_PER_REV);
        GEAR_REDUCTION = Math.abs(MathUtil.clamp(GEAR_REDUCTION, 0, 1));
    }

    /**
     * Checks if any value is 0.
     * @return boolean depending if all values are or are not 0
     */
    public boolean haveBeenDefined(){
        if(TICKS_PER_REV == 0 || GEAR_REDUCTION == 0 || WHEEL_DIAMETER_INCHES == 0){
            return false;
        }
        return true;
    }

}
