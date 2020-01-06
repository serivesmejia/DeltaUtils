package com.github.deltarobotics9351.deltasystem.subsystems;

import com.github.deltarobotics9351.deltasystem.DeltaOpMode;
import com.github.deltarobotics9351.deltasystem.utils.Hardware;
import com.github.deltarobotics9351.deltasystem.utils.HardwareType;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public class SubSystem {

    public ArrayList<Hardware> hardware = new ArrayList<Hardware>();

    public HardwareMap hdwMap;

    public String name;

    public Telemetry telemetry;

    public DeltaOpMode currentOpMode;

    public SubSystem(String name){
        this.name = name;
    }

    public boolean isStopRequested = false;

    public boolean isInitialized = false;

    public void registerHardware(String name, HardwareType type){
        hardware.add(new Hardware(name, type));
    }

    public final void init(HardwareMap hdwMap, Telemetry telemetry, DeltaOpMode currentOpMode){
        this.hdwMap = hdwMap;
        this.telemetry = telemetry;
        this.currentOpMode = currentOpMode;
        _init();
    }

    public void _init(){}

    public void step(){ }

    public final void requestStop(){
        isStopRequested = true;
    }

}
