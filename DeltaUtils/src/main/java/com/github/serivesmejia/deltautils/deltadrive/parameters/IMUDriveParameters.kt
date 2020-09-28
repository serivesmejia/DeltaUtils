/*
 * Copyright (c) 2020 FTC Delta Robotics #9351 - Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.serivesmejia.deltautils.deltadrive.parameters

import com.github.serivesmejia.deltautils.deltadrive.utils.Axis
import com.github.serivesmejia.deltautils.deltamath.DeltaMathUtil.clamp
import kotlin.math.abs

/**
 * Class containing public variables for setting IMU rotation parameters
 */
class IMUDriveParameters {

    /**
     * The maximum times a rotation can be corrected.
     * It is not used in IMU PID classes
     */
    var ROTATE_MAX_CORRECTION_TIMES = 0

    /**
     * The power in which the rotations will be corrected
     * It is not used in IMU PID classes
     */
    var ROTATE_CORRECTION_POWER = 0.0

    /**
     * The axis we'll use for the robot rotation. (Z by default)
     * If the expansion hub 1 is mounted horizontally, flat on the robot chassis, you'd use the Z axis.
     * If the expansion hub 1 is mounted vertically, in a wall or something, you'd use the Y axis.
     */
    var IMU_AXIS: Axis = Axis.Z

    /**
     * If you changed the IMU name in the robot config, you should update this variable.
     */
    var IMU_HARDWARE_NAME: String = "imu"

    /**
     * Wheel motor power in which the robot doesn't move
     * Only for IMU PID
     */
    var DEAD_ZONE = 0.0

    /**
     * Invert the rotation direction
     */
    var INVERT_ROTATION = false

    /**
     * Amount of error which is considered "acceptable"
     * Only for IMU PID
     */
    var ERROR_TOLERANCE = 0.0

    /**
     * Make sure parameters are in a correct range (0 to 1 and/or positive and/or not null)
     */
    fun secureParameters() {
        ROTATE_MAX_CORRECTION_TIMES = abs(ROTATE_MAX_CORRECTION_TIMES)
        ROTATE_CORRECTION_POWER = clamp(abs(ROTATE_CORRECTION_POWER), 0.0, 1.0)
        IMU_HARDWARE_NAME = if (IMU_HARDWARE_NAME == null) "imu" else IMU_HARDWARE_NAME
        IMU_AXIS = if (IMU_AXIS == null) Axis.Z else IMU_AXIS //set the value to something if it is null, just in case...
        DEAD_ZONE = clamp(abs(DEAD_ZONE), 0.0, 1.0)
        ERROR_TOLERANCE = abs(ERROR_TOLERANCE)
    }

    /**
     * Checks if any value is 0.
     * @return boolean depending if all values are not 0
     */
    fun haveBeenDefined(): Boolean {
        return !(ROTATE_MAX_CORRECTION_TIMES == 0 || ROTATE_CORRECTION_POWER == 0.0)
    }

}