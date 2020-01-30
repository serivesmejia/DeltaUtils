package com.github.deltarobotics9351.deltadrive.drive.mecanum;

import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class JoystickIMUDriveMecanum {


    //wheel motor power
    public double wheelFrontRightPower = 0;
    public double wheelFrontLeftPower = 0;
    public double wheelBackRightPower = 0;
    public double wheelBackLeftPower = 0;

    public double turbo = 0;

    private DeltaHardware hdw;
    private LinearOpMode currentOpMode;

    private BNO055IMU imu;

    private Orientation angles;
    private double headingOffset = 0;

    private double prevStrafe = 0;

    public JoystickIMUDriveMecanum(DeltaHardware hdw, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.currentOpMode = currentOpMode;
    }

    public void initIMU(){

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;

        imu = hdw.hdwMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);
    }

    public void waitForIMUCalibration(){
        while (!imu.isGyroCalibrated() && currentOpMode.opModeIsActive()){ }
    }

    public String getIMUCalibrationStatus(){
        return imu.getCalibrationStatus().toString();
    }

    public boolean isIMUCalibrated(){ return imu.isGyroCalibrated(); }

    public void joystick(Gamepad gamepad, double turbo){

        if(!isIMUCalibrated()) return;

        turbo = Range.clip(Math.abs(turbo), 0, 1);

        this.turbo = turbo;

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double drive = -gamepad.left_stick_y;
        double strafe = gamepad.left_stick_x;
        double turn = gamepad.right_stick_x;

        if(prevStrafe == 0 && strafe > 0) headingOffset = angles.firstAngle;

        currentOpMode.telemetry.addData("headingOffset", headingOffset);
        currentOpMode.telemetry.addData("angle", angles);

        this.turbo = turbo;

        double strafeTemp = drive*Math.sin(Math.toRadians(angles.firstAngle-headingOffset)) + strafe*Math.cos(Math.toRadians(angles.firstAngle-headingOffset));

        strafe = strafeTemp;

        wheelFrontRightPower = (drive - strafe - turn) * turbo;
        wheelBackRightPower = (drive - strafe + turn) * turbo;
        wheelFrontLeftPower = (drive + strafe + turn) * turbo;
        wheelBackLeftPower = (drive + strafe - turn) * turbo;

        double max = Math.max(Math.abs(wheelFrontRightPower), Math.max(Math.abs(wheelBackRightPower),
                Math.max(Math.abs(wheelFrontLeftPower), Math.abs(wheelBackLeftPower))));

        if (max > 1.0)
        {
            wheelFrontRightPower /= max;
            wheelBackRightPower  /= max;
            wheelFrontLeftPower  /= max;
            wheelBackLeftPower   /= max;
        }

        hdw.wheelFrontRight.setPower(wheelFrontRightPower);
        hdw.wheelFrontLeft.setPower(wheelFrontLeftPower);
        hdw.wheelBackRight.setPower(wheelBackRightPower);
        hdw.wheelBackLeft.setPower(wheelBackLeftPower);

        prevStrafe = strafe;

        sleep(100);

    }

    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
