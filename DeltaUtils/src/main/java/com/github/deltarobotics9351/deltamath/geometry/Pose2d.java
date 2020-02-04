package com.github.deltarobotics9351.deltamath.geometry;

public class Pose2d {

    private Vector2d vec;
    private double heading;

    public Pose2d(){
        vec = new Vector2d(0, 0);
        heading = 0;
    }

    public Pose2d(double x, double y, double heading){
        vec = new Vector2d(x, y);
        this.heading = heading;
    }

    public Pose2d(Vector2d vec, double heading){
        this.vec = vec;
        this.heading = heading;
    }

    public Pose2d(Pose2d o){
        vec = o.vec;
        heading = o.heading;
    }

    public Vector2d getPosition(){
        return vec;
    }

    public double getHeading(){
        return heading;
    }

    public void add(Pose2d o){
        vec.add(o.vec);
        heading += o.heading;
    }

    public void divide(double by){
        vec.divide(by);
        heading /= by;
    }

    public void invert(){
        vec.invert();
        heading = -heading;
    }

    public void multiply(double by){
        vec.multiply(by);
        heading *= by;
    }

    public void rotate(double by){
        heading += by;
    }

    @Override
    public String toString(){

        String v = vec.toString();
        String h = String.valueOf(heading);

        return "Pose2d(" + v + ", " + h + ")";
    }

}
