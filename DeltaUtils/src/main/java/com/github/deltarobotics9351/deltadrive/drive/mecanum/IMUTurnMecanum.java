package com.github.deltarobotics9351.deltadrive.drive.mecanum;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltadrive.parameters.IMUDriveConstants;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class IMUTurnMecanum {

    public BNO055IMU imu;
    DeltaHardware hdw;
    Orientation lastAngles = new Orientation();

    DcMotor frontleft;
    DcMotor frontright;
    DcMotor backleft;
    DcMotor backright;

    double globalAngle;

    Telemetry telemetry;

    LinearOpMode currentOpMode;

    public IMUTurnMecanum(DeltaHardware hdw, Telemetry telemetry, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.telemetry = telemetry;
        this.currentOpMode = currentOpMode;
    }

    public void initIMU(){
        frontleft = hdw.wheelFrontLeft;
        frontright = hdw.wheelFrontRight;
        backleft = hdw.wheelBackLeft;
        backright = hdw.wheelBackRight;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        imu = hdw.hdwMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

    }

    public void waitForIMUCalibration(){
        while (!imu.isGyroCalibrated()){ }
    }

    public String getIMUCalibrationStatus(){
        return imu.getCalibrationStatus().toString();
    }

    public boolean isIMUCalibrated(){ return imu.isGyroCalibrated(); }

    private double getAngle()
    {

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    public void rotate(double degrees, double power)
    {
        double  backleftpower, backrightpower, frontrightpower, frontleftpower;

        if (Math.abs(degrees) > 359) degrees = (int) Math.copySign(359, degrees);

        // reiniciamos el IMU
        resetAngle();

        if (degrees < 0) //si es menor que 0 significa que el robot girara a la derecha
        {   // girar a la derecha
            backleftpower = power;
            backrightpower = -power;
            frontleftpower = power;
            frontrightpower = -power;
        }
        else if (degrees > 0) // si es mayor a 0 significa que el robot girara a la izquierda
        {   // girar a la izquierda
            backleftpower = -power;
            backrightpower = power;
            frontleftpower = -power;
            frontrightpower = power;
        }
        else return;

        // definimos el power de los motores
        defineAllWheelPower(frontleftpower,-frontrightpower,-backleftpower,-backrightpower);

        // rotaremos hasta que se complete la vuelta
        if (degrees < 0)
        {
            while (getAngle() == 0 && currentOpMode.opModeIsActive()) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry.addData("imuAngle", getAngle());
                telemetry.addData("degreesDestino", degrees);
                telemetry.update();
            }

            while (getAngle() > degrees && currentOpMode.opModeIsActive()) { //entramos en un bucle hasta que los degrees sean los esperados
                telemetry.addData("imuAngle", getAngle());
                telemetry.addData("degreesDestino", degrees);
                telemetry.update();
            }
        }
        else
            while (getAngle() < degrees && currentOpMode.opModeIsActive()) { //entramos en un bucle hasta que los degrees sean los esperados
                telemetry.addData("imuAngle", getAngle());
                telemetry.addData("degreesDestino", degrees);
                telemetry.update();
            }

        // paramos los motores
        defineAllWheelPower(0,0,0,0);

        // reiniciamos el IMU otra vez.
        resetAngle();
    }

    public void strafeRight(double power, double time){

        long finalMillis = System.currentTimeMillis() + (long)(time*1000);

        double initialAngle = getAngle();

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            double frontleft = power, frontright = -power, backleft = -power, backright = power;

            double deltaAngle = calculateDeltaAngles(initialAngle, getAngle());

            double error = deltaAngle * Range.clip(IMUDriveConstants.STRAFING_COUNTERACT_CONSTANT, 0, 1);

            if(getAngle() < initialAngle){

                frontleft = power;
                frontright = -power * error;
                backleft = -power;
                backright = power * error;

                telemetry.addData("frontleft", frontleft);
                telemetry.addData("frontright", frontright);
                telemetry.addData("backleft", backleft);
                telemetry.addData("backright", backright);
                telemetry.addData("error value", error);
                telemetry.addData("deltaAngle", deltaAngle);
                telemetry.update();

            }else if(getAngle() > initialAngle){

                frontleft = power * error;
                frontright = -power;
                backleft = -power * error;
                backright = power;

                telemetry.addData("frontleft", frontleft);
                telemetry.addData("frontright", frontright);
                telemetry.addData("backleft", backleft);
                telemetry.addData("backright", backright);
                telemetry.addData("error value", error);
                telemetry.addData("deltaAngle", deltaAngle);
                telemetry.update();

            }else{
                frontleft = power;
                frontright = -power;
                backleft = -power;
                backright = power;
                telemetry.addData("frontleft", -frontleft);
                telemetry.addData("frontright", frontright);
                telemetry.addData("backleft", backleft);
                telemetry.addData("backright", backright);
                telemetry.update();
            }

            defineAllWheelPower(frontleft,frontright,backleft,backright);

        }

        defineAllWheelPower(0,0,0,0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();

    }

    public void strafeLeft(double power, double time){

        long finalMillis = System.currentTimeMillis() + (long)(time*1000);

        double initialAngle = getAngle();

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            double frontleft = -power, frontright = power, backleft = power, backright = -power;

            double deltaAngle = calculateDeltaAngles(initialAngle, getAngle());

            double error = deltaAngle * Range.clip(IMUDriveConstants.STRAFING_COUNTERACT_CONSTANT, 0, 1);

            if(getAngle() < initialAngle){

                frontleft = -power;
                frontright = power * error;
                backleft = power;
                backright = -power * error;

                telemetry.addData("frontleft", frontleft);
                telemetry.addData("frontright", frontright);
                telemetry.addData("backleft", backleft);
                telemetry.addData("backright", backright);
                telemetry.addData("error value", error);
                telemetry.addData("deltaAngle", deltaAngle);
                telemetry.update();

            }else if(getAngle() > initialAngle){

                frontleft = -power * error;
                frontright = power;
                backleft = power * error;
                backright = -power;

                telemetry.addData("frontleft", frontleft);
                telemetry.addData("frontright", frontright);
                telemetry.addData("backleft", backleft);
                telemetry.addData("backright", backright);
                telemetry.addData("error value", error);
                telemetry.addData("deltaAngle", deltaAngle);
                telemetry.update();

            }else{
                frontleft = power;
                frontright = -power;
                backleft = -power;
                backright = power;
                telemetry.addData("frontleft", frontleft);
                telemetry.addData("frontright", frontright);
                telemetry.addData("backleft", backleft);
                telemetry.addData("backright", backright);
                telemetry.update();
            }

            defineAllWheelPower(frontleft,frontright,backleft,backright);

        }

        defineAllWheelPower(0,0,0,0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();

    }

    private void resetAngle()
    {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }

    private double calculateDeltaAngles(double angle1, double angle2){
        double deltaAngle = angle1 - angle2;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return deltaAngle;
    }

    private void defineAllWheelPower(double frontleft, double frontright, double backleft, double backright){
        hdw.wheelFrontLeft.setPower(frontleft);
        hdw.wheelFrontRight.setPower(-frontright);
        hdw.wheelBackLeft.setPower(-backleft);
        hdw.wheelBackRight.setPower(-backright);
    }

    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void correctRotation(double expectedAngle){

        double deltaAngle = calculateDeltaAngles(getAngle(), expectedAngle);

        rotate(deltaAngle, 0.3);

    }

    //esta funcion sirve para esperar que el robot este totalmente estatico.
    private void waitForTurnToFinish(){

        double beforeAngle = getAngle();
        double deltaAngle = 0;

        sleep(500);

        deltaAngle = getAngle() - beforeAngle;

        telemetry.addData("currentAngle", getAngle());
        telemetry.addData("beforeAngle", beforeAngle);
        telemetry.addData("deltaAngle", deltaAngle);
        telemetry.update();

        while(deltaAngle != 0){

            telemetry.addData("currentAngle", getAngle());
            telemetry.addData("beforeAngle", beforeAngle);
            telemetry.addData("deltaAngle", deltaAngle);
            telemetry.update();

            deltaAngle = getAngle() - beforeAngle;

            beforeAngle = getAngle();

            sleep(500);

        }

    }



}