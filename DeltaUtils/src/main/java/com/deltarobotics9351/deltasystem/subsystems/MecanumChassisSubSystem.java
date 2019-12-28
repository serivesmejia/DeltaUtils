package com.deltarobotics9351.deltasystem.subsystems;

import com.deltarobotics9351.deltadrive.DeltaDriveMecanum;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.utils.ChassisType;
import com.deltarobotics9351.deltadrive.utils.OpModeStatus;
import com.deltarobotics9351.deltasystem.utils.HardwareType;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumChassisSubSystem extends MotionSubSystem {

    public MecanumChassisSubSystem(HardwareMap hdwMap, String name, Telemetry telemetry, OpModeStatus opModeStatus){
        super(hdwMap, name, telemetry, opModeStatus);
    }

    public DeltaDriveMecanum driveMecanum;

    public DeltaHardware deltaHardware;

    String frontleft;
    String frontright;
    String backleft;
    String backright;

    public void defineChassisMotors(String frontleft, String frontright, String backleft, String backright){

        registerHardware(frontleft, HardwareType.DcMotor);
        registerHardware(frontright, HardwareType.DcMotor);
        registerHardware(backleft, HardwareType.DcMotor);
        registerHardware(backright, HardwareType.DcMotor);

        this.frontleft = frontleft;
        this.frontright = frontright;
        this.backleft = backleft;
        this.backright = backright;

    }

    @Override
    public void _init(){

        deltaHardware = new DeltaHardware(hdwMap, getMotor("frontleft"), getMotor("frontright"), getMotor("backleft"), getMotor("backright"), ChassisType.mecanum);
        driveMecanum = new DeltaDriveMecanum(deltaHardware, telemetry, opModeStatus);

        driveMecanum.initialize(true);

    }

}
