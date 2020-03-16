/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.parameters;

import com.deltarobotics9351.deltamath.MathUtil;
import com.qualcomm.robotcore.util.Range;
import com.deltarobotics9351.deltadrive.utils.Axis;

/**
 * Class containing public variables for setting IMU rotation parameters
 */
public class IMUDriveParameters {

    /**
     * The maximum times a rotation can be corrected.
     * It is not used in IMU PID classes
     */
    public int ROTATE_MAX_CORRECTION_TIMES = 0;

    /**
     * The power in which the rotations will be corrected
     * It is not used in IMU PID classes
     */
    public double ROTATE_CORRECTION_POWER = 0;

    /**
     * The axis we'll use for the robot rotation. (Z by default)
     * If the expansion hub 1 is mounted horizontally, flat on the robot chassis, you'd use the Z axis.
     * If the expansion hub 1 is mounted vertically, in a wall or something, you'd use the Y axis.
     */
    public Axis IMU_AXIS = Axis.Z;

    /**
     *  If you changed the IMU name in the robot config, you should update this variable.
     */
    public String IMU_HARDWARE_NAME = "imu";

    /**
     * Wheel motor power in which the robot doesn't move
     * Only for IMU PID
     */
    public double DEAD_ZONE = 0;

    /**
     * Invert the rotation direction
     */
    public boolean INVERT_ROTATION = false;

    /**
     * Amount of error which is considered "acceptable"
     * Only for IMU PID
     */
    public double ERROR_TOLERANCE = 0;

    /**
     * Amount of velocity (delta between current error and last error) whis is considered "acceptable"
     * Only for IMU PID
     */
    public double VELOCITY_TOLERANCE = 0;

    public IMUDriveParameters(){ }

    /**
     * Make sure parameters are in a correct range (0 to 1 and/or positive and/or not null)
     */
    public void secureParameters(){
        ROTATE_MAX_CORRECTION_TIMES = Math.abs(ROTATE_MAX_CORRECTION_TIMES);
        ROTATE_CORRECTION_POWER = MathUtil.clamp(Math.abs(ROTATE_CORRECTION_POWER), 0, 1);

        IMU_HARDWARE_NAME = (IMU_HARDWARE_NAME == null) ? "imu" : IMU_HARDWARE_NAME;

        IMU_AXIS = (IMU_AXIS == null) ? Axis.Z : IMU_AXIS; //set the value to something if it is null, just in case...

        DEAD_ZONE = MathUtil.clamp(Math.abs(DEAD_ZONE), 0, 1);

        ERROR_TOLERANCE = Math.abs(ERROR_TOLERANCE);
        VELOCITY_TOLERANCE = Math.abs(VELOCITY_TOLERANCE);
    }

    /**
     * Checks if any value is 0.
     * @return boolean depending if all values are not 0
     */
    public boolean haveBeenDefined(){
        if(ROTATE_MAX_CORRECTION_TIMES == 0 || ROTATE_CORRECTION_POWER == 0 ){
            return false;
        }
        return true;
    }

}
