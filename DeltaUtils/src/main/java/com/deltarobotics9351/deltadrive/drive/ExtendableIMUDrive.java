/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltadrive.drive;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.deltarobotics9351.LibraryData;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.parameters.IMUDriveParameters;

import com.deltarobotics9351.deltamath.MathUtil;
import com.deltarobotics9351.deltamath.geometry.Rot2d;
import com.deltarobotics9351.deltamath.geometry.Twist2d;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Class to use the IMU sensor integrated in the Rev Expansion Hub to make precise turns
 */
public class ExtendableIMUDrive {

    public BNO055IMU imu;
    DeltaHardware hdw;

    Telemetry telemetry;

    Orientation             lastAngles = new Orientation();
    double                  globalAngle;

    IMUDriveParameters parameters;

    private boolean isInitialized = false;

    private ElapsedTime runtime = new ElapsedTime();

    private DeltaHardware.Type allowedDeltaHardwareType = DeltaHardware.Type.DEFAULT;

    /**
     * Constructor for the IMU drive class
     * (Do not forget to call initIMU() before the OpMode starts!)
     * @param hdw The initialized DeltaHardware containing all the chassis motors
     * @param telemetry Current OpMode telemetry to show movement info
     */
    public ExtendableIMUDrive(DeltaHardware hdw, Telemetry telemetry) {
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

    /**
     * Initialize the IMU sensor and set the parameters
     * (Remember to wait for the imu calibration [waitForIMUCalibration()] before the OpMode starts!)
     * @param parameters Object containing the parameters for IMU Turns
     */
    public void initIMU(IMUDriveParameters parameters){
        if(isInitialized) return;

        if (hdw.type != allowedDeltaHardwareType) throw new IllegalArgumentException("Given DeltaHardware in the constructor is not the expected type " + allowedDeltaHardwareType);

        this.parameters = parameters;

        parameters.secureParameters();

        BNO055IMU.Parameters param = new BNO055IMU.Parameters();

        param.mode                = BNO055IMU.SensorMode.IMU;
        param.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        param.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        param.loggingEnabled      = false;

        imu = hdw.hdwMap.get(BNO055IMU.class, parameters.IMU_HARDWARE_NAME);

        imu.initialize(param);

        isInitialized = true;
    }

    /**
     * Loop until the IMU sensor reports it is calibrated or until OpMode stops.
     */
    public void waitForIMUCalibration(){
        while (!imu.isGyroCalibrated() && !Thread.interrupted()) {
            telemetry.addData("[/!\\]", "Calibrating IMU Gyro sensor, please wait...");
            telemetry.addData("[Status]", getIMUCalibrationStatus() + "\n\nDeltaUtils v" + LibraryData.VERSION);
            telemetry.update();
        }
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
     * @return a Twist2d representing how much the robot rotated
     */
    public Twist2d rotate(Rot2d rotation, double power, double timeoutS)
    {

        if(!isInitialized){
            telemetry.addData("[/!\\]", "Call initIMU() method before rotating.");
            telemetry.update();
            sleep(2000);
            return new Twist2d();
        }

        if (!isIMUCalibrated()) return new Twist2d();

        resetAngle();

        double degrees = rotation.getDegrees();

        if(parameters.INVERT_ROTATION) degrees = -degrees;

        if(correctedTimes == 0) {
            runtime.reset();
            if(runtime.seconds() >= timeoutS){
                correctedTimes = 0;
                return new Twist2d();
            }
        }

        if(timeoutS == 0){
            timeoutS = 411495121;
        }

        power = Math.abs(power);

        double  backleftpower, backrightpower, frontrightpower, frontleftpower;

        parameters.secureParameters();

        if (degrees < 0) //si es menor que 0 significa que el robot girara a la derecha
        {   // girar a la derecha
            backleftpower = power;
            backrightpower = -power;
            frontleftpower = power;
            frontrightpower = -power;
        }

        else if (degrees > 0) // si es mayor que 0 significa que el robot girara a la izquierda
        {   // girar a la izquierda
            backleftpower = -power;
            backrightpower = power;
            frontleftpower = -power;
            frontrightpower = power;
        }
        else return new Twist2d();

        // definimos el power de los motores
        setAllMotorPower(frontleftpower, frontrightpower, backleftpower, backrightpower);

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
        hdw.setAllMotorPower(0,0,0,0);

        return correctRotation(degrees);
    }

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

    private Twist2d correctRotation(double expectedAngle){

        correctedTimes += 1;

        if(correctedTimes > parameters.ROTATE_MAX_CORRECTION_TIMES) {
            correctedTimes = 0;
            return new Twist2d(0, 0, Rot2d.fromDegrees(getAngle()));
        }

        double deltaAngle = MathUtil.deltaDegrees(expectedAngle, getAngle());

        telemetry.addData("error", deltaAngle);
        telemetry.update();

        rotate(Rot2d.fromDegrees(deltaAngle), parameters.ROTATE_CORRECTION_POWER, 0);

        return new Twist2d(0, 0, Rot2d.fromDegrees(getAngle()));
    }

    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
