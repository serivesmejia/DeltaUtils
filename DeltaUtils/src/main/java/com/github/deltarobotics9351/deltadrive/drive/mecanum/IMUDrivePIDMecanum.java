package com.github.deltarobotics9351.deltadrive.drive.mecanum;

import com.github.deltarobotics9351.deltadrive.drive.mecanum.hardware.DeltaHardwareMecanum;
import com.github.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.github.deltarobotics9351.deltamath.geometry.Rot2d;
import com.github.deltarobotics9351.pid.PIDConstants;
import com.github.deltarobotics9351.pid.PIDController;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class IMUDrivePIDMecanum {

    public BNO055IMU imu;
    DeltaHardwareMecanum hdw;

    DcMotor frontleft;
    DcMotor frontright;
    DcMotor backleft;
    DcMotor backright;

    Orientation lastAngles = new Orientation();
    double globalAngle;

    Telemetry telemetry;

    LinearOpMode currentOpMode;

    PIDConstants pidConst;

    PIDController pidControl;

    private ElapsedTime runtime = new ElapsedTime();

    public IMUDrivePIDMecanum(DeltaHardwareMecanum hdw, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.telemetry = currentOpMode.telemetry;
        this.currentOpMode = currentOpMode;
    }

    public void initIMU(IMUDriveParameters parameters, PIDConstants pidConst){
        this.pidConst = pidConst;

        pidControl = new PIDController(pidConst);

        frontleft = hdw.wheelFrontLeft;
        frontright = hdw.wheelFrontRight;
        backleft = hdw.wheelBackLeft;
        backright = hdw.wheelBackRight;

        BNO055IMU.Parameters param = new BNO055IMU.Parameters();

        param.mode                = BNO055IMU.SensorMode.IMU;
        param.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        param.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        param.loggingEnabled      = false;

        imu = hdw.hdwMap.get(BNO055IMU.class, "imu");

        imu.initialize(param);
    }

    public void setPID(PIDConstants pidConst){
        this.pidConst = pidConst;
        pidControl.setPID(pidConst);
    }

    public PIDConstants getPID(){
        return pidConst;
    }

    public void waitForIMUCalibration(){
        while (!imu.isGyroCalibrated() && currentOpMode.opModeIsActive()){ }
    }

    public String getIMUCalibrationStatus(){
        return imu.getCalibrationStatus().toString();
    }

    public boolean isIMUCalibrated(){ return imu.isGyroCalibrated(); }

    private double getAngle()
    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

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

    private void resetAngle()
    {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }


    public Rot2d getRobotAngle(){
        return Rot2d.fromDegrees(getAngle());
    }

    int correctedTimes = 0;

    public void rotate(Rot2d rotation, double power, double timeoutS)
    {

        resetAngle();

        double degrees = rotation.getDegrees();

        if(correctedTimes == 0) {
            runtime.reset();
            if(runtime.seconds() >= timeoutS){
                correctedTimes = 0;
                return;
            }
        }

        if(timeoutS == 0){
            timeoutS = 411495121;
        }

        pidControl.reset();
        pidControl.setSetpoint(degrees);
        pidControl.setInputRange(0, degrees);
        pidControl.setOutputRange(0, power);
        pidControl.setTolerance(1);
        pidControl.enable();

        power = Math.abs(power);

        if(!isIMUCalibrated()) return;

        double  backleftpower, backrightpower, frontrightpower, frontleftpower;

        // rotaremos hasta que se complete la vuelta
        if (degrees < 0)
        {
            while (getAngle() == 0  && currentOpMode.opModeIsActive() && (runtime.seconds() < timeoutS)) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.addData("PID error", pidControl.getError());
                telemetry.addData("Power", power);

                backleftpower = power;
                backrightpower = -power;
                frontleftpower = power;
                frontrightpower = -power;
                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);
            }

            while (!pidControl.onTarget() && currentOpMode.opModeIsActive() && (runtime.seconds() < timeoutS)) { //entramos en un bucle hasta que los degrees sean los esperados
                power = pidControl.performPID(getAngle()); //el power sera negativo al girar a la derecha

                backleftpower = -power;
                backrightpower = power;
                frontleftpower = -power;
                frontrightpower = power;
                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.addData("PID error", pidControl.getError());
                telemetry.addData("Power", power);
            }
        }
        else
            while (!pidControl.onTarget() && currentOpMode.opModeIsActive() && (runtime.seconds() < timeoutS)) { //entramos en un bucle hasta que los degrees sean los esperados
                power = pidControl.performPID(getAngle()); //el power sera positivo al girar a la izquierda

                backleftpower = -power;
                backrightpower = power;
                frontleftpower = -power;
                frontrightpower = power;
                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);


                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.addData("PID error", pidControl.getError());
                telemetry.addData("Power", power);
                telemetry.update();
            }

        // stop the movement
        defineAllWheelPower(0,0,0,0);
    }


    public static double calculateDeltaAngles(double angle1, double angle2){
        double deltaAngle = angle1 - angle2;

        return deltaAngle;
    }

    private void defineAllWheelPower(double frontleft, double frontright, double backleft, double backright){
        switch(hdw.invert) {
            case RIGHT_SIDE:
                hdw.wheelFrontLeft.setPower(frontleft);
                hdw.wheelFrontRight.setPower(-frontright);
                hdw.wheelBackLeft.setPower(backleft);
                hdw.wheelBackRight.setPower(-backright);
                break;
            case LEFT_SIDE:
                hdw.wheelFrontLeft.setPower(-frontleft);
                hdw.wheelFrontRight.setPower(frontright);
                hdw.wheelBackLeft.setPower(-backleft);
                hdw.wheelBackRight.setPower(backright);
                break;
            case BOTH_SIDES:
                hdw.wheelFrontLeft.setPower(-frontleft);
                hdw.wheelFrontRight.setPower(-frontright);
                hdw.wheelBackLeft.setPower(-backleft);
                hdw.wheelBackRight.setPower(-backright);
                break;
            case NO_INVERT:
                hdw.wheelFrontLeft.setPower(frontleft);
                hdw.wheelFrontRight.setPower(frontright);
                hdw.wheelBackLeft.setPower(backleft);
                hdw.wheelBackRight.setPower(backright);
                break;
        }
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