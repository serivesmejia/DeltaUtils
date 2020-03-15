/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.pid;

/**
 * Class containing the PID Constants needed by some "drives"
 */
public class PIDCoefficients {

    /**
     * PID Coefficients
     */
    public double kP, kI, kD;

    /**
     * Constructor for PIDCoefficients class
     * @param kP the Proportional coefficient
     * @param kI the Integral coefficient
     * @param kD the Derivative coefficient
     */
    public PIDCoefficients(double kP, double kI, double kD){
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

}
