/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardwareHolonomic;
import com.deltarobotics9351.deltadrive.parameters.EncoderDriveParameters;
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;
import com.deltarobotics9351.deltamath.MathUtil;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Twist2d;
import com.deltarobotics9351.pid.PIDCoefficients;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Base64;

/**
 * Turning using the IMU sensor integrated in the Expansion Hub and a PID repeat, which slows the motors speed the more closer the robot is to the target.
 */
public class ExtendableIMUDrivePID {

    public BNO055IMU imu;
    DeltaHardware hdw;

    Orientation lastAngles = new Orientation();
    double globalAngle;

    Telemetry telemetry;

    private ElapsedTime runtime = new ElapsedTime();

    private IMUDriveParameters parameters;

    private double rkP = 0;
    private double rkI = 0;
    private double rkD = 0;

    private PIDCoefficients pidCoefficientsRotate = new PIDCoefficients(0, 0, 0);

    private double dkP = 0;
    private double dkI = 0;
    private double dkD = 0;

    private PIDCoefficients pidCoefficientsDrive = new PIDCoefficients(0, 0, 0);

    private boolean isInitialized = false;

    private DeltaHardware.Type allowedDeltaHardwareType = DeltaHardware.Type.DEFAULT;

    private EncoderDriveParameters encoderDriveParameters;

    /**
     * Constructor for the IMU drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    public ExtendableIMUDrivePID(DeltaHardware hdw, Telemetry telemetry) {
        this.hdw = hdw;
        this.telemetry = telemetry;
    }

    boolean alreadySetAllowedDeltaHardwareType = false;

    /**
     * INTERNAL LIBRARY METHOD! Should not be used in your TeamCode
     * @param type ?
     */
    public final void setAllowedDeltaHardwareType(DeltaHardware.Type type){
        if(alreadySetAllowedDeltaHardwareType) return;
        allowedDeltaHardwareType = type;
        alreadySetAllowedDeltaHardwareType = true;
    }

    public void initIMU(IMUDriveParameters parameters) {

        if(!(hdw.type == allowedDeltaHardwareType)) throw new IllegalArgumentException("Given DeltaHardware is not the expected");

        this.parameters = parameters;

        BNO055IMU.Parameters param = new BNO055IMU.Parameters();

        param.mode = BNO055IMU.SensorMode.IMU;
        param.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        param.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        param.loggingEnabled = false;

        imu = hdw.hdwMap.get(BNO055IMU.class, "imu");

        imu.initialize(param);

        isInitialized = true;
    }

    /**
     * @param coefficients the rotate PID coefficients, in a DeltaUtils PIDCoefficients object
     */
    public void setRotatePID(PIDCoefficients coefficients) {
        this.rkP = Math.abs(coefficients.kP);
        this.rkI = Math.abs(coefficients.kI);
        this.rkD = Math.abs(coefficients.kD);
        pidCoefficientsRotate = coefficients;
    }

    public PIDCoefficients getRotatePID(){ return pidCoefficientsRotate; }

    public double getRotateP() {
        return rkP;
    }

    public double getRotateI() {
        return rkI;
    }

    public double getRotateD() {
        return rkD;
    }

    /**
     * @param coefficients the drive (forward & backwards) PID coefficients, in a DeltaUtils PIDCoefficients object
     */
    public void setDrivePID(PIDCoefficients coefficients) {
        this.dkP = Math.abs(coefficients.kP);
        this.dkI = Math.abs(coefficients.kI);
        this.dkD = Math.abs(coefficients.kD);
        pidCoefficientsDrive = coefficients;
    }

    public PIDCoefficients getDrivePID(){ return pidCoefficientsDrive; }

    public double getDriveP() {
        return dkP;
    }

    public double getDriveI() {
        return dkI;
    }

    public double getDriveD() {
        return dkD;
    }

    public void setEncoderDriveParameters(EncoderDriveParameters parameters){ encoderDriveParameters = parameters; }

    /**
     * Enter in a while repeat until the IMU reports it is calibrated or until the opmode stops
     */
    public void waitForIMUCalibration() {
        while (!imu.isGyroCalibrated() && !Thread.interrupted()) {
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", getIMUCalibrationStatus() + "\n\nDeltaUtils v" + LibraryData.VERSION);
            telemetry.update();
        }
    }

