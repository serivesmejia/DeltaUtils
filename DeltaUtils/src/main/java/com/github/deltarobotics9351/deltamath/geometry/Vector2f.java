package com.github.deltarobotics9351.deltamath.geometry;

public class Vector2f {

    private float[] vec = { 0, 0 };

    public Vector2f(float x, float y){
        vec[0] = x;
        vec[1] = y;
    }

    public Vector2f(){ }

    public Vector2f(Vector2f o){
        vec[0] = o.x();
        vec[1] = o.y();
    }

    public void setX(float x){
        vec[0] = x;
    }

    public float x(){
        return vec[0];
    }

    public void setY(float y){
        vec[1] = y;
    }

    public float y(){
        return vec[1];
    }

    @Override
    public String toString(){
        return "Vect2f(" + x() + ", " + y();
    }

    public void add(Vector2f o){
        setX(o.x() + x());
        setY(o.y() + y());
    }

    public void subtract(Vector2f o){
        setX(x() - o.x());
        setY(y() - o.y());
    }

    public void divide(float by){
        setX(x() / by);
        setY(y() / by);
    }

    public void rotate(Rotation2D by){
        setX((float) (x() * Math.cos(by.getRadians()) - y() * Math.sin(by.getRadians())) );
        setY((float) (x() * Math.sin(by.getRadians()) + y() * Math.cos(by.getRadians())) );
    }

    public void invert(){
        setX(-x());
        setY(-y());
    }

    public void multiply(float by){
        setX(x() * by);
        setY(x() * by);
    }
}
