package com.github.deltarobotics9351.deltasystem;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class DeltaOpMode extends LinearOpMode {

    public System system = new System(hardwareMap, telemetry, this);

    @Override
    public final void runOpMode(){
        _init();

        while(!opModeIsActive()) _initloop();
        system.initSubSystems();

        _run();

        while(opModeIsActive()){
            system.stepSubSystems();
            _runloop();
        }

        _stop();

        system.requestStopSubSystems();
    }

    public void _init(){ }

    public void _initloop(){ }

    public void _run(){ }

    public void _runloop(){ }

    public void _stop(){ }

}
