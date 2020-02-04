package com.github.deltarobotics9351.deltamath.geometry;

public class Rotation2D {

    private double radians;
    private double cos;
    private double sin;

    public Rotation2D(){
        radians = 0;
        cos = 1;
        sin = 0;
    }

    public Rotation2D(double x, double y){
        double hy = Math.hypot(x, y);
        if(hy > 0.00001){
            sin = y / hy;
            cos = x / hy;
        }else{
            sin = 0;
            cos = 1;
        }
    }

    public Rotation2D(double radians){
        this.radians = radians;
        this.cos = Math.cos(radians);
        this.sin = Math.sin(radians);
    }

    public void setDegrees(double degrees){
        this.radians = Math.toRadians(degrees);
        this.cos = Math.cos(radians);
        this.sin = Math.sin(radians);
    }

    public void setRadians(double radians){
        this.radians = radians;
        this.cos = Math.cos(radians);
        this.sin = Math.sin(radians);
    }

    public double toDegrees(){
        return Math.toDegrees(radians);
    }

    public double deltaRadians(Rotation2D other){
        double deltaAngle = toDegrees() - other.toDegrees();

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return Math.toRadians(deltaAngle);
    }

    public double deltaDegrees(Rotation2D other){
        double deltaAngle = toDegrees() - other.toDegrees();

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return deltaAngle;
    }

    public double calculateTan(){
        return sin / cos;
    }

    public double getSin(){
        return sin;
    }

    public double getCos(){
        return cos;
    }

    public double getRadians(){
        return radians;
    }

    public void rotate(Rotation2D o){
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
    }

    @Override
    public String toString(){
        return "Rot2d(rad " + radians + ", deg" + Math.toDegrees(radians);
    }

}
