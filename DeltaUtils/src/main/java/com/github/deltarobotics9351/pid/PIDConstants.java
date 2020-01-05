package com.github.deltarobotics9351.pid;

public class PIDConstants {

    public static double P;
    public static double I;
    public static double D;

    public PIDConstants(double P, double I, double D){
        this.P = P;
        this.I = I;
        this.D = D;
    }

    public PIDConstants(){ }

    public static final PIDConstants toObject(){
        return new PIDConstants();
    }

}
