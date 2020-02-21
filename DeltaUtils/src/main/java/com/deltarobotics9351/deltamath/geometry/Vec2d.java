/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltamath.geometry;

//Class for Vector math
public class Vec2d {

    private double[] vec = { 0, 0 };

    /**
     * Constructor for Vec2d from x and y values
     * @param x
     * @param y
     */
    public Vec2d(double x, double y){
        vec[0] = x;
        vec[1] = y;
    }

    /**
     * Constructor for Vec2d
     */
    public Vec2d(){ }

    /**
     * Constructor for Vec2d using another Vec2d
     * @param o
     */
    public Vec2d(Vec2d o){
        vec[0] = o.x();
        vec[1] = o.y();
    }

    /**
     * @param x The X value to set to this Vec2d
     */
    public void setX(double x){
        vec[0] = x;
    }

    /**
     * @return the X value of this Vec2d
     */
    public double x(){
        return vec[0];
    }

    /**
     * @param y the Y value to set to this Vec2d
     */
    public void setY(double y){
        vec[1] = y;
    }

    /**
     * @return the Y value of this Vec2d
     */
    public double y(){
        return vec[1];
    }

    @Override
    public String toString(){
        return "Vect2d(" + x() + ", " + y();
    }

    /**
     * @return the magnitude of the vector
     */
    public double mag(){
        return Math.hypot(x(), y());
    }

    /**
     * Adds another Vec2d to this Vec2d
     * @param o the Vector to subtract to this vector
     */
    public void add(Vec2d o){
        setX(o.x() + x());
        setY(o.y() + y());
    }

    /**
     * Subtracts another Vec2d to this Vec2d
     * @param o the Vector to subtract to this vector
     */
    public void subtract(Vec2d o){
        setX(x() - o.x());
        setY(y() - o.y());
    }

    /**
     * Divide this Vec2d's X and Y by a value
     * @param by the value to divide by
     */
    public void divide(double by){
        setX(x() / by);
        setY(y() / by);
    }

    /**
     * Rotate this Vec2d by a Rot2d
     * @param by the Rot2d to rotate by
     */
    public void rotate(Rot2d by){
        setX(x() * Math.cos(by.getRadians()) - y() * Math.sin(by.getRadians()));
        setY(x() * Math.sin(by.getRadians()) + y() * Math.cos(by.getRadians()));
    }

    /**
     * Inverts current Vec2d values to negative/positive
     */
    public void invert(){
        setX(-x());
        setY(-y());
    }

    /**
     * Multiply this Vec2d's X and Y by a value
     * @param by value to multiply by
     */
    public void multiply(double by){
        setX(x() * by);
        setY(x() * by);
    }

}
