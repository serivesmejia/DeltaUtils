package com.deltarobotics9351.deltadrive.motors

import com.deltarobotics9351.deltadrive.utils.gear.TwoGearRatio

interface MotorData {

    val TICKS_PER_REVOLUTION: Double
    val NO_LOAD_RPM: Double
    val GEAR_RATIO: TwoGearRatio

}