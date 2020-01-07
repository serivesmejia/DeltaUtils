package com.github.deltarobotics9351.deltadrive.drive.mecanum.pid;

import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltadrive.parameters.EncoderDriveConstants;
import com.github.deltarobotics9351.pid.PIDController;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class IMUEncoderDriveMecanum {

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

    public IMUEncoderDriveMecanum(DeltaHardware hdw, Telemetry telemetry, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.telemetry = telemetry;
        this.currentOpMode = currentOpMode;

        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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

        pidRotate.setSetpoint(degrees);
        pidRotate.setInputRange(0, degrees);
        pidRotate.setOutputRange(0, power);
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
        defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

        if (degrees < 0)
        {
            while (getAngle() == 0 && currentOpMode.opModeIsActive()) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                power = pidRotate.performPID(getAngle());

                backleftpower = power;
                backrightpower = -power;
                frontleftpower = power;
                frontrightpower = -power;

                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

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

                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

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

                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

                telemetry.addData("imuAngle", getAngle());
                telemetry.addData("toDegrees", degrees);
                telemetry.update();
            }

        // paramos los motores
        defineAllWheelPower(0,0,0,0);

        telemetry.addData("final angle", getAngle());
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

    public void strafeRight(double power, double inches){

        power = Math.abs(power);

        double COUNTS_PER_INCH = (EncoderDriveConstants.COUNTS_PER_REV * EncoderDriveConstants.DRIVE_GEAR_REDUCTION) /
                (EncoderDriveConstants.WHEEL_DIAMETER_INCHES * 3.1415);

        resetAngle();

        double initialAngle = getAngle();

        pidStrafe.setSetpoint(initialAngle);
        pidStrafe.setInputRange(0, 360);
        pidStrafe.setOutputRange(0, power);
        pidStrafe.setTolerance(1);
        pidStrafe.reset();
        pidStrafe.enable();

        int newFrontLeftTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;

        // Determine new target position, and pass to motor controller
        newFrontLeftTarget = hdw.wheelFrontLeft.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
        newFrontRightTarget = hdw.wheelFrontRight.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);
        newBackLeftTarget = hdw.wheelBackLeft.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);
        newBackRightTarget = hdw.wheelBackRight.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);

        hdw.wheelFrontLeft.setTargetPosition(-newFrontLeftTarget);
        hdw.wheelFrontRight.setTargetPosition(-newFrontRightTarget);
        hdw.wheelBackLeft.setTargetPosition(-newBackLeftTarget);
        hdw.wheelBackRight.setTargetPosition(-newBackRightTarget);

        // Turn On RUN_TO_POSITION
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while((hdw.wheelFrontRight.isBusy() &&
                hdw.wheelFrontLeft.isBusy() &&
                hdw.wheelBackRight.isBusy() &&
                hdw.wheelBackLeft.isBusy()) && currentOpMode.opModeIsActive()){

            double frontleft = power, frontright = power, backleft = power, backright = power;

            double error = pidStrafe.getError();

            power = pidStrafe.performPID(getAngle());

            frontleft = power;
            frontright = power;
            backleft = power;
            backright = power;

            telemetry.addData("[>]", "Running to %7d :%7d : %7d :%7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget);

            telemetry.addData("[>]", "Running at %7d :%7d : %7d :%7d",
                    hdw.wheelFrontLeft.getCurrentPosition(),
                    hdw.wheelFrontRight.getCurrentPosition(),
                    hdw.wheelBackLeft.getCurrentPosition(),
                    hdw.wheelBackRight.getCurrentPosition());
            telemetry.addData("error", pidStrafe.getError());
            telemetry.update();

            defineAllWheelPower(frontleft,frontright,backleft,backright);

        }

        defineAllWheelPower(0,0,0,0);

        telemetry.update();

    }

    public void strafeLeft(double power, double inches){

        power = Math.abs(power);

        double COUNTS_PER_INCH = (EncoderDriveConstants.COUNTS_PER_REV * EncoderDriveConstants.DRIVE_GEAR_REDUCTION) /
                (EncoderDriveConstants.WHEEL_DIAMETER_INCHES * 3.1415);

        resetAngle();

        double initialAngle = getAngle();

        pidStrafe.setSetpoint(initialAngle);
        pidStrafe.setInputRange(0, 360);
        pidStrafe.setOutputRange(0, power);
        pidStrafe.setTolerance(1);
        pidStrafe.reset();
        pidStrafe.enable();

        int newFrontLeftTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;

        // Determine new target position, and pass to motor controller
        newFrontLeftTarget = hdw.wheelFrontLeft.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);
        newFrontRightTarget = hdw.wheelFrontRight.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
        newBackLeftTarget = hdw.wheelBackLeft.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
        newBackRightTarget = hdw.wheelBackRight.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);

        hdw.wheelFrontLeft.setTargetPosition(-newFrontLeftTarget);
        hdw.wheelFrontRight.setTargetPosition(-newFrontRightTarget);
        hdw.wheelBackLeft.setTargetPosition(-newBackLeftTarget);
        hdw.wheelBackRight.setTargetPosition(-newBackRightTarget);

        // Turn On RUN_TO_POSITION
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while((hdw.wheelFrontRight.isBusy() &&
                hdw.wheelFrontLeft.isBusy() &&
                hdw.wheelBackRight.isBusy() &&
                hdw.wheelBackLeft.isBusy()) && currentOpMode.opModeIsActive()){

            double frontleft = power, frontright = power, backleft = power, backright = power;

            power = pidStrafe.performPID(getAngle());

            frontleft = power;
            frontright = power;
            backleft = power;
            backright = power;

            telemetry.addData("[>]", "Running to %7d :%7d : %7d :%7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget);

            telemetry.addData("[>]", "Running at %7d :%7d : %7d :%7d",
                    hdw.wheelFrontLeft.getCurrentPosition(),
                    hdw.wheelFrontRight.getCurrentPosition(),
                    hdw.wheelBackLeft.getCurrentPosition(),
                    hdw.wheelBackRight.getCurrentPosition());
            telemetry.addData("error", pidStrafe.getError());
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

    public void forward(double power, double inches){
        power = Math.abs(power);

        double COUNTS_PER_INCH = (EncoderDriveConstants.COUNTS_PER_REV * EncoderDriveConstants.DRIVE_GEAR_REDUCTION) /
                (EncoderDriveConstants.WHEEL_DIAMETER_INCHES * 3.1415);

        resetAngle();

        double initialAngle = getAngle();

        pidDrive.setSetpoint(initialAngle);
        pidDrive.setInputRange(0, 360);
        pidDrive.setOutputRange(0, power);
        pidDrive.reset();
        pidDrive.enable();

        int newFrontLeftTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;

        // Determine new target position, and pass to motor controller
        newFrontLeftTarget = hdw.wheelFrontLeft.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
        newFrontRightTarget = hdw.wheelFrontRight.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
        newBackLeftTarget = hdw.wheelBackLeft.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
        newBackRightTarget = hdw.wheelBackRight.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);

        hdw.wheelFrontLeft.setTargetPosition(-newFrontLeftTarget);
        hdw.wheelFrontRight.setTargetPosition(-newFrontRightTarget);
        hdw.wheelBackLeft.setTargetPosition(-newBackLeftTarget);
        hdw.wheelBackRight.setTargetPosition(-newBackRightTarget);

        // Turn On RUN_TO_POSITION
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        double frontleft = power, frontright = power, backleft = power, backright = power;

        defineAllWheelPower(frontleft,frontright,backleft,backright);

        while((hdw.wheelFrontRight.isBusy() &&
                hdw.wheelFrontLeft.isBusy() &&
                hdw.wheelBackRight.isBusy() &&
                hdw.wheelBackLeft.isBusy()) && currentOpMode.opModeIsActive()){

            double correction = pidDrive.performPID(getAngle());

            frontleft -= correction;
            frontright += correction;
            backleft -= correction;
            backright += correction;

            telemetry.addData("[>]", "Running to %7d :%7d : %7d :%7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget);

            telemetry.addData("[>]", "Running at %7d :%7d : %7d :%7d",
                    hdw.wheelFrontLeft.getCurrentPosition(),
                    hdw.wheelFrontRight.getCurrentPosition(),
                    hdw.wheelBackLeft.getCurrentPosition(),
                    hdw.wheelBackRight.getCurrentPosition());
            telemetry.addData("error", pidStrafe.getError());
            telemetry.update();

            defineAllWheelPower(frontleft,-frontright,-backleft,-backright);

        }

        defineAllWheelPower(0, 0, 0, 0);

        telemetry.addData("frontleft", 0);
        telemetry.addData("frontright", 0);
        telemetry.addData("backleft", 0);
        telemetry.addData("backright", 0);
        telemetry.update();

    }

    public void backwards(double power, double inches){
        power = Math.abs(power);

        double COUNTS_PER_INCH = (EncoderDriveConstants.COUNTS_PER_REV * EncoderDriveConstants.DRIVE_GEAR_REDUCTION) /
                (EncoderDriveConstants.WHEEL_DIAMETER_INCHES * 3.1415);

        resetAngle();

        double initialAngle = getAngle();

        pidDrive.setSetpoint(initialAngle);
        pidDrive.setInputRange(0, 360);
        pidDrive.setOutputRange(0, power);
        pidDrive.reset();
        pidDrive.enable();

        int newFrontLeftTarget;
        int newFrontRightTarget;
        int newBackLeftTarget;
        int newBackRightTarget;

        // Determine new target position, and pass to motor controller
        newFrontLeftTarget = hdw.wheelFrontLeft.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);
        newFrontRightTarget = hdw.wheelFrontRight.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);
        newBackLeftTarget = hdw.wheelBackLeft.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);
        newBackRightTarget = hdw.wheelBackRight.getCurrentPosition() + (int) (-inches * COUNTS_PER_INCH);

        hdw.wheelFrontLeft.setTargetPosition(-newFrontLeftTarget);
        hdw.wheelFrontRight.setTargetPosition(-newFrontRightTarget);
        hdw.wheelBackLeft.setTargetPosition(-newBackLeftTarget);
        hdw.wheelBackRight.setTargetPosition(-newBackRightTarget);

        // Turn On RUN_TO_POSITION
        hdw.wheelFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hdw.wheelBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        double frontleft = power, frontright = power, backleft = power, backright = power;

        defineAllWheelPower(frontleft,frontright,backleft,backright);

        while((hdw.wheelFrontRight.isBusy() &&
                hdw.wheelFrontLeft.isBusy() &&
                hdw.wheelBackRight.isBusy() &&
                hdw.wheelBackLeft.isBusy()) && currentOpMode.opModeIsActive()){

            double correction = pidDrive.performPID(getAngle());

            frontleft -= correction;
            frontright += correction;
            backleft -= correction;
            backright += correction;

            telemetry.addData("[>]", "Running to %7d :%7d : %7d :%7d",
                    newFrontLeftTarget,
                    newFrontRightTarget,
                    newBackLeftTarget,
                    newBackRightTarget);

            telemetry.addData("[>]", "Running at %7d :%7d : %7d :%7d",
                    hdw.wheelFrontLeft.getCurrentPosition(),
                    hdw.wheelFrontRight.getCurrentPosition(),
                    hdw.wheelBackLeft.getCurrentPosition(),
                    hdw.wheelBackRight.getCurrentPosition());
            telemetry.addData("error", pidStrafe.getError());
            telemetry.update();

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