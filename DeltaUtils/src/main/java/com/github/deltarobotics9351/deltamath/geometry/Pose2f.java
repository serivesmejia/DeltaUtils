package com.github.deltarobotics9351.deltamath.geometry;

public class Pose2f {

    private Vector2f vec;
    private float heading;

    public Pose2f(){
        vec = new Vector2f(0, 0);
        heading = 0;
    }

    public Pose2f(float x, float y, float heading){
        vec = new Vector2f(x, y);
        this.heading = heading;
    }

    public Pose2f(Vector2f vec, float heading){
        this.vec = vec;
        this.heading = heading;
    }

    public Vector2f getPosition(){
        return vec;
    }

    public double getHeading(){
        return heading;
    }

    public void add(Pose2f o){
        vec.add(o.vec);
        heading += o.heading;
    }

    public void divide(float by){
        vec.divide(by);
        heading /= by;
    }

    public void invert(){
        vec.invert();
        heading = -heading;
    }

    public void multiply(float by){
        vec.multiply(by);
        heading *= by;
    }

    public void rotate(float by){
        heading += by;
    }

    @Override
    public String toString(){

        String v = vec.toString();
        String h = String.valueOf(heading);

        return "Pose2f(" + v + ", " + h + ")";
    }

}