    /**
     * @return the IMU calibration status as a String
     */
    public String getIMUCalibrationStatus() {
        return imu.getCalibrationStatus().toString();
    }

    public boolean isIMUCalibrated() {
        return imu.isGyroCalibrated();
    }

    private double getAngle() {
        // We have to process the angle because the imu works in euler angles so the axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = null;

        switch(parameters.IMU_AXIS) {
            case X:
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
                break;
            case Y:
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES);
                break;
            default:
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                break;
        }

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }

    private void resetAngle() {
        switch(parameters.IMU_AXIS) {
            case X:
                lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
                break;
            case Y:
                lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES);
                break;
            default:
                lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                break;
        }
        globalAngle = 0;
    }

    public Rot2d getRobotAngle() {
        return Rot2d.fromDegrees(getAngle());
    }

    /**
     * Rotate by a Rot2d with a PID repeat.
     * @param rotation The Rot2d to rotate by (use Rot2d.fromDegrees() to create a new Rot2d from degrees)
     * @param power The initial power to rotate
     * @param timeoutS The max time the rotation can take, to avoid robot getting stuck.
     * @return Twist2d containing how much the robot rotated
     */
    public Twist2d rotate(Rot2d rotation, double power, double timeoutS) {

        parameters.secureParameters();

        if(!isInitialized){
            telemetry.addData("[/!\\]", "Call initIMU() method before rotating.");
            telemetry.update();
            sleep(2000);
            return new Twist2d();
        }

        if (!isIMUCalibrated()) return new Twist2d();

        resetAngle();
        runtime.reset();

        double setpoint = rotation.getDegrees();
        double deadZone = parameters.DEAD_ZONE;

        if(parameters.INVERT_ROTATION) setpoint = -setpoint;

        if (timeoutS == 0) {
            timeoutS = 411495121;
        }

        power = Math.abs(power);

        double prevErrorDelta = 0;
        double prevMillis = 0;
        double prevIntegral = 0;
        double prevHeading = -1;

        double velocityDelta = parameters.VELOCITY_TOLERANCE + 1;
        double errorDelta = parameters.ERROR_TOLERANCE + 1;

        double backleftpower, backrightpower, frontrightpower, frontleftpower;

        double maxMillis = System.currentTimeMillis() + (timeoutS * 1000);

        boolean firstLoop = true;

        // rotaremos hasta que se complete la vuelta
        if (setpoint < 0) {
            while (getAngle() == 0 && !Thread.interrupted() && (System.currentTimeMillis() < maxMillis)) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Setpoint", setpoint);
                telemetry.addData("Delta", "Not calculated yet");
                telemetry.addData("Power", power);
                telemetry.update();

                backleftpower = power;
                backrightpower = -power;
                frontleftpower = power;
                frontrightpower = -power;

                setAllMotorPower(frontleftpower,frontrightpower,backleftpower,backrightpower);
            }

            while (errorDelta != parameters.ERROR_TOLERANCE && !Thread.interrupted() && (System.currentTimeMillis() < maxMillis)) { //entramos en un bucle hasta que los setpoint sean los esperados

                double nowMillis = System.currentTimeMillis();

                errorDelta = -((-getAngle()) + setpoint);

                velocityDelta = errorDelta - prevErrorDelta;

                double multiplyByDegs = Math.abs(setpoint / 90);

                prevIntegral += errorDelta;

                double proportional = (errorDelta * (rkP * multiplyByDegs));
                double integral = (prevIntegral * (rkI * multiplyByDegs));
                double derivative = (velocityDelta * (rkD * multiplyByDegs));

                double turbo = MathUtil.clamp(proportional + integral + derivative, -1, 1);

                double powerF = power * turbo;

                if (powerF > 0) {
                    powerF = MathUtil.clamp(powerF, deadZone, 1);
                } else if(powerF < 0) {
                    powerF = MathUtil.clamp(powerF, -1, -deadZone);
                }

                backleftpower = powerF;
                backrightpower = -powerF;
                frontleftpower = powerF;
                frontrightpower = -powerF;

                setAllMotorPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Setpoint", setpoint);
                telemetry.addData("Error", errorDelta);
                telemetry.addData("Turbo", turbo);
                telemetry.addData("Power", powerF);
                telemetry.update();

                prevErrorDelta = errorDelta;
                prevMillis = nowMillis;
                prevHeading = getAngle();

                sleep(3);
            }
        } else
            while (errorDelta != parameters.ERROR_TOLERANCE && !Thread.interrupted() && (System.currentTimeMillis() < maxMillis)) { //entramos en un bucle hasta que los setpoint sean los esperados

                double nowMillis = System.currentTimeMillis();

                errorDelta = setpoint - getAngle();

                velocityDelta = errorDelta - prevErrorDelta;

                double multiplyBy = Math.abs(setpoint / 90);

                prevIntegral += errorDelta;

                double proportional = (errorDelta * (rkP * multiplyBy));
                double integral = (prevIntegral * (rkI * multiplyBy));
                double derivative = (velocityDelta * (rkD * multiplyBy));

                double turbo = MathUtil.clamp(proportional + integral + derivative, -1, 1);

                double powerF = power * turbo;

                if (powerF > 0) {
                    powerF = MathUtil.clamp(powerF, deadZone, 1);
                } else if(powerF < 0) {
                    powerF = MathUtil.clamp(powerF, -1, -deadZone);
                }

                backleftpower = -powerF;
                backrightpower = powerF;
                frontleftpower = -powerF;
                frontrightpower = powerF;

                setAllMotorPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Setpoint", setpoint);
                telemetry.addData("Error", errorDelta);
                telemetry.addData("Turbo", turbo);
                telemetry.addData("Power", powerF);
                telemetry.update();

                prevErrorDelta = errorDelta;
                prevMillis = nowMillis;
                prevHeading = getAngle();

                sleep(3);
            }

        // stop the movement
        setAllMotorPower(0,0,0,0);

        sleep(50);
        return new Twist2d(0, 0, Rot2d.fromDegrees(getAngle()));
    }

    public void encoderPIDForward(double inches, double speed, double timeoutS){

        if(encoderDriveParameters == null) return;

        double initialRobotHeading = getAngle();
        encoderPIDDrive(speed, inches, inches, inches, inches, timeoutS, encoderDriveParameters.RIGHT_WHEELS_TURBO, encoderDriveParameters.LEFT_WHEELS_TURBO, "PID Forward", initialRobotHeading, this, encoderDriveParameters, hdw, telemetry);

    }


    public void encoderPIDBackwards(double inches, double speed, double timeoutS){

        if(encoderDriveParameters == null) return;

        speed = Math.abs(speed);

        double initialRobotHeading = getAngle();
        encoderPIDDrive(speed, -inches, -inches, -inches, -inches, timeoutS, encoderDriveParameters.RIGHT_WHEELS_TURBO, encoderDriveParameters.LEFT_WHEELS_TURBO, "PID Backwards", initialRobotHeading, this, encoderDriveParameters, hdw, telemetry);

    }

    public void timePIDForward(double power, double timeSecs){

        power = Math.abs(power);
        double initialRobotHeading = getAngle();

        timePIDDrive(power, power, power, power, timeSecs, initialRobotHeading, this, "PID Forward");

    }

    public void timePIDBackwards(double power, double timeSecs){

        power = Math.abs(power);
        double initialRobotHeading = getAngle();

        timePIDDrive(power, power, power, power, timeSecs, initialRobotHeading, this, "PID Backwards");

    }

    //needs to extend
    public void timePIDDrive(double frontleft, double frontright, double backleft, double backright, double timeSecs, double initialRobotHeading, ExtendableIMUDrivePID imu, String movementDescription) { }

    //needs to extend
    public void encoderPIDDrive(double speed,
                              double frontleft,
                              double frontright,
                              double backleft,
                              double backright,
                              double timeoutS,
                              double rightTurbo,
                              double leftTurbo,
                              String movementDescription,
                              double initialRobotHeading,
                              ExtendableIMUDrivePID imu,
                              EncoderDriveParameters encoderDriveParameters,
                              DeltaHardware hdw,
                              Telemetry telemetry  ) { }

    private void setAllMotorPower(double frontleftpower, double frontrightpower, double backleftpower, double backrightpower) {

        switch(hdw.type) {
            case HOLONOMIC:
                hdw.setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower);
                break;
            case HDRIVE:
                double averageLeft = (frontleftpower + backleftpower) / 2;
                double averageRight = (frontrightpower + backrightpower) / 2;
                hdw.setAllMotorPower(averageLeft, averageRight, 0);

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