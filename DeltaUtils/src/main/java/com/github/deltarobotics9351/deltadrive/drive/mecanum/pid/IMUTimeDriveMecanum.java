package com.github.deltarobotics9351.deltadrive.drive.mecanum.pid;

import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.pid.PIDConstants;
import com.github.deltarobotics9351.pid.PIDControl;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class IMUTimeDriveMecanum {

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

    PIDControl pidRotate, pidStrafe, pidDrive;

    public IMUTimeDriveMecanum(DeltaHardware hdw, Telemetry telemetry, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.telemetry = telemetry;
        this.currentOpMode = currentOpMode;
    }

    public void initPIDRotate(PIDConstants pid){
        if(pidRotate == null) {
            pidRotate = new PIDControl(pid);
            pidRotate.disable();
        }
    }

    public void setPIDRotate(PIDConstants pid){
        pidRotate.setPID(pid);
    }

    public void initPIDStrafe(PIDConstants pid){
        if(pidStrafe == null) {
            pidStrafe = new PIDControl(pid);
            pidStrafe.disable();
        }
    }

    public void setPIDStrafe(PIDConstants pid){
        pidStrafe.setPID(pid);
    }

    public void initPIDDrive(PIDConstants pid){
        if(pidDrive == null) {
            pidDrive = new PIDControl(pid);
            pidDrive.disable();
        }
    }

    public void setPIDDrive(PIDConstants pid){
        pidDrive.setPID(pid);
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

    private double getAngle() {

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
        if (Math.abs(degrees) > 359) degrees = (int) Math.copySign(359, degrees);

        pidRotate.defineSetpoint(degrees);
        pidRotate.defineInputRange(0, degrees);
        pidRotate.defineOutputRange(0, power);
        pidRotate.setTolerance(1);
        pidRotate.enable();

        double  backleftpower, backrightpower, frontrightpower, frontleftpower;

        // reiniciamos el IMU y el PID
        resetAngle();
        pidRotate.reset();

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

        if (degrees < 0)
        {
            while (getAngle() == 0 && currentOpMode.opModeIsActive()) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                power = pidRotate.performPID(getAngle());

                backleftpower = power;
                backrightpower = -power;
                frontleftpower = power;
                frontrightpower = -power;

                defineAllWheelPower(frontleftpower,-frontrightpower,-backleftpower,-backrightpower);

                telemetry.addData("imuAngle", getAngle());
                telemetry.addData("toDegrees", degrees);
                telemetry.addData("PID Error", pidRotate.getError());
                telemetry.update();
            }

            while (!pidRotate.onTarget() && currentOpMode.opModeIsActive()) { //entramos en un bucle hasta que los degrees sean los esperados
                power = pidRotate.performPID(getAngle());

                backleftpower = power;
                backrightpower = -power;
                frontleftpower = power;
                frontrightpower = -power;

                defineAllWheelPower(frontleftpower,-frontrightpower,-backleftpower,-backrightpower);

                telemetry.addData("imuAngle", getAngle());
                telemetry.addData("toDegrees", degrees);
                telemetry.addData("PID Error", pidRotate.getError());
                telemetry.update();
            }
        }
        else
            while (!pidRotate.onTarget() && currentOpMode.opModeIsActive()) { //entramos en un bucle hasta que los degrees sean los esperados
                power = pidRotate.performPID(getAngle());

                backleftpower = -power;
                backrightpower = power;
                frontleftpower = -power;
                frontrightpower = power;

                defineAllWheelPower(frontleftpower,-frontrightpower,-backleftpower,-backrightpower);

                telemetry.addData("imuAngle", getAngle());
                telemetry.addData("toDegrees", degrees);
                telemetry.update();
            }

        // paramos los motores
        defineAllWheelPower(0,0,0,0);

        telemetry.addData("final imu angle", getAngle());
        telemetry.update();

        // reiniciamos el IMU otra vez.
        resetAngle();
    }

    private void resetAngle()
    {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }

    public double calculateDeltaAngles(double angle1, double angle2){
        double deltaAngle = angle1 - angle2;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        return deltaAngle;
    }

    public void strafeRight(double power, double timeSecs){

        power = Math.abs(power);

        resetAngle();

        long finalMillis = System.currentTimeMillis() + (long)(timeSecs*1000);

        double initialAngle = getAngle();

        pidStrafe.defineSetpoint(initialAngle);
        pidStrafe.defineInputRange(-90, 90);
        pidStrafe.defineOutputRange(0, power);
        pidStrafe.reset();
        pidStrafe.enable();

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            double frontleft = power, frontright = -power, backleft = -power, backright = power;

            double error = pidStrafe.getError();

            power = pidStrafe.performPID(getAngle());

            frontleft = power;
            frontright = -power;
            backleft = -power;
            backright = power;

            telemetry.addData("frontleft", frontleft);
            telemetry.addData("frontright", frontright);
            telemetry.addData("backleft", backleft);
            telemetry.addData("backright", backright);
            telemetry.addData("error value", error);
            telemetry.update();

            defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        }

        defineAllWheelPower(0,0,0,0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();

    }

    public void strafeLeft(double power, double timeSecs){

        power = Math.abs(power);

        resetAngle();

        long finalMillis = System.currentTimeMillis() + (long)(timeSecs*1000);

        double initialAngle = getAngle();

        pidStrafe.defineSetpoint(initialAngle);
        pidStrafe.defineInputRange(-90, 90);
        pidStrafe.defineOutputRange(0, power);
        pidStrafe.reset();
        pidStrafe.enable();

        double frontleft = -power, frontright = power, backleft = power, backright = -power;

        defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            power = pidStrafe.performPID(getAngle());

            frontleft = -power;
            frontright = power;
            backleft = power;
            backright = -power;

            telemetry.addData("frontleft", frontleft);
            telemetry.addData("frontright", frontright);
            telemetry.addData("backleft", backleft);
            telemetry.addData("backright", backright);
            telemetry.addData("error", pidStrafe.getError());
            telemetry.update();

            defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        }

        defineAllWheelPower(0,0,0,0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();
    }

    public void forward(double power, double timeSecs){
        power = Math.abs(power);

        resetAngle();

        double initialAngle = getAngle();

        pidDrive.defineSetpoint(initialAngle);
        pidDrive.defineInputRange(-90, 90);
        pidDrive.defineOutputRange(0, power);
        pidDrive.reset();
        pidDrive.enable();

        long finalMillis = System.currentTimeMillis() + (long)(timeSecs*1000);

        double frontleft = power, frontright = power, backleft = power, backright = power;

        defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            double correction = pidDrive.performPID(getAngle());

            frontleft -= correction;
            frontright += correction;
            backleft -= correction;
            backright += correction;

            telemetry.addData("frontleft", 0);
            telemetry.addData("frontright", 0);
            telemetry.addData("backleft", 0);
            telemetry.addData("backright", 0);
            telemetry.addData("correction", correction);

            defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        }

        defineAllWheelPower(0, 0, 0, 0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();

    }

    public void backwards(double power, double timeSecs){
        power = Math.abs(power);

        resetAngle();

        double initialAngle = getAngle();

        pidDrive.defineSetpoint(initialAngle);
        pidDrive.defineInputRange(-90, 90);
        pidDrive.defineOutputRange(0, power);
        pidDrive.reset();
        pidDrive.enable();

        long finalMillis = System.currentTimeMillis() + (long)(timeSecs*1000);

        double frontleft = -power, frontright = -power, backleft = -power, backright = -power;

        defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            double correction = pidDrive.performPID(getAngle());

            frontleft -= correction;
            frontright += correction;
            backleft -= correction;
            backright += correction;

            telemetry.addData("frontleft", 0);
            telemetry.addData("frontright", 0);
            telemetry.addData("backleft", 0);
            telemetry.addData("backright", 0);
            telemetry.addData("correction", correction);

            defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        }

        defineAllWheelPower(0, 0, 0, 0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();

    }

    private void defineAllWheelPower(double frontleft, double frontright, double backleft, double backright){
        hdw.wheelFrontLeft.setPower(frontleft);
        hdw.wheelFrontRight.setPower(frontright);
        hdw.wheelBackLeft.setPower(backleft);
        hdw.wheelBackRight.setPower(backright);
    }

    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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