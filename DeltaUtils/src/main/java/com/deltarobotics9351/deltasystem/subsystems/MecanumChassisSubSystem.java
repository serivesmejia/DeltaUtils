package com.deltarobotics9351.deltasystem.subsystems;

import com.deltarobotics9351.deltadrive.DeltaDriveMecanum;
import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltadrive.utils.ChassisType;
import com.deltarobotics9351.deltasystem.DeltaOpMode;
import com.deltarobotics9351.deltasystem.utils.HardwareType;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumChassisSubSystem extends MotionSubSystem {

    public MecanumChassisSubSystem(HardwareMap hdwMap, String name, Telemetry telemetry, DeltaOpMode currentOpMode){
        super(hdwMap, name, telemetry, currentOpMode);
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
    public void _init()


}
