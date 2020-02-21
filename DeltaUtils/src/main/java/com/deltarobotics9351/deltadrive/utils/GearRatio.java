/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.utils;

/**
 * Class representing a two gear ratio
 */
public class GearRatio {

    private double[] ratio = {0, 0};

    /**
     * Constructor for GearRatio class
     * @param T2 Number of teeth of gear 2
     * @param T1 Number of teeth of gear 1
     */
    public GearRatio(double T2, double T1){
        ratio[0] = T2;
        ratio[1] = T1;
    }

    /**
     * @return Get ratio resutl as decimal
     */
    public double getRatioAsDecimal(){
        return ratio[0] / ratio[1];
    }

    /**
     * @return Gear ratio result as a percentage
     */
    public double getRatioAsPercentage() {
        return (ratio[0] / ratio[1]) * 100;
    }

    /**
     * @return Number of teeth of gear 2
     */
    public double getT2(){ return ratio[0]; }

    /**
     * @return Number of teeth of gear 1
     */
    public double getT1(){ return ratio[1]; }

    @Override
    public String toString(){
        return String.valueOf(getT2()) + ":" + String.valueOf(getT1());
    }

}
