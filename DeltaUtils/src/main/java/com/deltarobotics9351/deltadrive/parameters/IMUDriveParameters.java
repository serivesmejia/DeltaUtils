/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.parameters;

import com.qualcomm.robotcore.util.Range;
import com.deltarobotics9351.deltadrive.utils.Axis;

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

    /**
     * The axis we'll use for the robot rotation. (Z by default)
     * If the expansion hub 1 is mounted horizontally, flat on the robot chassis, you'd use the Z axis.
     * If the expansion hub 1 is mounted vertically, in a wall or something, you'd use the Y axis.
     */
    public Axis IMU_AXIS = Axis.Z;

    public IMUDriveParameters(){ }

    /**
     * Make sure parameters are in a correct range (0 to 1 and/or positive)
     */
    public void secureParameters(){
        ROTATE_MAX_CORRECTION_TIMES = Math.abs(ROTATE_MAX_CORRECTION_TIMES);
        ROTATE_CORRECTION_POWER = Range.clip(Math.abs(ROTATE_CORRECTION_POWER), 0, 1);

        IMU_AXIS = IMU_AXIS == null ? Axis.Z : IMU_AXIS; //set the value to something if it is null, just in case...
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
