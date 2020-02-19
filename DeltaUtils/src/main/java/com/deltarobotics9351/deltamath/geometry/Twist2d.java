package com.deltarobotics9351.deltamath.geometry;

/**
 * Twist2d represents how much the X, Y or theta (rotation) changed in a movement.
 */
public class Twist2d {

    private double[] twi = { 0, 0};
    private Rot2d theta = new Rot2d();

    /**
     * Constructor for Twist2d from x, y and theta values
     * @param x
     * @param y
     * @param theta
     */
    public Twist2d(double x, double y, Rot2d theta){
        twi[0] = x;
        twi[1] = y;
        this.theta = theta;
    }

    /**
     * Constructor for Twist2d
     */
    public Twist2d(){ }

    /**
     * Constructor for Twist2d using another Twist2d
     * @param o
     */
    public Twist2d(Twist2d o){
        twi[0] = o.x();
        twi[1] = o.y();
        theta = o.theta();
    }

    /**
     * @return the X value of this Vec2d
     */
    public double x(){
        return twi[0];
    }

    /**
     * @return the Y value of this Vec2d
     */
    public double y(){
        return twi[1];
    }

    /**
     * @return the theta value of this Vec2d
     */
    public Rot2d theta(){
        return theta;
    }

    @Override
    public String toString(){
        return "Twist2d(" + x() + ", " + y() + ", ";
    }


}
