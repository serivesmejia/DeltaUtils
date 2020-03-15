/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.utils;

/**
 * Enum that indicates which side of the chassis will have its motors inverted
 */
public enum Invert {
    LEFT_SIDE, RIGHT_SIDE, BOTH_SIDES,
    MIDDLE, LEFT_SIDE_MIDDLE, RIGHT_SIDE_MIDDLE, BOTH_SIDES_MIDDLE,
    NO_INVERT
}