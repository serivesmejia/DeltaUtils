package com.deltarobotics9351.deltadrive.drive.mecanum;

import com.deltarobotics9351.deltadrive.drive.mecanum.hardware.DeltaHardwareMecanum;
import com.deltarobotics9351.deltamath.MathUtil;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/**
 * Turning using the IMU sensor integrated in the Expansion Hub and a P loop, which slows the motors speed the more closer the robot is to the target.
 */
public class IMUDrivePMecanum {

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

    private ElapsedTime runtime = new ElapsedTime();

    private double P;
    private double deadZone = 0;

    public IMUDrivePMecanum(DeltaHardwareMecanum hdw, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.telemetry = currentOpMode.telemetry;
        this.currentOpMode = currentOpMode;
    }

    public void initIMU(){

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

    /**
     * Dead zone is the minimum motor "power" value in which the robot has motion, in order to avoid it getting stuck during P loop.
     * @param deadZone the dead zone said above
     */
    public void setDeadZone(double deadZone){
        this.deadZone = Math.abs(deadZone);
    }

    public double getDeadZone(){
        return deadZone;
    }

    /**
     * @param P the Proportional coefficient
     */
    public void setP(double P){
        this.P = Math.abs(P);
    }

    public double getP(){
        return P;
    }

    /**
     * Enter in a while loop until the IMU reports it is calibrated or until the opmode stops
     */
    public void waitForIMUCalibration(){
        while (!imu.isGyroCalibrated() && currentOpMode.opModeIsActive()){ }
    }

    /**
     * @return the IMU calibration status as a String
     */
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

    public void rotate(Rot2d rotation, double power, double timeoutS)
    {

        resetAngle();

        double degrees = rotation.getDegrees();

        runtime.reset();

        if(timeoutS == 0){
            timeoutS = 411495121;
        }

        power = Math.abs(power);

        if(!isIMUCalibrated()) return;

        double  backleftpower, backrightpower, frontrightpower, frontleftpower;

        // rotaremos hasta que se complete la vuelta
        if (degrees < 0)
        {
            while (getAngle() == 0  && currentOpMode.opModeIsActive() && (runtime.seconds() < timeoutS)) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.addData("Delta", "Not calculated yet");
                telemetry.addData("Power", power);

                backleftpower = power;
                backrightpower = -power;
                frontleftpower = power;
                frontrightpower = -power;
                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);
            }

            while ((degrees - getAngle()) != 0 && currentOpMode.opModeIsActive() && (runtime.seconds() < timeoutS)) { //entramos en un bucle hasta que los degrees sean los esperados
                double delta = -(degrees + getAngle());

                double div90 = Math.abs(degrees / 90);

                double turbo = MathUtil.clamp(delta * (P / div90), 0, 1);

                double powerF;

                if(turbo > 0){
                    powerF = power * MathUtil.clamp(turbo, deadZone, 1);
                }else{
                    powerF = power * MathUtil.clamp(turbo, -1, deadZone);
                }

                backleftpower = powerF;
                backrightpower = -powerF;
                frontleftpower = powerF;
                frontrightpower = -powerF;

                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.addData("Delta", delta);
                telemetry.addData("Turbo", turbo);
                telemetry.addData("Power", powerF);
                telemetry.update();
            }
        }
        else
            while ((degrees - getAngle()) != 0 && currentOpMode.opModeIsActive() && (runtime.seconds() < timeoutS)) { //entramos en un bucle hasta que los degrees sean los esperados

                double delta = degrees - getAngle();

                double div90 = Math.abs(degrees / 90);

                double turbo = MathUtil.clamp(delta * (P / div90), 0, 1);

                double powerF;

                if(turbo > 0){
                    powerF = power * MathUtil.clamp(turbo, deadZone, 1);
                }else{
                    powerF = power * MathUtil.clamp(turbo, -1, deadZone);
                }

                backleftpower = -powerF;
                backrightpower = powerF;
                frontleftpower = -powerF;
                frontrightpower = powerF;


                defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.addData("Delta", delta);
                telemetry.addData("Turbo", turbo);
                telemetry.addData("Power", powerF);
                telemetry.update();
            }

        // stop the movement
        defineAllWheelPower(0,0,0,0);

        currentOpMode.sleep(100);
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

}