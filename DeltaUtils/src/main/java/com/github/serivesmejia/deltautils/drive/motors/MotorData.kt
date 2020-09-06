package com.github.serivesmejia.deltautils.drive.motors

import com.github.serivesmejia.deltautils.drive.utils.gear.TwoGearRatio

interface MotorData {

    val TICKS_PER_REVOLUTION: Double
    val NO_LOAD_RPM: Double
    val GEAR_RATIO: TwoGearRatio

}