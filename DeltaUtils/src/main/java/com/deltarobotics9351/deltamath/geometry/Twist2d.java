/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltamath.geometry;

/**
 * Twist2d represents how much the X, Y or theta (rotation) changed in a movement.
 */
public class Twist2d {

    private double[] twi = { 0, 0 };
    private Rot2d theta = new Rot2d();

    /**
     * Constructor for Twist2d from x, y and Rot2d theta values
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
     * Constructor for Twist2d from Vec2d and Rot2d theta values
     * @param vector
     * @param theta
     */
    public Twist2d(Vec2d vector, Rot2d theta){
        twi[0] = vector.x();
        twi[1] = vector.y();
        this.theta = theta;
    }


    /**
     * Constructor for Twist2d from x, y and double (radians) theta values
     * @param x
     * @param y
     * @param theta
     */
    public Twist2d(double x, double y, double theta){
        twi[0] = x;
        twi[1] = y;
        this.theta = new Rot2d(theta);
    }

    /**
     * Constructor for Twist2d from Vec2d and double (radians) theta values
     * @param vector
     * @param theta
     */
    public Twist2d(Vec2d vector, double theta){
        twi[0] = vector.x();
        twi[1] = vector.y();
        this.theta = new Rot2d(theta);
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
     * @return the X value of this Twist2d
     */
    public double x(){
        return twi[0];
    }

    /**
     * @return the Y value of this Twist2d
     */
    public double y(){
        return twi[1];
    }

    /**
     * @return the Vec2d value of this Twist2d
     */
    public Vec2d vector(){ return new Vec2d(x(), y()); }

    /**
     * @return the theta value as a Rot2d of this Twist2d
     */
    public Rot2d theta(){
        return theta;
    }

    @Override
    public String toString(){
        return "Twist2d(" + x() + ", " + y() + ", " + theta.getRadians();
    }

}
