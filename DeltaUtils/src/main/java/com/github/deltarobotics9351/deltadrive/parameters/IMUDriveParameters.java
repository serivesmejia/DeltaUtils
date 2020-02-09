package com.github.deltarobotics9351.deltadrive.parameters;

import com.qualcomm.robotcore.util.Range;

/**
 * Class containing public variables for setting IMU rotation parameters
 */
public class IMUDriveParameters {

    /**
     * The maximum times a rotation can be corrected.
     */
    public int ROTATE_MAX_CORRECTION_TIMES = 0;
    /**
     * The power in which the rotations will be corrected
     */
    public double ROTATE_CORRECTION_POWER = 0;

    public IMUDriveParameters(){ }

    /**
     * Make sure parameters are in a correct range (0 to 1 and/or positive)
     */
    public void secureParameters(){
        ROTATE_MAX_CORRECTION_TIMES = Math.abs(ROTATE_MAX_CORRECTION_TIMES);
        ROTATE_CORRECTION_POWER = Range.clip(Math.abs(ROTATE_CORRECTION_POWER), 0, 1);
    }

    /**
     * Checks if any value is 0.
     * @return boolean depending if all values are not 0
     */
    public boolean haveBeenDefined(){
        if(ROTATE_MAX_CORRECTION_TIMES == 0 || ROTATE_CORRECTION_POWER == 0){
            return false;
        }
        return true;
    }

}
