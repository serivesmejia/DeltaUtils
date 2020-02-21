/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.motors.revrobotics;

import com.deltarobotics9351.deltadrive.utils.GearRatio;

public class CoreHex {
    public static final double TICKS_PER_REVOLUTION = 288;
    public static final double NO_LOAD_RPM = 125;
    public static final GearRatio GEAR_RATIO = new GearRatio(1, 72);
}
