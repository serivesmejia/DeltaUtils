package com.deltarobotics9351.deltasystem;

import com.deltarobotics9351.deltadrive.hardware.DeltaHardware;
import com.deltarobotics9351.deltasystem.subsystems.MecanumChassisSubSystem;

public class SampleDeltaSystemTeleOpMecanum extends DeltaSystemOpMode {

    public MecanumChassisSubSystem mecanumSubsystem = new MecanumChassisSubSystem(hardwareMap, "chassis", telemetry, opModeStatus);

    @Override
    public void _init(){
        mecanumSubsystem.defineChassisMotors("fl", "fr", "bl", "br");
        system.addSubSystem(mecanumSubsystem);
    }

    @Override
    public void _runloop() {
        mecanumSubsystem.driveMecanum.joystick(gamepad1, true, 0.7, 1);
    }

}
