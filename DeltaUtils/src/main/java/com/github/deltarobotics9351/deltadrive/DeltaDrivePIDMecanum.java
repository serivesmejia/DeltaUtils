package com.github.deltarobotics9351.deltadrive;

import com.github.deltarobotics9351.deltadrive.drive.mecanum.EncoderDriveMecanum;
import com.github.deltarobotics9351.deltadrive.drive.mecanum.JoystickDriveMecanum;
import com.github.deltarobotics9351.deltadrive.drive.mecanum.TimeDriveMecanum;
import com.github.deltarobotics9351.deltadrive.drive.mecanum.pid.IMUEncoderDriveMecanum;
import com.github.deltarobotics9351.deltadrive.drive.mecanum.pid.IMUTimeDriveMecanum;
import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltadrive.utils.ChassisType;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DeltaDrivePIDMecanum {

    private DeltaHardware hdw;
    private Telemetry telemetry;

    public EncoderDriveMecanum encoderDrive;

    public IMUTimeDriveMecanum imuTimeDrive;
    public IMUEncoderDriveMecanum imuEncoderDrive;

    private JoystickDriveMecanum joystickDrive;
    public TimeDriveMecanum timeDrive;

    public LinearOpMode currentOpMode;

    boolean initalized = false;

    public DeltaDrivePIDMecanum(DeltaHardware hdw, Telemetry telemetry, LinearOpMode currentOpMode){
        this.hdw = hdw;
        this.telemetry = telemetry;
        this.currentOpMode = currentOpMode;
    }

    public void initialize(boolean waitForIMUCalibration){

        if(hdw.chassisType != ChassisType.mecanum){
            return;
        }

        encoderDrive = new EncoderDriveMecanum(hdw, telemetry);
        imuTimeDrive = new IMUTimeDriveMecanum(hdw, telemetry, currentOpMode);
        imuEncoderDrive = new IMUEncoderDriveMecanum(hdw, telemetry, currentOpMode);
        joystickDrive = new JoystickDriveMecanum(hdw);
        timeDrive = new TimeDriveMecanum(hdw, telemetry);

        imuTimeDrive.initIMU();
        imuEncoderDrive.initIMU();

        if(waitForIMUCalibration) {
            telemetry.addData("Status", "Waiting for IMU calibration...");
            telemetry.update();

            imuTimeDrive.waitForIMUCalibration();

            telemetry.update();
        }
    }

    public void setPIDDrive(double p, double i, double d){
        imuTimeDrive.initPIDDrive(p, i, d);
    }

    public void setPIDStrafe(double p, double i, double d){
        imuEncoderDrive.initPIDStrafe(p, i, d);
        imuTimeDrive.initPIDStrafe(p, i, d);
    }

    public void setPIDRotate(double p, double i, double d){
        imuEncoderDrive.initPIDRotate(p, i, d);
        imuTimeDrive.initPIDRotate(p, i, d);
    }

    public void joystick(Gamepad gamepad, boolean controlSpeedWithTriggers, double maxMinusSpeed, double maxSpeed){
        if(controlSpeedWithTriggers){
            if (gamepad.left_trigger > 0.1) {
                joystickDrive.joystick(gamepad,  maxSpeed - Range.clip(gamepad.left_trigger, 0, maxMinusSpeed));
            }else if(gamepad.right_trigger > 0.1){
                joystickDrive.joystick(gamepad, maxSpeed -  Range.clip(gamepad.right_trigger, 0, maxMinusSpeed));
            } else {
                joystickDrive.joystick(gamepad, maxSpeed);
            }
        }else{
            joystickDrive.joystick(gamepad, maxSpeed);
        }
    }

}
