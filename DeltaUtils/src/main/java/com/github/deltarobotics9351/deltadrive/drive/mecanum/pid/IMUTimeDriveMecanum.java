package com.github.deltarobotics9351.deltadrive.drive.mecanum.pid;

import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.pid.PIDController;
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

    PIDController pidRotate, pidStrafe, pidDrive;

    public IMUTimeDriveMecanum(DeltaHardware hdw, Telemetry telemetry, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.telemetry = telemetry;
        this.currentOpMode = currentOpMode;
    }

    public void initPIDRotate(double p, double i, double d){
        if(pidRotate == null) {
            pidRotate = new PIDController(p, i, d);
            pidRotate.disable();
        }
    }

    public void setPIDRotate(double p, double i, double d){
        pidRotate.setPID(p, i, d);
    }

    public void initPIDStrafe(double p, double i, double d){
        if(pidStrafe == null) {
            pidStrafe = new PIDController(p, i, d);
            pidStrafe.disable();
        }
    }

    public void setPIDStrafe(double p, double i, double d){
        pidStrafe.setPID(p, i, d);
    }

    public void initPIDDrive(double p, double i, double d){
        if(pidDrive == null) {
            pidDrive = new PIDController(p, i, d);
            pidDrive.disable();
        }
    }

    public void setPIDDrive(double p, double i, double d){
        pidDrive.setPID(p, i, d);
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
        // restart imu angle tracking.
        resetAngle();

        // if degrees > 359 we cap at 359 with same sign as original degrees.
        if (Math.abs(degrees) > 359) degrees = (int) Math.copySign(359, degrees);

        // start pid controller. PID controller will monitor the turn angle with respect to the
        // target angle and reduce power as we approach the target angle. This is to prevent the
        // robots momentum from overshooting the turn after we turn off the power. The PID controller
        // reports onTarget() = true when the difference between turn angle and target angle is within
        // 1% of target (tolerance) which is about 1 degree. This helps prevent overshoot. Overshoot is
        // dependant on the motor and gearing configuration, starting power, weight of the robot and the
        // on target tolerance. If the controller overshoots, it will reverse the sign of the output
        // turning the robot back toward the setpoint value.

        pidRotate.reset();
        pidRotate.setSetpoint(degrees);
        pidRotate.setInputRange(0, degrees);
        pidRotate.setOutputRange(0, power);
        pidRotate.setTolerance(1);
        pidRotate.enable();

        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
        // clockwise (right).

        // rotate until turn is completed.

        if (degrees < 0)
        {
            // On right turn we have to get off zero first.
            while (currentOpMode.opModeIsActive() && getAngle() == 0)
            {
                defineAllWheelPower(power, -power, power, -power);
                sleep(100);
            }

            do
            {
                power = pidRotate.performPID(getAngle()); // power will be - on right turn.
                defineAllWheelPower(power, -power, power, -power);
            } while (currentOpMode.opModeIsActive() && !pidRotate.onTarget());
        }
        else    // left turn.
            do
            {
                power = pidRotate.performPID(getAngle()); // power will be + on left turn.
                defineAllWheelPower(power, -power, power, -power);
            } while (currentOpMode.opModeIsActive() && !pidRotate.onTarget());

        // turn the motors off.
        defineAllWheelPower(0, 0, 0, 0);

        // wait for rotation to stop.
        sleep(500);

        // reset angle tracking on new heading.
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

        pidStrafe.setSetpoint(initialAngle);
        pidStrafe.setInputRange(0, 360);
        pidStrafe.setOutputRange(0, power);
        pidStrafe.reset();
        pidStrafe.enable();

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            double frontleft = power, frontright = -power, backleft = -power, backright = power;

            double error = pidStrafe.getError();

            double correction = pidStrafe.performPID(getAngle());

            frontleft -= correction;
            frontright += correction;
            backleft += correction;
            backright -= correction;

            telemetry.addData("frontleft", frontleft);
            telemetry.addData("frontright", frontright);
            telemetry.addData("backleft", backleft);
            telemetry.addData("backright", backright);
            telemetry.addData("time", timeSecs);
            telemetry.addData("error value", error);
            telemetry.update();

            defineAllWheelPower(frontleft,frontright,backleft,backright);

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

        pidStrafe.setSetpoint(initialAngle);
        pidStrafe.setInputRange(0, 360);
        pidStrafe.setOutputRange(0, power);
        pidStrafe.reset();
        pidStrafe.enable();

        double frontleft = -power, frontright = power, backleft = power, backright = -power;

        defineAllWheelPower(frontleft,frontright,backleft,backright);

        while(System.currentTimeMillis() < finalMillis && currentOpMode.opModeIsActive()){

            double correction = pidStrafe.performPID(getAngle());

            frontleft += correction;
            frontright -= correction;
            backleft -= correction;
            backright += correction;

            telemetry.addData("frontleft", frontleft);
            telemetry.addData("frontright", frontright);
            telemetry.addData("backleft", backleft);
            telemetry.addData("backright", backright);
            telemetry.addData("time", timeSecs);
            telemetry.addData("correction", pidStrafe.getError());
            telemetry.update();

            defineAllWheelPower(frontleft,frontright,backleft,backright);

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

        pidDrive.setSetpoint(initialAngle);
        pidDrive.setInputRange(0, 360);
        pidDrive.setOutputRange(0, power);
        pidDrive.reset();
        pidDrive.enable();

        long finalMillis = System.currentTimeMillis() + (long)(timeSecs*1000);

        double frontleft = power, frontright = power, backleft = power, backright = power;

        defineAllWheelPower(frontleft,frontright,backleft,backright);

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
            telemetry.addData("time", timeSecs);
            telemetry.addData("correction", correction);

            defineAllWheelPower(frontleft,frontright,backleft,backright);

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

        pidDrive.setSetpoint(initialAngle);
        pidDrive.setInputRange(0, 360);
        pidDrive.setOutputRange(0, power);
        pidDrive.reset();
        pidDrive.enable();

        long finalMillis = System.currentTimeMillis() + (long)(timeSecs*1000);

        double frontleft = -power, frontright = -power, backleft = -power, backright = -power;

        defineAllWheelPower(frontleft,frontright,backleft,backright);

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
            telemetry.addData("time", timeSecs);
            telemetry.addData("correction", correction);

            defineAllWheelPower(frontleft,frontright,backleft,backright);

        }

        defineAllWheelPower(0, 0, 0, 0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();

    }

    private void defineAllWheelPower(double frontleft, double frontright, double backleft, double backright){
        hdw.wheelFrontLeft.setPower(-frontleft);
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