package com.deltarobotics9351.deltasystem.subsystems;

import com.deltarobotics9351.deltasystem.utils.Hardware;
import com.deltarobotics9351.deltasystem.utils.HardwareType;
import com.deltarobotics9351.deltasystem.DeltaOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class MotionSubSystem extends SubSystem{

    public MotionSubSystem(HardwareMap hdwMap, String name, Telemetry telemetry, DeltaOpMode currentOpMode){
        super(hdwMap, name, telemetry, currentOpMode);
    }

    private HashMap<String, DcMotor> motors;
    private HashMap<String, Servo> servos;
    private HashMap<String, CRServo> crservos;

    @Override
    public final void registerHardware(String name, HardwareType type){
        hardware.add(new Hardware(name, type));
    }

    public final DcMotor getMotor(String name){
        return motors.get(name);
    }

    public final Servo getServo(String name){
        return servos.get(name);
    }


    public final CRServo getCRServo(String name){
        return crservos.get(name);
    }


    @Override
    public final void init(){
        if(!isInitialized) {
            for (Hardware hdw : hardware) {
                if (hdw.type == HardwareType.DcMotor) {
                    motors.put(hdw.name, hdwMap.get(DcMotor.class, hdw.name));
                } else if (hdw.type == HardwareType.Servo) {
                    servos.put(hdw.name, hdwMap.get(Servo.class, hdw.name));
                } else if (hdw.type == HardwareType.CRServo) {
                    crservos.put(hdw.name, hdwMap.get(CRServo.class, hdw.name));
                } else {
                    return;
                }
            }
            _init();
            isInitialized = true;
        }
    }

    public void _init(){ }

    @Override
    public void step(){

    }

}
