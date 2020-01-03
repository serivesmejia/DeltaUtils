package com.github.deltarobotics9351.deltasystem.subsystems;

import com.github.deltarobotics9351.deltadrive.DeltaDriveMecanum;
import com.github.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.github.deltarobotics9351.deltasystem.utils.HardwareType;

public class MecanumChassisSubSystem extends MotionSubSystem {

    public MecanumChassisSubSystem(String name){
        super(name);
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
    public void __init(){ }

}
