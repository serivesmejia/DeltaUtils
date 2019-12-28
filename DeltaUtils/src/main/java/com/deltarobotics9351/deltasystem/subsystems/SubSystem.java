package com.deltarobotics9351.deltasystem.subsystems;

import com.deltarobotics9351.deltadrive.utils.OpModeStatus;
import com.deltarobotics9351.deltasystem.utils.Hardware;
import com.deltarobotics9351.deltasystem.utils.HardwareType;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.HashMap;

public class SubSystem {

    public ArrayList<Hardware> hardware = new ArrayList<Hardware>();

    public HardwareMap hdwMap;

    public String name;

    public Telemetry telemetry;

    public OpModeStatus opModeStatus;

    public SubSystem(HardwareMap hdwMap, String name, Telemetry telemetry, OpModeStatus opModeStatus){
        this.hdwMap = hdwMap; this.name = name;
        this.telemetry = telemetry; this.opModeStatus = opModeStatus;
    }

    public boolean isStopRequested = false;

    public boolean isInitialized = false;

    public void registerHardware(String name, HardwareType type){

    }

    public void init(){ }

    public void step(){ }

    public final void requestStop(){
        isStopRequested = true;
    }

}
