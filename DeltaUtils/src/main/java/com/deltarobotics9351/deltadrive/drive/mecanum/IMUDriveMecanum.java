package com.deltarobotics9351.deltadrive.drive.mecanum;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.deltarobotics9351.deltadrive.drive.mecanum.hardware.DeltaHardwareMecanum;
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;

import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Class to use the IMU sensor integrated in the Rev Expansion Hub to make precise turns
 *
*/
public class IMUDriveMecanum {

    public BNO055IMU imu;
    DeltaHardwareMecanum hdw;
    DcMotor frontleft;
    DcMotor frontright;
    DcMotor backleft;
    DcMotor backright;

    Telemetry telemetry;

    Orientation             lastAngles = new Orientation();
    double                  globalAngle;

    IMUDriveParameters parameters;

    private ElapsedTime runtime = new ElapsedTime();

    /**
     * Constructor for the IMU drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     * @param hdw The initialized hardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    public IMUDriveMecanum(DeltaHardwareMecanum hdw, Telemetry telemetry){
        this.hdw = hdw;
        this.telemetry = telemetry;
    }

    /**
     * Initialize the IMU sensor and set the parameters
     * (Remember to wait for the imu calibration [waitForIMUCalibration()] before the OpMode starts!)
     * @param parameters Object containing the parameters for IMU Turns
     */
    public void initIMU(IMUDriveParameters parameters){
        this.parameters = parameters;

        parameters.secureParameters();

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
     * Loop until the IMU sensor reports it is calibrated or until OpMode stops.
     */
    public void waitForIMUCalibration(){
        while (!imu.isGyroCalibrated() && !Thread.interrupted()){ }
    }

    /**
     * Get the IMU calibration status as an String.
     * @return the String containing the sensor calibration status.
     */
    public String getIMUCalibrationStatus(){
        return imu.getCalibrationStatus().toString();
    }

    /**
     * @return boolean depending if IMU sensor is calibrated.
     */
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

    /**
     * Get the current robot angle as a Rot2d
     * WARNING: It resets back to 0 after every turn.
     * @return Rot2d with current the Robot angle.
     */
    public Rot2d getRobotAngle(){
        return Rot2d.fromDegrees(getAngle());
    }

    int correctedTimes = 0;

    /**
     * Rotate to a specific angle, with error correction
     * @param rotation
     * @param power Speed to rotate
     * @param timeoutS Max time (in seconds) that the rotation may take, set to 0 for infinite time.
     */
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

        power = Math.abs(power);

        if(!isIMUCalibrated()) return;

        double  backleftpower, backrightpower, frontrightpower, frontleftpower;

        parameters.secureParameters();

        if (degrees < 0) //si es menor que el angulo actual significa que el robot girara a la derecha
        {   // girar a la derecha
            backleftpower = power;
            backrightpower = -power;
            frontleftpower = power;
            frontrightpower = -power;
        }

        else if (degrees > 0) // si es mayor que el angulo actual significa que el robot girara a la izquierda
        {   // girar a la izquierda
            backleftpower = -power;
            backrightpower = power;
            frontleftpower = -power;
            frontrightpower = power;
        }
        else return;

        // definimos el power de los motores
        defineAllWheelPower(frontleftpower,frontrightpower,backleftpower,backrightpower);

        // rotaremos hasta que se complete la vuelta
        if (degrees < 0)
        {
            while (getAngle() == 0 && !Thread.interrupted() && (runtime.seconds() < timeoutS)) { //al girar a la derecha necesitamos salirnos de 0 grados primero
                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.update();
            }

            while (getAngle() > degrees && !Thread.interrupted() && (runtime.seconds() < timeoutS)) { //entramos en un bucle hasta que los degrees sean los esperados
                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.update();
            }
        }
        else
            while (getAngle() < degrees && !Thread.interrupted() && (runtime.seconds() < timeoutS)) { //entramos en un bucle hasta que los degrees sean los esperados
                telemetry.addData("IMU Angle", getAngle());
                telemetry.addData("Targeted degrees", degrees);
                telemetry.update();
            }

        // stop the movement
        defineAllWheelPower(0,0,0,0);

        correctRotation(degrees);
    }

    private void correctRotation(double expectedAngle){

        correctedTimes += 1;

        if(correctedTimes > parameters.ROTATE_MAX_CORRECTION_TIMES) {
            correctedTimes = 0;
            return;
        }

        double deltaAngle = calculateDeltaAngles(expectedAngle, getAngle());

        telemetry.addData("error", deltaAngle);
        telemetry.update();

        rotate(Rot2d.fromDegrees(deltaAngle), parameters.ROTATE_CORRECTION_POWER, 0);

    }

    /**
     * Get the difference (delta) between two angles (in degrees)
     * @param angle1 The angle to be subtracted by the angle2
     * @param angle2 The angle to be subtracted to angle1
     * @return The result of the angles difference, considering the -360 to 360 range.
     */
    public static double calculateDeltaAngles(double angle1, double angle2){
        double deltaAngle = angle1 - angle2;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

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