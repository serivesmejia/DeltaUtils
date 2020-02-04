package com.github.deltarobotics9351.deltamath.geometry;

public class Vector2d {

    private double[] vec = { 0, 0 };

    public Vector2d(double x, double y){
        vec[0] = x;
        vec[1] = y;
    }

    public Vector2d(){ }

    public Vector2d(Vector2d o){
        vec[0] = o.x();
        vec[1] = o.y();
    }

    public void setX(double x){
        vec[0] = x;
    }

    public double x(){
        return vec[0];
    }

    public void setY(double y){
        vec[1] = y;
    }

    public double y(){
        return vec[1];
    }

    @Override
    public String toString(){
        return "Vect2d(" + x() + ", " + y();
    }

    public void add(Vector2d o){
        setX(o.x() + x());
        setY(o.y() + y());
    }

    public void subtract(Vector2d o){
        setX(x() - o.x());
        setY(y() - o.y());
    }

    public void divide(double by){
        setX(x() / by);
        setY(y() / by);
    }

    public void rotate(Rotation2D by){
        setX(x() * Math.cos(by.getRadians()) - y() * Math.sin(by.getRadians()));
        setY(x() * Math.sin(by.getRadians()) + y() * Math.cos(by.getRadians()));
    }

    public void invert(){
        setX(-x());
        setY(-y());
    }

    public void multiply(double by){
        setX(x() * by);
        setY(x() * by);
    }

}
