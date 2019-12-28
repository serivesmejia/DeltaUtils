package com.deltarobotics9351.deltasystem;

import com.deltarobotics9351.deltadrive.utils.OpModeStatus;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class DeltaSystemOpMode extends LinearOpMode {

    public OpModeStatus opModeStatus = new OpModeStatus(false);

    public System system = new System(hardwareMap);

    @Override
    public final void runOpMode(){
        Thread t = startOpModeStatusThread();

        init();

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

    private Thread startOpModeStatusThread(){
        Thread t;
        t = new Thread(){
            public void run(){
                while(!opModeIsActive()) {
                    opModeStatus.opModeIsActive = false;
                }
                while(opModeIsActive()) {
                    opModeStatus.opModeIsActive = true;
                }
                opModeStatus.opModeIsActive = false;
            }
        };

        t.start();
        return t;
    }

}
