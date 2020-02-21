/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltamath.geometry;

/**
 * Class for rotation math
 */
public class Rot2d {

    private double radians;
    private double cos;
    private double sin;

    /**
     * Constructor for Rot2d
     */
    public Rot2d() {
        radians = 0;
        cos = 1;
        sin = 0;
    }

    /**
     * Constructor for Rot2d using x and y values
     * @param x
     * @param y
     */
    public Rot2d(double x, double y) {
        double hy = Math.hypot(x, y);
        if (hy > 0.00001) {
            sin = y / hy;
            cos = x / hy;
        } else {
            sin = 0;
            cos = 1;
        }
        radians = Math.atan2(sin, cos);
    }

    /**
     * Constructor for Rot2d using radians
     * @param radians
     */
    public Rot2d(double radians) {
        this.radians = radians;
        this.cos = Math.cos(radians);
        this.sin = Math.sin(radians);
    }

    /**
     * Creates a new Rot2d from degrees
     * @param degrees degrees to set to the new Rot2d
     * @return new Rot2d from degrees
     */
    public static final Rot2d fromDegrees(double degrees) {
        return new Rot2d(Math.toRadians(degrees));
    }

    /**
     * Sets the Rot2d radians from degrees and returns a new one
     * @param degrees
     * @return Result Rot2d
     */
    public Rot2d setDegrees(double degrees) {
        this.radians = Math.toRadians(degrees);
        this.cos = Math.cos(radians);
        this.sin = Math.sin(radians);
        return new Rot2d(radians);
    }

    /**
     * Sets the radians and returns a new Rot2d
     * @param radians
     * @return Result Rot2d
     */
    public Rot2d setRadians(double radians) {
        this.radians = radians;
        this.cos = Math.cos(radians);
        this.sin = Math.sin(radians);
        return new Rot2d(radians);
    }

    /**
     * @return the degrees from this Rot2d
     */
    public double getDegrees() {
        return Math.toDegrees(radians);
    }

    /**
     * @param other Other Rot2d
     * @return the difference in radians between this and other Rot2d
     */
    public double deltaRadians(Rot2d other) {
        double deltaAngle = getDegrees() - other.getDegrees();

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return Math.toRadians(deltaAngle);
    }

    /**
     * @param other Other Rot2d
     * @return the difference in degrees between this and other Rot2d
     */
    public double deltaDegrees(Rot2d other) {
        double deltaAngle = getDegrees() - other.getDegrees();

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return deltaAngle;
    }

    /**
     * @return the calculated tan
     */
    public double calculateTan() {
        return sin / cos;
    }

    /**
     * @return the calculated sin
     */
    public double getSin() {
        return sin;
    }

    /**
     * @return the calculated cos
     */
    public double getCos() {
        return cos;
    }

    /**
     * @return the calculated radians
     */
    public double getRadians() {
        return radians;
    }

    /**
     * Rotate by another Rot2d and returns a new one
     * @param o the Rot2d to rotate by
     * @return Result Rot2d
     */
    public Rot2d rotate(Rot2d o){
        double x = cos * o.cos - sin * o.sin;
        double y = cos * o.sin + sin * o.cos;

        double hy = Math.hypot(
                x,
                y);

        if(hy > 0.00001){
            sin = y / hy;
            cos = x / hy;
        }else{
            sin = 0;
            cos = 1;
        }
        radians = Math.atan2(sin, cos);
        return new Rot2d(x, y);
    }

    /**
     * Add another Rot2d and returns a new Rot2d with the result
     * @param o the Rot2d to add by
     * @return Result Rot2d
     */
    public Rot2d add(Rot2d o){
        rotate(o);
        return new Rot2d(radians);
    }

    /**
     * Subtract another Rot2d and returns a new Rot2d with the result
     * @param o the Rot2d to subtract by
     * @return Result Rot2d
     */
    public Rot2d subtract(Rot2d o){
        rotate(o.invert());
        return new Rot2d(radians);
    }

    /**
     * Inverts the radians and returns a new Rot2d
     * @return Result Rot2d
     */
    public Rot2d invert(){
        radians = -radians;
        return new Rot2d(radians);
    }

    @Override
    public String toString(){
        return "Rot2d(rad " + radians + ", deg " + Math.toDegrees(radians) + ")";
    }

}
